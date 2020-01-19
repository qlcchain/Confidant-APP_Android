package com.stratagile.pnrouter.ui.activity.user

import android.graphics.*
import android.os.Bundle
import android.os.Environment
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.longToast
import com.smailnet.eamil.Utils.AESCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.RouterCodeData
import com.stratagile.pnrouter.ui.activity.user.component.DaggerUserAccoutCodeComponent
import com.stratagile.pnrouter.ui.activity.user.contract.UserAccoutCodeContract
import com.stratagile.pnrouter.ui.activity.user.module.UserAccoutCodeModule
import com.stratagile.pnrouter.ui.activity.user.presenter.UserAccoutCodePresenter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_useraccoutcode.*
import java.io.File
import java.io.FileOutputStream

import javax.inject.Inject;
import kotlin.concurrent.thread

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/12 19:25:26
 */

class UserAccoutCodeActivity : BaseActivity(), UserAccoutCodeContract.View {

    @Inject
    internal lateinit var mPresenter: UserAccoutCodePresenter
    lateinit var routerEntity: RouterEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_useraccoutcode)
    }
    override fun initData() {
        title.text = getString(R.string.Login_on_a_New_Device)
        routerEntity = intent.getParcelableExtra("router")
        tvUserName.text = SpUtil.getString(this, ConstantValue.username, "")
        tvRouterName.text = "【" + routerEntity.routerName + "】"
        adminName.text = getString(R.string.Circle_Owner)+ String(RxEncodeTool.base64Decode(routerEntity.adminName))
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        ivAvatar.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        ivAvatar2.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))+".jpg"
        val lastFile = File(PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name, "")
        var bitmapAvatarUser : Bitmap? = null
        if (lastFile.exists()) {
            bitmapAvatarUser = getRoundedCornerBitmap(BitmapFactory.decodeFile(lastFile.path))
        }
        ivAvatar.setImageFile(fileBase58Name)
        ivAvatar2.setImageFile(fileBase58Name)
        Thread(Runnable() {
            run() {

                val selfNickNameBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
                var  bitmap: Bitmap? = null
                if (bitmapAvatarUser != null) {

                    bitmap =   QRCodeEncoder.syncEncodeQRCode("type_3,"+ConstantValue.libsodiumprivateSignKey+","+ConstantValue.currentRouterSN+","+selfNickNameBase64, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor), bitmapAvatarUser)

                } else {
                    bitmap =   QRCodeEncoder.syncEncodeQRCode("type_3,"+ConstantValue.libsodiumprivateSignKey+","+ConstantValue.currentRouterSN+","+selfNickNameBase64, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor))
                }
                runOnUiThread {
                    ivQrCodeMy.setImageBitmap(bitmap!!)
                }

            }
        }).start()
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
        tvShare.setOnClickListener {
            saveQrCodeToPhone()
        }
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
    //生成圆角图片
    fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
        var offWidth = 0
        val roundPx = resources.getDimension(R.dimen.x10)
        val widht = resources.getDimension(R.dimen.x20).toInt()
        val output = Bitmap.createBitmap(bitmap.width +offWidth, bitmap.height+ offWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width+offWidth, bitmap.height+offWidth)
        val rect1 = Rect(widht / 4, widht / 4, bitmap.width + widht / 4+offWidth, bitmap.height + widht / 4+offWidth)
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
    fun saveQrCodeToPhone() {
        showProgressDialog()
        thread {
            val dView = cardView
            dView.isDrawingCacheEnabled = true
            dView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(dView.drawingCache)
            if (bitmap != null) {
                try {
                    var galleryPath = (Environment.getExternalStorageDirectory().toString()
                            + File.separator + Environment.DIRECTORY_DCIM
                            + File.separator + "Confidant" + File.separator)
                    val galleryPathFile = File(galleryPath)
                    if (!galleryPathFile.exists()) {
                        galleryPathFile.mkdir()
                    }
                    // 图片文件路径
                    var username = SpUtil.getString(this, ConstantValue.username, "")
                    val filePath = galleryPath+ username + ".png"
                    val file = File(filePath)
                    val os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    runOnUiThread {
                        closeProgressDialog()
                        longToast(getString(R.string.save_to_phone_success) + "\n" + filePath)
                    }
                    AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, filePath, System.currentTimeMillis())
                    KLog.i("存储完成")
                } catch (e: Exception) {
                }

            }

            val dView2 = cardView2
            dView2.isDrawingCacheEnabled = true
            dView2.buildDrawingCache()
            val bitmap2 = Bitmap.createBitmap(dView2.drawingCache)
            if (bitmap2 != null) {
                try {
                    // 获取内置SD卡路径
                    var galleryPath = (Environment.getExternalStorageDirectory().toString()
                            + File.separator + Environment.DIRECTORY_DCIM
                            + File.separator + "Confidant" + File.separator)
                    val galleryPathFile = File(galleryPath)
                    if (!galleryPathFile.exists()) {
                        galleryPathFile.mkdir()
                    }
                    // 图片文件路径
                    val filePath = galleryPath + routerEntity.routerName + ".png"
                    val file = File(filePath)
                    val os = FileOutputStream(file)
                    bitmap2.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    runOnUiThread {
                        closeProgressDialog()
                        longToast(getString(R.string.save_to_phone_success) + "\n" + filePath)
                    }
                    AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, filePath, System.currentTimeMillis())
                    KLog.i("存储完成")
                } catch (e: Exception) {
                }

            }
        }
    }
    override fun setupActivityComponent() {
        DaggerUserAccoutCodeComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .userAccoutCodeModule(UserAccoutCodeModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: UserAccoutCodeContract.UserAccoutCodeContractPresenter) {
        mPresenter = presenter as UserAccoutCodePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}