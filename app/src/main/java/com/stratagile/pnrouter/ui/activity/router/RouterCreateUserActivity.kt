package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.CreateNormalUserReq
import com.stratagile.pnrouter.entity.JCreateNormalUserRsp
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterCreateUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterCreateUserContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterCreateUserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterCreateUserPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.baseDataToJson
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_adduser.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 17:59:39
 */

class RouterCreateUserActivity : BaseActivity(), RouterCreateUserContract.View, PNRouterServiceMessageReceiver.CreateUserCallBack {
    override fun createUser(jCreateNormalUserRsp: JCreateNormalUserRsp) {
        runOnUiThread {

            closeProgressDialog()
            finish()
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
        title.text = getString(R.string.Createuseraccounts)
        routerEntity = intent.getParcelableExtra("routerUserEntity")

        AppConfig.instance.messageReceiver!!.createUserCallBack = this

        registerUserBtn.setOnClickListener {
            if (mnemonic.text.toString().equals("") || IdentifyCode.text.toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if ( IdentifyCode.text.toString().length <8) {
                toast(getString(R.string.needs8))
                return@setOnClickListener
            }
            showProgressDialog("waiting...")
            val NickName = RxEncodeTool.base64Encode2String(mnemonic.text.toString().toByteArray())
            var IdentifyCode = IdentifyCode.text.toString()
            var createNormalUser = CreateNormalUserReq(routerEntity.routerId,routerEntity.userId, NickName,IdentifyCode)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,createNormalUser))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(2,createNormalUser)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
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
    }
}