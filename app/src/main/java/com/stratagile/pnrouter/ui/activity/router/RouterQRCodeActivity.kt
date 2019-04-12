package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.pawegio.kandroid.i
import com.pawegio.kandroid.longToast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.RouterCodeData
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterQRCodeComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterQRCodeModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterQRCodePresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_router_qrcode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/09/26 11:53:16
 */

class RouterQRCodeActivity : BaseActivity(), RouterQRCodeContract.View {

    @Inject
    internal lateinit var mPresenter: RouterQRCodePresenter
    lateinit var routerEntity: RouterEntity
    //lateinit var createEnglishQRCode : ThreadUtil.Companion.CreateEnglishQRCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_qrcode)
    }
    override fun initData() {
        EventBus.getDefault().register(this)
        title.text = resources.getString(R.string.qr_code_business_card)
        routerEntity = intent.getParcelableExtra("router")
        tvRouterName.text = "【" + routerEntity.routerName + "】"
        adminName.text = getString(R.string.Circle_Owner)+ String(RxEncodeTool.base64Decode(routerEntity.adminName))
        tvRouterInvitationInfo.text = "\n" + "This is an invitation to join this circle"
        ivAvatarUser.withShape = true
        ivAvatarUser.setImageFile("", routerEntity.routerName)
        tvShare2.setOnClickListener {

            cardView2.setDrawingCacheEnabled(true)
            cardView2.buildDrawingCache();
            val bitmapPic = Bitmap.createBitmap(cardView2.getDrawingCache())
            if(bitmapPic != null)
            {
                var dir = ConstantValue.localPath + "/RA/" + routerEntity.userSn + ".jpg"
                var share_intent = Intent()
                share_intent.action = Intent.ACTION_SEND//设置分享行为
                share_intent.type = "image/*"  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "share")
                startActivity(share_intent)
            }
            //PopWindowUtil.showSharePopWindow(this, tvShare2)

        }
       /* createEnglishQRCode = ThreadUtil.Companion.CreateEnglishQRCode(userEntity.routerId, ivQrCode2)
        createEnglishQRCode.execute()*/
        var routerCodeData: RouterCodeData = RouterCodeData();
        routerCodeData.id = "010001".toByteArray()
        routerCodeData.routerId = routerEntity.routerId.toByteArray()
        routerCodeData.userSn = routerEntity.userSn.toByteArray()
        var routerCodeDataByte = routerCodeData.toByteArray();
        var base64Str = AESCipher.aesEncryptBytesToBase64(routerCodeDataByte,"welcometoqlc0101".toByteArray())
        Thread(Runnable() {
            run() {
                var bitMapAvatar =  getRoundedCornerBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode("type_1,"+base64Str, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor), bitMapAvatar)
                runOnUiThread {
                    ivQrCode2.setImageBitmap(bitmap)
                }

            }
        }).start()
        tvSaveToPhone2.setOnClickListener {
            saveQrCodeToPhone()
        }
    }

    //生成圆角图片
    fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
        val roundPx = resources.getDimension(R.dimen.x10)
        val widht = resources.getDimension(R.dimen.x20).toInt()
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rect1 = Rect(widht / 4, widht / 4, bitmap.width + widht / 4, bitmap.height + widht / 4)
        val rectF = RectF(rect)
        val rectF1 = RectF(rect1)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = resources.getColor(R.color.white)


        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect1, rect1, paint)


        return output
    }

    fun getRoundedCornerBitmap1(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rect1 = Rect(0 + 60, 0 + 60, bitmap.width  - 60, bitmap.height  - 60)
        val rectF = RectF(rect)
        val rectF1 = RectF(rect1)
        val roundPx = resources.getDimension(R.dimen.x80)

        paint.isAntiAlias = true
        canvas.drawColor(resources.getColor(R.color.white))
        paint.color = resources.getColor(R.color.black)


        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        //目标图像
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        //原图像
        canvas.drawBitmap(bitmap, rect, rect, paint)

//        canvas.drawRoundRect(rectF1, roundPx, roundPx, paint)


        return output
    }

    fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap !== source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        squaredBitmap.recycle()
        return bitmap
    }

    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun setupActivityComponent() {
       DaggerRouterQRCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerQRCodeModule(RouterQRCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterQRCodeContract.RouterQRCodeContractPresenter) {
            mPresenter = presenter as RouterQRCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    fun saveQrCodeToPhone() {
        showProgressDialog()
        thread {
            val dView = cardView2
            dView.isDrawingCacheEnabled = true
            dView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(dView.drawingCache)
            if (bitmap != null) {
                try {
                    // 获取内置SD卡路径
                    val sdCardPath = Environment.getExternalStorageDirectory().getPath() + ConstantValue.localPath
                    // 图片文件路径
                    val filePath = sdCardPath + File.separator + routerEntity.routerName + ".png"
                    val file = File(filePath)
                    val os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    runOnUiThread {
                        closeProgressDialog()
                        longToast(getString(R.string.save_to_phone_success) + "\n" + filePath)
                    }
                    KLog.i("存储完成")
                } catch (e: Exception) {
                }

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            cardView2.setDrawingCacheEnabled(true);
            cardView2.buildDrawingCache();
            val bitmapPic = Bitmap.createBitmap(cardView2.getDrawingCache())
            if(bitmapPic != null)
            {
                var dir = ConstantValue.localPath + "/RA/" + routerEntity.userSn + ".jpg"
                var share_intent = Intent()
                share_intent.action = Intent.ACTION_SEND//设置分享行为
                share_intent.type = "image/*"  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "share")
                startActivity(share_intent)
            }
            //PopWindowUtil.showSharePopWindow(this, tvShare2)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share_self, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}