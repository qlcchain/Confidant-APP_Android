package com.stratagile.pnrouter.ui.activity.user

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
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.user.component.DaggerCircleMemberDetailComponent
import com.stratagile.pnrouter.ui.activity.user.contract.CircleMemberDetailContract
import com.stratagile.pnrouter.ui.activity.user.module.CircleMemberDetailModule
import com.stratagile.pnrouter.ui.activity.user.presenter.CircleMemberDetailPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.TimeUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_circlemember_detail.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/17 10:21:23
 */

class CircleMemberDetailActivity : BaseActivity(), CircleMemberDetailContract.View , PNRouterServiceMessageReceiver.RemoveMemberCallBack {
    override fun removeMember(jRemoveMemberRsp: JRemoveMemberRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        when(jRemoveMemberRsp.params.retCode)
        {
            0->
            {
                runOnUiThread {
                    toast(R.string.success)
                }
                val intent = Intent()
                intent.putExtra("result", "1")
                setResult(RESULT_OK, intent)
                finish()
            }
            1->
            {
                runOnUiThread {
                    toast(R.string.No_authority)
                }
            }
            2->
            {
                runOnUiThread {
                    toast(R.string.uid_error)
                }
            }
            3->
            {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
        }


    }

    @Inject
    internal lateinit var mPresenter: CircleMemberDetailPresenter
    lateinit var routerUserEntity: RouterUserEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_circlemember_detail)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.removeMemberCallBack = this
        title.text = resources.getString(R.string.Contact_Details)
        routerUserEntity = intent.getParcelableExtra("user")

        var nickNameSouce = ""
        if(!routerUserEntity.nickName.equals(""))
        {
            nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.nickName))
            nickName.tvContent.text = nickNameSouce
        }else{
            nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic))
            nickName.tvContent.text = nickNameSouce
        }
        if(routerUserEntity.userId == null || routerUserEntity.userId.equals("") || nickNameSouce.equals("tempUser"))
        {
            removeMember.visibility = View.GONE
        }else{
            removeMember.visibility = View.VISIBLE
        }
        if(routerUserEntity.createTime != null && routerUserEntity.createTime != 0)
        {
            joinTime.tvContent.text = TimeUtil.getCreateTime(routerUserEntity.createTime.toLong())
            joinTime.visibility = View.VISIBLE
        }else{
            joinTime.visibility = View.GONE
        }
        if(!routerUserEntity.nickName.equals(""))
        {
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.nickName))
            ivAvatar.setText(nickNameSouce)
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic))
            ivAvatar.setText(nickNameSouce)
        }
        removeMember.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setTitleText(getString(R.string.leave)).
                            setContentText(getString(R.string.delete_user_ask))
                    .setConfirmClickListener {
                        showProgressDialog(getString(R.string.waiting))
                        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var regeister = DelUserReq(userId!!,routerUserEntity.userId,routerUserEntity.userSN)
                        if(ConstantValue.isWebsocketConnected)
                        {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                        }
                        else if(ConstantValue.isToxConnected)
                        {
                            var baseData = BaseData(4,regeister)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            if (ConstantValue.isAntox) {
                                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                            }
                        }
                    }
                    .show()

        }
    }

    override fun setupActivityComponent() {
        DaggerCircleMemberDetailComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .circleMemberDetailModule(CircleMemberDetailModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: CircleMemberDetailContract.CircleMemberDetailContractPresenter) {
        mPresenter = presenter as CircleMemberDetailPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.removeMemberCallBack = null
        super.onDestroy()
    }
}