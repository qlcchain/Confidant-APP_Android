package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerUserQRCodeComponent
import com.stratagile.pnrouter.ui.activity.router.contract.UserQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.module.UserQRCodeModule
import com.stratagile.pnrouter.ui.activity.router.presenter.UserQRCodePresenter
import kotlinx.android.synthetic.main.activity_user_qrcode.*
import javax.inject.Inject
import android.content.Intent.ACTION_SEND
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/10 17:38:20
 */

class UserQRCodeActivity : BaseActivity(), UserQRCodeContract.View {

    @Inject
    internal lateinit var mPresenter: UserQRCodePresenter
    lateinit var routerUserEntity: RouterUserEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_user_qrcode)
    }
    override fun initData() {
        title.text = resources.getString(R.string.qr_code_business_card)
        routerUserEntity = intent.getParcelableExtra("user")
        if(intent.hasExtra("mnemonic"))
        {
            var mnemonic = intent.getStringExtra("mnemonic");
            hiTips.visibility = View.VISIBLE
            hiTips.text = "Hi, "+String(RxEncodeTool.base64Decode(mnemonic));
        }else{
            hiTips.visibility = View.INVISIBLE
        }
        EventBus.getDefault().register(this)
//        tvShareUser.setOnClickListener {
//
//            cardViewUser.setDrawingCacheEnabled(true);
//            cardViewUser.buildDrawingCache();
//            val bitmapPic = Bitmap.createBitmap(cardViewUser.getDrawingCache())
//            if(bitmapPic != null)
//            {
//                var dir = ConstantValue.localPath + "/RA/" + routerUserEntity.userSN + ".jpg"
//                var share_intent = Intent()
//                share_intent.action = Intent.ACTION_SEND//设置分享行为
//                share_intent.type = "image/*"  //设置分享内容的类型
//                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
//                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                //创建分享的Dialog
//                share_intent = Intent.createChooser(share_intent, "share")
//                startActivity(share_intent)
//            }
//            //PopWindowUtil.showSharePopWindow(this, tvShareUser)
//        }
        if(routerUserEntity.nickName !=null  &&  !routerUserEntity.nickName.equals(""))
        {
//            tvRouterNameUser.text = String(RxEncodeTool.base64Decode(routerUserEntity.nickName))
            ivAvatarUser.setText(String(RxEncodeTool.base64Decode(routerUserEntity.nickName)))
            ivAvatarUser.setImageFile(String(RxEncodeTool.base64Decode(routerUserEntity.nickName)))
        }else if(routerUserEntity.mnemonic !=null  &&  !routerUserEntity.mnemonic.equals(""))
        {
//            tvRouterNameUser.text = String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic))
            ivAvatarUser.setText(String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic)))
            ivAvatarUser.setImageFile(String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic)))
        }
        var selectedRouter : RouterEntity
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
        }
        /* createEnglishQRCode = ThreadUtil.Companion.CreateEnglishQRCode(routerUserEntity.routerId, ivQrCode2)
         createEnglishQRCode.execute()*/
        Thread(Runnable() {
            run() {

                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode(routerUserEntity.qrcode, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor))
                runOnUiThread {
                    ivQrCode2.setImageBitmap(bitmap)
                }

            }
        }).start()
       /* tvSaveToPhoneUser.setOnClickListener {
            saveQrCodeToPhone()
        }*/
    }

    override fun setupActivityComponent() {
       DaggerUserQRCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .userQRCodeModule(UserQRCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: UserQRCodeContract.UserQRCodeContractPresenter) {
            mPresenter = presenter as UserQRCodePresenter
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