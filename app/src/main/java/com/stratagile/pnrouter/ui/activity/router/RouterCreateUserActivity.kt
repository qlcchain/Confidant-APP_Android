package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterCreateUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterCreateUserContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterCreateUserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterCreateUserPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_adduser.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 17:59:39
 */

class RouterCreateUserActivity : BaseActivity(), RouterCreateUserContract.View, PNRouterServiceMessageReceiver.CreateUserCallBack {
    override fun pullTmpAccount(jPullTmpAccountRsp: JPullTmpAccountRsp) {
        if(jPullTmpAccountRsp.params.retCode == 0)
        {
            runOnUiThread {
                var intent = Intent(this, UserQRCodeActivity::class.java)
                var routerUserEntity = RouterUserEntity()
                routerUserEntity.userSN = jPullTmpAccountRsp.params.userSN
                routerUserEntity.userType = 1
                routerUserEntity.active = 0
                routerUserEntity.identifyCode = "0"
                routerUserEntity.mnemonic = RxEncodeTool.base64Encode2String(mnemonic.text.toString().trim().toByteArray())
                routerUserEntity.nickName = ""
                routerUserEntity.userId = ""
                routerUserEntity.lastLoginTime = 0
                routerUserEntity.qrcode = jPullTmpAccountRsp.params.qrcode
                intent.putExtra("user", routerUserEntity)
                startActivity(intent)
                closeProgressDialog()
            }
        }else{
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.error)
            }
        }
    }

    override fun createUser(jCreateNormalUserRsp: JCreateNormalUserRsp) {
        if(jCreateNormalUserRsp.params.retCode == 0)
        {
            runOnUiThread {
                var intent = Intent(this, UserQRCodeActivity::class.java)
                var routerUserEntity = RouterUserEntity()
                routerUserEntity.userSN = jCreateNormalUserRsp.params.userSN
                routerUserEntity.userType = 2
                routerUserEntity.active = 0
                routerUserEntity.identifyCode = "0"
                routerUserEntity.mnemonic = RxEncodeTool.base64Encode2String(mnemonic.text.toString().trim().toByteArray())
                routerUserEntity.nickName = ""
                routerUserEntity.userId = ""
                routerUserEntity.lastLoginTime = 0
                routerUserEntity.qrcode = jCreateNormalUserRsp.params.qrcode
                intent.putExtra("user", routerUserEntity)
                intent.putExtra("mnemonic", RxEncodeTool.base64Encode2String(mnemonic.text.toString().trim().toByteArray()))
                startActivity(intent)
                closeProgressDialog()
                finish()
            }
        }else{
            if(jCreateNormalUserRsp.params.retCode == 1)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.rid_error)
                }
            }else if(jCreateNormalUserRsp.params.retCode == 2)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.No_authority)
                }
            }else{
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Users_have_reached_the_upper_limit)
                }
            }
        }

    }

    @Inject
    internal lateinit var mPresenter: RouterCreateUserPresenter
    lateinit var routerEntity: RouterEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adduser)
    }
    override fun initData() {
        title.text = "Add a New Member"
        routerEntity = intent.getParcelableExtra("routerUserEntity")

        AppConfig.instance.messageReceiver!!.createUserCallBack = this
        EventBus.getDefault().register(this)
        registerUserBtn.setOnClickListener {
            if (mnemonic.text.toString().trim().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
//            if ( IdentifyCode.text.toString().trim().length <8) {
//                toast(getString(R.string.needs8))
//                return@setOnClickListener
//            }
            showProgressDialog("waiting...")
            val NickName = RxEncodeTool.base64Encode2String(mnemonic.text.toString().trim().toByteArray())
//            var IdentifyCode = IdentifyCode.text.toString().trim()
            var createNormalUser = CreateNormalUserReq(routerEntity.routerId,routerEntity.userId, NickName,"")
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,createNormalUser))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(2,createNormalUser)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                }
            }

        }
        tempQrcode.setOnClickListener {

            val htmlStr = "<big>" + getString(R.string.Caution) + " </big>" +"<br>"+ getString(R.string.Cautiontips)
            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setContentText(htmlStr)
                    .setConfirmClickListener {
                        showProgressDialog("waiting...")
                        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
//            var IdentifyCode = IdentifyCode.text.toString().trim()
                        var pullTmpAccountReq = PullTmpAccountReq(userId!!)
                        if(ConstantValue.isWebsocketConnected)
                        {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,pullTmpAccountReq))
                        }
                        else if(ConstantValue.isToxConnected)
                        {
                            var baseData = BaseData(4,pullTmpAccountReq)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            if (ConstantValue.isAntox) {
                                //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                            }
                        }
                    }
                    .show()

        }

        mnemonic.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!"".equals(p0?.toString())) {
                    registerUserBtn.background = resources.getDrawable(R.drawable.btn_maincolor)
                } else {
                    registerUserBtn.background = resources.getDrawable(R.drawable.btn_d5d5d5)
                }
            }

        })
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
       DaggerRouterCreateUserComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerCreateUserModule(RouterCreateUserModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterCreateUserContract.RouterCreateUserContractPresenter) {
            mPresenter = presenter as RouterCreateUserPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.createUserCallBack = null
        EventBus.getDefault().unregister(this)
    }
}