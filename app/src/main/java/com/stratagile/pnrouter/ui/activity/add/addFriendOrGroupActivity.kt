package com.stratagile.pnrouter.ui.activity.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullTmpAccountRsp
import com.stratagile.pnrouter.entity.PullTmpAccountReq
import com.stratagile.pnrouter.ui.activity.add.component.DaggeraddFriendOrGroupComponent
import com.stratagile.pnrouter.ui.activity.add.contract.addFriendOrGroupContract
import com.stratagile.pnrouter.ui.activity.add.module.addFriendOrGroupModule
import com.stratagile.pnrouter.ui.activity.add.presenter.addFriendOrGroupPresenter
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity
import com.stratagile.pnrouter.ui.activity.router.RouterQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.ShareTempQRCodeActivity
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.layout_add.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.add
 * @Description: $description
 * @date 2019/04/02 16:08:05
 */

class addFriendOrGroupActivity : BaseActivity(), addFriendOrGroupContract.View, PNRouterServiceMessageReceiver.PullTmpAccountBack {
    override fun pullTmpAccount(jPullTmpAccountRsp: JPullTmpAccountRsp) {
        if(jPullTmpAccountRsp.params.retCode == 0)
        {
            runOnUiThread {
                var intent = Intent(this, ShareTempQRCodeActivity::class.java)
                var routerUserEntity = RouterUserEntity()
                routerUserEntity.userSN = jPullTmpAccountRsp.params.userSN
                routerUserEntity.userType = 1
                routerUserEntity.active = 0
                routerUserEntity.identifyCode = "0"
                routerUserEntity.mnemonic = ""
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

    @Inject
    internal lateinit var mPresenter: addFriendOrGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.layout_add)
        setTitle(getString(R.string.button_add))
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.pullTmpAccountBack = this
        createGroup.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", "0")
            setResult(RESULT_OK, intent)
            finish()
        }
        newEmail.visibility = View.GONE
        if(AppConfig.instance.emailConfig().account != null)
        {
            newEmail.visibility = View.VISIBLE
        }
        newEmail.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", "3")
            setResult(RESULT_OK, intent)
            finish()
        }
        scanadd.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", "1")
            setResult(RESULT_OK, intent)
            finish()
        }
        sharecard.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", "2")
            setResult(RESULT_OK, intent)
            finish()
        }
        var routerEntity : RouterEntity
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        routerList.forEach {
            if (it.lastCheck) {
                routerEntity = it
                if(ConstantValue.currentRouterSN != null && ConstantValue.isCurrentRouterAdmin && ConstantValue.currentRouterSN.equals(routerEntity.userSn))
                {
                    //管理员
                    addNewMember.visibility = View.VISIBLE
                    addNewMember.setOnClickListener {
                       /* var intent = Intent(this, RouterQRCodeActivity::class.java)
                        intent.putExtra("router", routerEntity)
                        startActivity(intent)*/
                        var intent = Intent(this, RouterCreateUserActivity::class.java)
                        intent.putExtra("routerUserEntity", routerEntity)
                        startActivity(intent)
                       /* showProgressDialog("waiting...")
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
                                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                            }
                        }*/
                    }
                }else{
                    addNewMember.visibility = View.GONE
                }
                return@forEach
            }
        }
    }

    override fun setupActivityComponent() {
        DaggeraddFriendOrGroupComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .addFriendOrGroupModule(addFriendOrGroupModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: addFriendOrGroupContract.addFriendOrGroupContractPresenter) {
        mPresenter = presenter as addFriendOrGroupPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        try {
            AppConfig.instance.messageReceiver!!.pullTmpAccountBack = null
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}