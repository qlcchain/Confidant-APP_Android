package com.stratagile.pnrouter.ui.activity.chat

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.message.Message
import com.stratagile.pnrouter.ui.activity.chat.component.DaggerChatComponent
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.module.ChatModule
import com.stratagile.pnrouter.ui.activity.chat.presenter.ChatPresenter
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: $description
 * @date 2018/09/13 13:18:46
 */

class ChatActivity : BaseActivity(), ChatContract.View, PNRouterServiceMessageReceiver.ChatCallBack, ViewTreeObserver.OnGlobalLayoutListener {
    var statusBarHeight: Int = 0
    override fun onGlobalLayout() {
        var myLayout = getWindow().getDecorView();
        val r = Rect()
        // 使用最外层布局填充，进行测算计算
        parentLayout.getWindowVisibleDisplayFrame(r)
        val screenHeight = myLayout.getRootView().getHeight()
        val heightDiff = screenHeight - (r.bottom - r.top)
        if (heightDiff > 100) {
            // 如果超过100个像素，它可能是一个键盘。获取状态栏的高度
            statusBarHeight = 0
        }
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = Integer.parseInt(field.get(obj).toString())
            statusBarHeight = getResources().getDimensionPixelSize(x)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val realKeyboardHeight = heightDiff - statusBarHeight
        KLog.e("keyboard height(单位像素) = $realKeyboardHeight")
        if (realKeyboardHeight != 0) {
            SpUtil.putInt(this@ChatActivity, ConstantValue.realKeyboardHeight, realKeyboardHeight)
            parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this@ChatActivity)
        }
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {
        chatFragment?.delFreindMsg(delMsgPushRsp)
    }

    override fun delMsgRsp(delMsgRsp: JDelMsgRsp) {
        if (delMsgRsp.params.retCode == 0) {
            chatFragment?.delMyMsg(delMsgRsp)
        }
    }

    override fun pullMsgRsp(pushMsgRsp: JPullMsgRsp) {

        var messageList: List<Message> = pushMsgRsp.params.payload
        chatFragment?.refreshData(messageList)
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        if (pushMsgRsp.params.fromId.equals(toChatUserID)) {
            var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), 0, "")
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
            chatFragment?.receiveMessage(pushMsgRsp)
        }
    }

    override fun sendMsg(FromId: String, ToId: String, Msg: String) {
        var msgData = SendMsgReq(FromId!!, ToId!!, Msg)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
    }

    override fun sendMsgRsp(sendMsgRsp: JSendMsgRsp) {
        chatFragment?.upateMessage(sendMsgRsp)
        //todo
    }

    @Inject
    internal lateinit var mPresenter: ChatPresenter
    var activityInstance: ChatActivity? = null
    private var chatFragment: EaseChatFragment? = null
    internal var toChatUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        toChatUserID = intent.extras!!.getString(EaseConstant.EXTRA_USER_ID)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activityInstance = this
        //user or group id
        AppConfig.instance.isChatWithFirend = toChatUserID
        chatFragment = EaseChatFragment()
        //set arguments
        chatFragment?.setArguments(intent.extras)
        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment!!).commit()
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        view1.setLayoutParams(llp)
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(this@ChatActivity)
    }

    override fun initView() {
//        setContentView(R.layout.activity_chat)
    }

    override fun initData() {
        AppConfig.instance.messageReceiver!!.chatCallBack = this
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullMsgList = PullMsgReq(userId!!, toChatUserID!!, 1, 0, 10)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullMsgList))
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