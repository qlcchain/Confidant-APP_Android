package com.stratagile.pnrouter.ui.activity.chat

import android.content.Intent
import android.os.Bundle
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.chat.component.DaggerChatComponent
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.module.ChatModule
import com.stratagile.pnrouter.ui.activity.chat.presenter.ChatPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: $description
 * @date 2018/09/13 13:18:46
 */

class ChatActivity : BaseActivity(), ChatContract.View, PNRouterServiceMessageReceiver.ChatCallBack {
    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        if(pushMsgRsp.params.fromId.equals(toChatUserID))
        {
            var msgData = PushMsgReq( Integer.valueOf(pushMsgRsp?.params.msgId), 0,"")
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
            chatFragment?.receiveMessage(pushMsgRsp)
        }
    }

    override fun sendMsg(FromId: String, ToId: String, Msg: String) {
        var msgData = SendMsgReq( FromId!!, ToId!!, Msg)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
    }

    override fun sendMsgRsp(sendMsgRsp: JSendMsgRsp) {
        var aa = sendMsgRsp;
        //todo
    }

    @Inject
    internal lateinit var mPresenter: ChatPresenter
    var activityInstance: ChatActivity? = null
    private var chatFragment: EaseChatFragment? = null
    internal var toChatUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activityInstance = this
        //user or group id


        toChatUserID = intent.extras!!.getString(EaseConstant.EXTRA_USER_ID)
        AppConfig.instance.isChatWithFirend = toChatUserID
        chatFragment = EaseChatFragment()
        //set arguments
        chatFragment?.setArguments(intent.extras)
        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment!!).commit()
    }

    override fun initView() {
//        setContentView(R.layout.activity_chat)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.chatCallBack = this
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.chatCallBack = null
        AppConfig.instance.isChatWithFirend = null;
        activityInstance = null
    }

    override fun onNewIntent(intent: Intent) {
        // enter to chat activity when click notification bar, here make sure only one chat activiy
        val username = intent.getStringExtra("userId")
        if (toChatUserID == username)
            super.onNewIntent(intent)
        else {
            finish()
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        chatFragment?.onBackPressed()
    }

    fun getToChatUsername(): String {
        return toChatUserID!!
    }
    override fun setupActivityComponent() {
       DaggerChatComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .chatModule(ChatModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ChatContract.ChatContractPresenter) {
            mPresenter = presenter as ChatPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}