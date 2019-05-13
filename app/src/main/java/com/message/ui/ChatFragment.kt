package com.message.ui

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.easeui.domain.EaseEmojicon
import com.hyphenate.easeui.ui.EaseBaiduMapActivity
import com.hyphenate.easeui.widget.EaseChatExtendMenu
import com.hyphenate.easeui.widget.EaseChatInputMenu
import com.message.Message
import com.message.MessageProvider
import com.message.adapter.MessageListAdapter
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.SendMsgReq
import com.stratagile.pnrouter.entity.SendMsgReqV3
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

class ChatFragment : BaseFragment(), MessageProvider.ReceivedMessageListener {
    override fun receivedMessage(index : Int, message: Message) {
        runOnUiThread {
            messageListAdapter.notifyItemChanged(index)
        }
    }

    override fun receivedMessage() {
        runOnUiThread {
            messageListAdapter.notifyItemInserted(messageListAdapter.getData().size)
            messageRecyclerView.scrollToPosition(messageListAdapter.getData().size - 1)
        }
        MessageProvider.getInstance().clearUnreadCountByUserId(userEntity.userId)
    }

    lateinit var userEntity: UserEntity

    var inputManager: InputMethodManager? = null
    var clipboard: ClipboardManager? = null

    protected val REQUEST_CODE_MAP = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6

    protected val MSG_TYPING_BEGIN = 0
    protected val MSG_TYPING_END = 1

    internal val ITEM_PICTURE = 1
    internal val ITEM_TAKE_PICTURE = 2
    internal val ITEM_SHORTVIDEO = 3
    internal val ITEM_FILE = 4
    internal val ITEM_LOCATION = 5
    internal val ITEM_MEETING = 6
    internal val ITEM_VIDEOCALL = 7
    internal val ITEM_PRIVATEFILE = 8

    internal var extendMenuItemClickListener: MyItemClickListener? = null

    lateinit var messageListAdapter: MessageListAdapter

    var messageList : ArrayList<Message>? = null

    protected var itemStrings = intArrayOf(R.string.attach_picture, R.string.attach_take_pic, R.string.attach_Short_video, R.string.attach_file, R.string.attach_location, R.string.attach_Meeting, R.string.attach_Video_call, R.string.attach_Privatefile)
    protected var itemdrawables = intArrayOf(R.drawable.ease_chat_image_selector, R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_shortvideo_selector, R.drawable.ease_chat_localdocument_selector, R.drawable.ease_chat_location_selector, R.drawable.ease_chat_meeting_selector, R.drawable.ease_chat_videocall_selector, R.drawable.ease_chat_pirvatedocument_selector)
    protected var itemIds = intArrayOf(ITEM_PICTURE, ITEM_TAKE_PICTURE, ITEM_SHORTVIDEO, ITEM_FILE, ITEM_LOCATION, ITEM_MEETING, ITEM_VIDEOCALL, ITEM_PRIVATEFILE)

    override fun setupFragmentComponent() {
        KLog.i("setupFragmentComponent")
    }

    override fun initDataFromLocal() {
        KLog.i("initDataFromLocal")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_chat, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userEntity = arguments!!.getParcelable(USER)
        var usernameSouce = String(RxEncodeTool.base64Decode(userEntity.nickName))
        if (userEntity.remarks != null && userEntity.remarks != "") {
            usernameSouce = String(RxEncodeTool.base64Decode(userEntity.remarks))
        }
        toolbar_parent.findViewById<TextView>(R.id.title).text = usernameSouce
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar_parent.findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
                if (it.id == -1) {
                    activity!!.finish()
                }
                true
            }
        }

        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(activity), UIUtils.getStatusBarHeight(activity))
        view2.setLayoutParams(llp)
        inputMenu.bindContentView(messageRecyclerView)
        registerExtendMenuItem()
        // init input menu
        inputMenu.init(null)
        MessageProvider.getInstance().receivedMessageListener = this
        inputMenu.setChatInputMenuListener(object : EaseChatInputMenu.ChatInputMenuListener {

           override fun onTyping(s: CharSequence, start: Int, before: Int, count: Int) {
                KLog.i("send action:TypingBegin cmd msg.")
            }
            override fun onSendMessage(content: String,point :String ) {
                sendTextMessage(content)
            }
            override fun onSendMessage(content: String) {
                sendTextMessage(content)
            }
            override fun onPressToSpeakBtnTouch(v: View, event: MotionEvent): Boolean {
                KLog.i("onPressToSpeakBtnTouch")
                return true
//                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, EaseVoiceRecorderView.EaseVoiceRecorderCallback { voiceFilePath, voiceTimeLength -> sendVoiceMessage(voiceFilePath, voiceTimeLength) })
            }

            override fun onBigExpressionClicked(emojicon: EaseEmojicon) {
                KLog.i("onBigExpressionClicked")
//                sendBigExpressionMessage(emojicon.name, emojicon.identityCode)
            }
        })

        inputMenu.setChatMenuOpenListenter {
            messageRecyclerView.postDelayed(Runnable {
                messageRecyclerView.scrollToPosition(messageListAdapter.getData().size - 1)
            }, 100)
        }

        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                inputMenu.hideExtendMenuContainer()
                inputMenu.hideSoftInput()
            }
        })

        messageRecyclerView.setOnClickListener {
            KLog.i("点击了messageRecyclerView")
            inputMenu.hideExtendMenuContainer()
            inputMenu.hideSoftInput()
        }

        (messageRecyclerView.getItemAnimator() as SimpleItemAnimator).setSupportsChangeAnimations(false)

        inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        messageList = MessageProvider.getInstance().getMessageListbyUserId(userEntity.userId)
        if (messageList == null) {
            messageList = arrayListOf()
        }
        MessageProvider.getInstance().clearUnreadCountByUserId(userEntity.userId)
//        for (i in 0..10) {
//            var message = Message()
//            message.type = Message.Type.TXT
//            message.to = ""
//            message.msg = "" + i * 1000
//            message.status = Message.Status.SUCCESS
//            message.timeStatmp = Calendar.getInstance().timeInMillis
//            if (i % 2 != 0) {
//                message.from = SpUtil.getString(activity!!, ConstantValue.userId, "")
//            } else {
//                message.from = ""
//            }
//            easeChatMessageList?.add(message)
//        }
        if (messageList != null) {
            messageListAdapter = MessageListAdapter(activity!!, messageList!!)
        }
        messageRecyclerView.adapter = messageListAdapter
        messageRecyclerView.scrollToPosition(messageListAdapter.getData().size - 1)
//        messageListAdapter.setNewData(easeChatMessageList)
    }

    protected fun registerExtendMenuItem() {
        for (i in itemStrings.indices) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener)
        }
    }

    fun onBackPressed() {
        if (inputMenu.onBackPressed()) {
            activity!!.finish()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageProvider.getInstance().receivedMessageListener = null
    }



    fun sendTextMessage(content: String) {
        var message = Message()
        message.type = Message.Type.TXT
        message.to = userEntity.userId
        message.from = SpUtil.getString(activity!!, ConstantValue.userId, "")
        message.msg = content
        message.status = 4
        message.timeStamp = Calendar.getInstance().timeInMillis / 1000
        messageListAdapter.addData(message)
        messageRecyclerView.scrollToPosition(messageListAdapter.getData().size - 1)
        KLog.i(messageList?.size)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendMsgV3(SpUtil.getString(activity!!, ConstantValue.userId, "")!!, userEntity.userId,userEntity.miPublicKey, content)
        }else{
            sendMsg(SpUtil.getString(activity!!, ConstantValue.userId, "")!!, userEntity.userId,userEntity.signPublicKey, content)
        }

    }

    fun sendMsg(FromId: String, ToId: String,FriendPublicKey :String, Msg: String) {
        try {
            var len = Msg.toCharArray()
            if(len.size >ConstantValue.sendMaxSize)
            {
                toast(R.string.nomorecharacters)
                return
            }
            var aesKey =  RxEncryptTool.generateAESKey()
            LogUtil.addLog("sendMsg2 aesKey:",aesKey)
            var my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
            LogUtil.addLog("sendMsg2 myKey:",ConstantValue.publicRAS)
            var friend = RxEncodeTool.base64Decode(FriendPublicKey)
            LogUtil.addLog("sendMsg2 friendKey:",FriendPublicKey)
            var SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),my))
            LogUtil.addLog("sendMsg2 SrcKey:",SrcKey.toString())
            var DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),friend))
            LogUtil.addLog("sendMsg2 SrcKey:",SrcKey.toString())
            var miMsg = AESCipher.aesEncryptString(Msg,aesKey)
            LogUtil.addLog("sendMsg2 miMsg:",miMsg)
            var msgData = SendMsgReq(FromId!!, ToId!!, miMsg,String(SrcKey),String(DstKey))
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }catch (e:Exception)
        {
            LogUtil.addLog("sendMsg2 错误:",e.toString())
            toast(R.string.Encryptionerror)
        }
    }
    fun sendMsgV3(FromIndex: String, ToIndex: String, FriendMiPublicKey :String, Msg: String) {
        try {
            var len = Msg.toCharArray()
            if(len.size >ConstantValue.sendMaxSize)
            {
                toast(R.string.nomorecharacters)
                return
            }
            var friendMiPublic = RxEncodeTool.base64Decode(FriendMiPublicKey)
            LogUtil.addLog("sendMsgV3 friendKey:",FriendMiPublicKey)
            var msgMap = LibsodiumUtil.EncryptSendMsg(Msg,friendMiPublic)
            var msgData = SendMsgReqV3(FromIndex!!, ToIndex!!, msgMap.get("encryptedBase64")!!,msgMap.get("signBase64")!!,msgMap.get("NonceBase64")!!,msgMap.get("dst_shared_key_Mi_My64")!!)

            if (ConstantValue.curreantNetworkType.equals("WIFI")) {
                AppConfig.instance.getPNRouterServiceMessageSender().sendChatMsg(BaseData(3,msgData))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(3,msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }catch (e:Exception)
        {
            LogUtil.addLog("sendMsg2 错误:",e.toString())
            toast(R.string.Encryptionerror)
        }
    }
    companion object {
        private val USER = "user"

        fun newInstance(userEntity: UserEntity): ChatFragment {
            val args = Bundle()
            args.putParcelable(USER, userEntity)
            val fragment = ChatFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * handle the click event for extend menu
     *
     */
    internal inner class MyItemClickListener : EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        override fun onClick(itemId: Int, view: View) {
//            if (chatFragmentHelper != null) {
//                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
//                    return
//                }
//            }
            when (itemId) {
//                ITEM_PICTURE -> selectPicFromLocal()
                ITEM_TAKE_PICTURE -> AndPermission.with(AppConfig.instance)
                        .requestCode(101)
                        .permission(
                                Manifest.permission.CAMERA
                        )
                        .callback(permission)
                        .start()
                ITEM_SHORTVIDEO -> AndPermission.with(AppConfig.instance)
                        .requestCode(101)
                        .permission(
                                Manifest.permission.CAMERA
                        )
                        .callback(permissionVideo)
                        .start()
                ITEM_LOCATION -> startActivityForResult(Intent(activity, EaseBaiduMapActivity::class.java), REQUEST_CODE_MAP)
                ITEM_FILE -> startActivityForResult(Intent(activity, FileChooseActivity::class.java).putExtra("fileType", 2), REQUEST_CODE_FILE)
                else -> Toast.makeText(activity, R.string.wait, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val permission = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {

            // 权限申请成功回调。
            if (requestCode == 101) {
//                selectPicFromCamera()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")

            }
        }
    }
    private val permissionVideo = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {

            // 权限申请成功回调。
            if (requestCode == 101) {
//                selectVideoFromCamera()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")

            }
        }
    }
}