package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.router.component.DaggerShareTempQRCodeComponent
import com.stratagile.pnrouter.ui.activity.router.contract.ShareTempQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.module.ShareTempQRCodeModule
import com.stratagile.pnrouter.ui.activity.router.presenter.ShareTempQRCodePresenter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_router_sharetemp_qrcode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/04/17 14:04:59
 */

class ShareTempQRCodeActivity : BaseActivity(), ShareTempQRCodeContract.View {

    @Inject
    internal lateinit var mPresenter: ShareTempQRCodePresenter
    lateinit var routerUserEntity: RouterUserEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_sharetemp_qrcode)
    }
    override fun initData() {
        //title.text = resources.getString(R.string.qr_code_business_card)
        title.text = ""
        routerUserEntity = intent.getParcelableExtra("user")
        EventBus.getDefault().register(this)

        var type = routerUserEntity.qrcode.substring(0,6);
        var data = routerUserEntity.qrcode.substring(7,routerUserEntity.qrcode.length);
        var soureData:ByteArray =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
        if(type.equals("type_1")) {
            val keyId: ByteArray = ByteArray(6) //密钥ID
            val RouterId: ByteArray = ByteArray(76) //路由器id
            val UserSn: ByteArray = ByteArray(32)  //用户SN
            System.arraycopy(soureData, 0, keyId, 0, 6)
            System.arraycopy(soureData, 6, RouterId, 0, 76)
            System.arraycopy(soureData, 82, UserSn, 0, 32)
            var keyIdStr = String(keyId)
            var RouterIdStr = String(RouterId)
            var UserSnStr = String(UserSn)
            val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.RouterId.eq(RouterIdStr)).list()
            if (routerEntityList != null && routerEntityList!!.size != 0) {
                var routerEntity = routerEntityList.get(0)
                adminName.text = getString(R.string.Circle_Owner)+ String(RxEncodeTool.base64Decode(routerEntity.adminName))
                tvRouterName.text = "【" + routerEntity.routerName + "】"
            }else{
                adminName.text = ""
            }
        }

        tvRouterInvitationInfo.text = "\n" + "This is an invitation to join this circle"
        ivAvatarUser.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey)) + ".jpg"
        ivAvatarUser.setImageFile(fileBase58Name)
        ivAvatarUser.withShape = true
       /* var selectedRouter : RouterEntity
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        routerList.forEach {
            if (it.lastCheck) {
                tvRouterName.text = "【" + it.routerName + "】"
                ivAvatarUser.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
                var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey)) + ".jpg"
                ivAvatarUser.setImageFile(fileBase58Name)
                selectedRouter = it
                ivAvatarUser.withShape = true
                return@forEach
            }
        }*/
        /* createEnglishQRCode = ThreadUtil.Companion.CreateEnglishQRCode(routerUserEntity.routerId, ivQrCode2)
         createEnglishQRCode.execute()*/
        Thread(Runnable() {
            run() {
                var bitMapAvatar =  getRoundedCornerBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode(routerUserEntity.qrcode, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor),bitMapAvatar)
                runOnUiThread {
                    ivQrCode2.setImageBitmap(bitmap)
                }

            }
        }).start()
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
    override fun setupActivityComponent() {
       DaggerShareTempQRCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .shareTempQRCodeModule(ShareTempQRCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ShareTempQRCodeContract.ShareTempQRCodeContractPresenter) {
            mPresenter = presenter as ShareTempQRCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            cardView2.setDrawingCacheEnabled(true);
            cardView2.buildDrawingCache();
            var userId = FileUtil.getLocalUserData("userid")
            val bitmapPic = Bitmap.createBitmap(cardView2.getDrawingCache())
            if(bitmapPic != null)
            {
                var dir = ConstantValue.localPath + "/RA/" + userId + ".jpg"
                var share_intent = Intent()
                share_intent.action = Intent.ACTION_SEND//设置分享行为
                share_intent.type = "image/*"  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "share")
                startActivity(share_intent)
            }
            //PopWindowUtil.showSharePopWindow(this, tvShare)
        }
        return super.onOptionsItemSelected(item)
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