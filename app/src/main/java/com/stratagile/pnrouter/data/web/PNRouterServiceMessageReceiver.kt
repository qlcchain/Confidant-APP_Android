package com.stratagile.pnrouter.data.web

import com.alibaba.fastjson.JSONObject
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.utils.GsonUtil
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import java.io.IOException


class PNRouterServiceMessageReceiver
/**
 * Construct a PNRouterServiceMessageReceiver.
 *
 * @param urls The URL of the Signal Service.
 * @param credentials The Signal Service user's credentials.
 */
constructor(private val urls: SignalServiceConfiguration, private val credentialsProvider: CredentialsProvider, private val userAgent: String, private val connectivityListener: ConnectivityListener) : SignalServiceMessagePipe.MessagePipeCallback {

    override fun onMessage(baseData: BaseData, text: String?) {
//        KLog.i(baseData.baseDataToJson())
//        KLog.i(baseData.params.toString())
        var gson = GsonUtil.getIntGson()
        var paramsStr = (JSONObject.parseObject(baseData.baseDataToJson())).get("params").toString()
        var action = JSONObject.parseObject(paramsStr).getString("Action")
        if( ConstantValue.loginOut)
        {
            if(action.toString().contains("Recovery") || action.toString().contains("Register")|| action.toString().contains("Login")|| action.toString().contains("LogOut")|| action.toString().contains("RouterLogin")|| action.toString().contains("ResetRouterKey")|| action.toString().contains("ResetUserIdcode"))
            {
                when (action) {
                    "Recovery" -> {
                        val jRecoveryRsp = gson.fromJson(text, JRecoveryRsp::class.java)
                        KLog.i(jRecoveryRsp)
                        recoveryBackListener?.recoveryBack(jRecoveryRsp)
                        loginBackListener?.recoveryBack(jRecoveryRsp)
                        adminRecoveryCallBack?.recoveryBack(jRecoveryRsp)
                    }
                    "Register" -> {
                        val JRegisterRsp = gson.fromJson(text, JRegisterRsp::class.java)
                        KLog.i(JRegisterRsp)
                        registerListener?.registerBack(JRegisterRsp)
                    }
                    "Login" -> {
                        val loginRsp = gson.fromJson(text, JLoginRsp::class.java)
                        LogUtil.addLog("Login"+loginBackListener,"PNRouterServiceMessageReceiver")
                        KLog.i(loginRsp)
                        loginBackListener?.loginBack(loginRsp)
                        registerListener?.loginBack(loginRsp)
                    }
                    "LogOut" -> {
                        val JLogOutRsp = gson.fromJson(text, JLogOutRsp::class.java)
                        logOutBack?.logOutBack(JLogOutRsp)
                    }
                    //admin登陆
                    "RouterLogin" -> {
                        val JAdminLoginRsp = gson.fromJson(text, JAdminLoginRsp::class.java)
                        adminLoginCallBack?.login(JAdminLoginRsp)

                    }
                    //admin修改密码
                    "ResetRouterKey" -> {
                        val JAdminUpdataPasswordRsp = gson.fromJson(text, JAdminUpdataPasswordRsp::class.java)
                        adminUpdataPassWordCallBack?.updataPassWord(JAdminUpdataPasswordRsp)

                    }
                    //admin修改code
                    "ResetUserIdcode" -> {
                        val JAdminUpdataCodeRsp = gson.fromJson(text, JAdminUpdataCodeRsp::class.java)
                        adminUpdataCodeCallBack?.updataCode(JAdminUpdataCodeRsp)

                    }
                }
            }

        }else{
            when (action) {
                "Recovery" -> {
                    val jRecoveryRsp = gson.fromJson(text, JRecoveryRsp::class.java)
                    KLog.i(jRecoveryRsp)
                    recoveryBackListener?.recoveryBack(jRecoveryRsp)
                    loginBackListener?.recoveryBack(jRecoveryRsp)
                    adminRecoveryCallBack?.recoveryBack(jRecoveryRsp)
                }
                "Register" -> {
                    val JRegisterRsp = gson.fromJson(text, JRegisterRsp::class.java)
                    KLog.i(JRegisterRsp)
                    registerListener?.registerBack(JRegisterRsp)
                }
                "Login" -> {
                    val loginRsp = gson.fromJson(text, JLoginRsp::class.java)
                    LogUtil.addLog("Login"+loginBackListener,"PNRouterServiceMessageReceiver")
                    KLog.i(loginRsp)
                    loginBackListener?.loginBack(loginRsp)
                    registerListener?.loginBack(loginRsp)
                }
                "AddFriendReq" -> {
                    val addFreindRsp = gson.fromJson(text, JAddFreindRsp::class.java)
                    KLog.i(addFreindRsp.toString())
//                addfrendCallBack?.addFriendBack(addFreindRsp)
                    userControlleCallBack?.addFriendBack(addFreindRsp)
                }
                "UserInfoUpdate" -> {
                    val jUserInfoUpdateRsp = gson.fromJson(text, JUserInfoUpdateRsp::class.java)
                    KLog.i(jUserInfoUpdateRsp.toString())
                    uerInfoUpdateCallBack?.UserInfoUpdateCallBack(jUserInfoUpdateRsp)
                }
                "UserInfoPush" -> {
                    val jUserInfoPushRsp = gson.fromJson(text, JUserInfoPushRsp::class.java)
                    KLog.i(jUserInfoPushRsp.toString())
                    mainInfoBack?.userInfoPushRsp(jUserInfoPushRsp)
                }
                "LogOut" -> {
                    val JLogOutRsp = gson.fromJson(text, JLogOutRsp::class.java)
                    logOutBack?.logOutBack(JLogOutRsp)
                }

                //对方要加我为好友，服务器给我推送的好友请求
                "AddFriendPush" -> {
                    val addFreindPusRsp = gson.fromJson(text, JAddFriendPushRsp::class.java)
                    KLog.i(addFreindPusRsp.toString())
                    //mainInfoBack?.addFriendPushRsp(addFreindPusRsp)
                    userControlleCallBack?.addFriendPushRsp(addFreindPusRsp)
                }
                //添加好友，对方处理的结果的推送
                "AddFriendDeal" -> {
                    val addFriendDealRsp = gson.fromJson(text, JAddFriendDealRsp::class.java)
                    addFriendDealCallBack?.addFriendDealRsp(addFriendDealRsp)
                    userControlleCallBack?.addFriendDealRsp(addFriendDealRsp)
                }
                //添加好友的返回
                "AddFriendReply" -> {
                    val jAddFriendReplyRsp = gson.fromJson(text, JAddFriendReplyRsp::class.java)
                    mainInfoBack?.addFriendReplyRsp(jAddFriendReplyRsp)
                    userControlleCallBack?.addFriendReplyRsp(jAddFriendReplyRsp)
                }
                //删除对方，服务器返回是否操作成功
                "DelFriendCmd" -> {
                    val jDelFriendCmdRsp = gson.fromJson(text, JDelFriendCmdRsp::class.java)
                    userControlleCallBack!!.delFriendCmdRsp(jDelFriendCmdRsp)
                }
                //删除对方，服务器返回是否操作成功
                "ChangeRemarks" -> {
                    val jChangeRemarksRsp = gson.fromJson(text, JChangeRemarksRsp::class.java)
                    userControlleCallBack!!.changeRemarksRsp(jChangeRemarksRsp)
                }
                //对方删除我，服务器给我推送消息
                "DelFriendPush" -> {
                    val jDelFriendPushRsp = gson.fromJson(text, JDelFriendPushRsp::class.java)
                    mainInfoBack?.delFriendPushRsp(jDelFriendPushRsp)
                    //userControlleCallBack?.delFriendPushRsp(jDelFriendPushRsp)
                }
                //拉取好友列表
                "PullFriend" -> {
                    val jPullFriendRsp = gson.fromJson(text, JPullFriendRsp::class.java)
                    pullFriendCallBack?.firendList(jPullFriendRsp)
                    //userControlleCallBack?.firendList(jPullFriendRsp)
                }
                //拉取用户列表
                "PullUserList" -> {
                    val jPullUserRsp = gson.fromJson(text, JPullUserRsp::class.java)
                    pullUserCallBack?.userList(jPullUserRsp)
                    //userControlleCallBack?.firendList(jPullFriendRsp)
                }
                //创建用户
                "CreateNormalUser" -> {
                    val JCreateNormalUserRsp = gson.fromJson(text, JCreateNormalUserRsp::class.java)
                    createUserCallBack?.createUser(JCreateNormalUserRsp)
                    //userControlleCallBack?.firendList(jPullFriendRsp)
                }
                //发送消息服务器给的返回，代表消息服务器已经收到
                "SendMsg" -> {
                    val JSendMsgRsp = gson.fromJson(text, JSendMsgRsp::class.java)
                    chatCallBack?.sendMsgRsp(JSendMsgRsp)
                    convsationCallBack?.sendMsgRsp(JSendMsgRsp)
                }
                "QueryFriend" -> {
                    val jQueryFriendRsp = gson.fromJson(text, JQueryFriendRsp::class.java)
                    chatCallBack?.QueryFriendRep(jQueryFriendRsp)
                }
                //发送消息对方已读
                "ReadMsgPush" -> {
                    val JReadMsgPushRsp = gson.fromJson(text, JReadMsgPushRsp::class.java)
                    chatCallBack?.readMsgPushRsp(JReadMsgPushRsp)
                }
                //发送文件_Tox消息回馈
                "SendFile" -> {
                    val jSendToxFileRsp = gson.fromJson(text, JSendToxFileRsp::class.java)
                    chatCallBack?.sendToxFileRsp(jSendToxFileRsp)
                }
                //服务器推送过来的别人的消息
                "PushMsg" -> {
                    val JPushMsgRsp = gson.fromJson(text, JPushMsgRsp::class.java)
                    chatCallBack?.pushMsgRsp(JPushMsgRsp)
                    convsationCallBack?.pushMsgRsp(JPushMsgRsp)
                    if (mainInfoBack == null) {
                        AppConfig.instance.tempPushMsgList.add(JPushMsgRsp)
                    }
                    mainInfoBack?.pushMsgRsp(JPushMsgRsp)
                }
                //拉取某个好友的消息,一次十条
                "PullMsg" -> {
                    val JPullMsgRsp = gson.fromJson(text, JPullMsgRsp::class.java)
                    KLog.i("insertMessage:PNRouterServiceMessageReceiver"+chatCallBack)
                    KLog.i("insertMessage:PNRouterServiceMessageReceiver"+convsationCallBack)
                    chatCallBack?.pullMsgRsp(JPullMsgRsp)
                    convsationCallBack?.pullMsgRsp(JPullMsgRsp)
                }
                "DelMsg" -> {
                    val JDelMsgRsp = gson.fromJson(text, JDelMsgRsp::class.java)
                    chatCallBack?.delMsgRsp(JDelMsgRsp)
                    convsationCallBack?.delMsgRsp(JDelMsgRsp)
                }
                "PushDelMsg" -> {
                    val JDelMsgPushRsp = gson.fromJson(text, JDelMsgPushRsp::class.java)
                    chatCallBack?.pushDelMsgRsp(JDelMsgPushRsp)
                    convsationCallBack?.pushDelMsgRsp(JDelMsgPushRsp)
                    mainInfoBack?.pushDelMsgRsp(JDelMsgPushRsp)
                }
                "PushFile" -> {
                    val JPushFileMsgRsp = gson.fromJson(text, JPushFileMsgRsp::class.java)
                    chatCallBack?.pushFileMsgRsp(JPushFileMsgRsp)
                    mainInfoBack?.pushFileMsgRsp(JPushFileMsgRsp)
                }
                "PullFile" -> {
                    val jToxPullFileRsp = gson.fromJson(text, JToxPullFileRsp::class.java)
                    chatCallBack?.pullFileMsgRsp(jToxPullFileRsp)
                }
                //admin登陆
                "RouterLogin" -> {
                    val JAdminLoginRsp = gson.fromJson(text, JAdminLoginRsp::class.java)
                    adminLoginCallBack?.login(JAdminLoginRsp)

                }
                //admin修改密码
                "ResetRouterKey" -> {
                    val JAdminUpdataPasswordRsp = gson.fromJson(text, JAdminUpdataPasswordRsp::class.java)
                    adminUpdataPassWordCallBack?.updataPassWord(JAdminUpdataPasswordRsp)

                }
                //admin修改code
                "ResetUserIdcode" -> {
                    val JAdminUpdataCodeRsp = gson.fromJson(text, JAdminUpdataCodeRsp::class.java)
                    adminUpdataCodeCallBack?.updataCode(JAdminUpdataCodeRsp)

                }
            }
        }

        messageListner?.onMessage(baseData)
    }


//    private val socket: PushServiceSocket

    var pipe: SignalServiceMessagePipe? = null
    var messageListner: MessageReceivedCallback? = null
    var recoveryBackListener: RecoveryMessageCallback? = null
    var registerListener: RegisterMessageCallback? = null
    var loginBackListener: LoginMessageCallback? = null
    var addfrendCallBack: AddfrendCallBack? = null
    var uerInfoUpdateCallBack: UserInfoUpdateCallBack? = null
    var logOutBack: LogOutCallBack? = null
    var mainInfoBack: MainInfoBack? = null
    var addFriendDealCallBack: AddFriendDealCallBack? = null
    var chatCallBack: ChatCallBack? = null
    var convsationCallBack: CoversationCallBack? = null

    var pullFriendCallBack: PullFriendCallBack? = null

    var pullUserCallBack: PullUserCallBack? = null

    var createUserCallBack: CreateUserCallBack? = null

    var adminLoginCallBack: AdminLoginCallBack? = null

    var adminUpdataPassWordCallBack: AdminUpdataPassWordCallBack? = null

    var adminUpdataCodeCallBack: AdminUpdataCodeCallBack? = null

    var adminRecoveryCallBack: AdminRecoveryCallBack? = null

    var userControlleCallBack : UserControlleCallBack? = null

    /**
     * Construct a PNRouterServiceMessageReceiver.
     *
     * @param urls The URL of the Signal Service.
     * @param user The Signal Service username (eg. phone number).
     * @param password The Signal Service user password.
     * @param signalingKey The 52 byte signaling key assigned to this user at registration.
     */
    constructor(urls: SignalServiceConfiguration,
                user: String, password: String,
                signalingKey: String, userAgent: String,
                listener: ConnectivityListener) : this(urls, StaticCredentialsProvider(user, password, signalingKey), userAgent, listener) {
    }

    init {
//        this.socket = PushServiceSocket(urls, credentialsProvider, userAgent)
        createMessagePipe()
//        pipe!!.read(object : SignalServiceMessagePipe.NullMessagePipeCallback() {
//            override fun onMessage(envelope: BaseData<*>) {
//                Log.i("receiver", envelope.baseDataToJson())
//            }
//        })
    }

//    @Throws(IOException::class)
//    fun retrieveProfile(address: SignalServiceAddress): SignalServiceProfile {
//        return socket.retrieveProfile(address)
//    }
//
//    @Throws(IOException::class)
//    fun retrieveProfileAvatar(path: String, destination: File, profileKey: ByteArray, maxSizeBytes: Int): InputStream {
//        socket.retrieveProfileAvatar(path, destination, maxSizeBytes)
//        return ProfileCipherInputStream(FileInputStream(destination), profileKey)
//    }

    /**
     * Retrieves a SignalServiceAttachment.
     *
     * @param pointer The [SignalServiceAttachmentPointer]
     * received in a [SignalServiceDataMessage].
     * @param destination The download destination for this attachment.
     * @param listener An optional listener (may be null) to receive callbacks on download progress.
     *
     * @return An InputStream that streams the plaintext attachment contents.
     * @throws IOException
     * @throws InvalidMessageException
     */
//    @Throws(IOException::class, InvalidMessageException::class)
//    @JvmOverloads
//    fun retrieveAttachment(pointer: SignalServiceAttachmentPointer, destination: File, maxSizeBytes: Int, listener: ProgressListener? = null): InputStream {
//        if (!pointer.getDigest().isPresent()) throw InvalidMessageException("No attachment digest!")
//
//        socket.retrieveAttachment(pointer.getRelay().orNull(), pointer.getId(), destination, maxSizeBytes, listener)
//        return AttachmentCipherInputStream.createFor(destination, pointer.getSize().or(0), pointer.getKey(), pointer.getDigest().get())
//    }

    /**
     * Creates a pipe for receiving SignalService messages.
     *
     * Callers must call [SignalServiceMessagePipe.shutdown] when finished with the pipe.
     *
     * @return A SignalServiceMessagePipe for receiving Signal Service messages.
     */
    fun createMessagePipe(): SignalServiceMessagePipe {
        if (pipe == null) {
            val webSocket = WebSocketConnection(urls.signalServiceUrls[0].url, urls.signalServiceUrls[0].trustStore, credentialsProvider, userAgent, connectivityListener)
            pipe = SignalServiceMessagePipe(webSocket, credentialsProvider)
            pipe!!.messagePipeCallback = this
            return pipe!!
        } else {
            return pipe!!
        }
    }

    fun shutdown() {
        pipe!!.shutdown()
    }
    fun close()
    {
        pipe!!.close()
    }
    fun reConnect() {
        pipe!!.reConenct()
    }

    fun getTrustStore() : TrustStore{
        return urls.signalServiceUrls[0].trustStore
    }

    fun createFileWebSocket(): FileWebSocketConnection {
        return FileWebSocketConnection(urls.signalServiceUrls[0].url, urls.signalServiceUrls[0].trustStore, userAgent, null)
    }

    /**
     * 作为对外暴露的接口，聊天消息统一用这个接口对外输出消息
     */
    interface MessageReceivedCallback {
        fun onMessage(baseData: BaseData)
    }

    class NullMessageReceivedCallback : MessageReceivedCallback {
        override fun onMessage(envelope: BaseData) {}
    }
    interface RecoveryMessageCallback {
        fun recoveryBack(recoveryRsp: JRecoveryRsp)
    }
    interface RegisterMessageCallback {
        fun registerBack(registerRsp: JRegisterRsp)
        fun loginBack(loginRsp: JLoginRsp)
    }
    interface LoginMessageCallback {
        fun loginBack(loginRsp: JLoginRsp)
        fun recoveryBack(recoveryRsp: JRecoveryRsp)
    }

    interface AddfrendCallBack {
        fun addFriendBack(addFriendRsp: JAddFreindRsp)
    }
    interface UserInfoUpdateCallBack {
        fun UserInfoUpdateCallBack(jUserInfoUpdateRsp: JUserInfoUpdateRsp)
    }
    interface LogOutCallBack {
        fun logOutBack(jLogOutRsp: JLogOutRsp)
    }
    interface MainInfoBack {
        fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp)
        fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp)
        fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp)
        fun firendList(jPullFriendRsp: JPullFriendRsp)
        fun pushMsgRsp(pushMsgRsp: JPushMsgRsp)
        fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp)
        fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp)
        fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp)
    }

    interface UserControlleCallBack {
        fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp)
        fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp)
        fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp)
        fun firendList(jPullFriendRsp: JPullFriendRsp)
        fun addFriendBack(addFriendRsp: JAddFreindRsp)
        fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp)
        fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp)
        fun changeRemarksRsp(jChangeRemarksRsp: JChangeRemarksRsp)

    }

    interface AddFriendDealCallBack {
        fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp)
    }

    interface DelFriendCallBack {
        fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp)
    }

    interface PullFriendCallBack {
        fun firendList(jPullFriendRsp: JPullFriendRsp)
    }
    interface PullUserCallBack {
        fun userList(jPullUserRsp: JPullUserRsp)
    }
    interface CreateUserCallBack {
        fun createUser(jCreateNormalUserRsp: JCreateNormalUserRsp)
    }
    interface AdminLoginCallBack {
        fun login(jAdminLoginRsp: JAdminLoginRsp)
    }
    interface AdminUpdataCodeCallBack {
        fun updataCode(jAdminUpdataCodeRsp:JAdminUpdataCodeRsp)
    }
    interface AdminRecoveryCallBack {
        fun recoveryBack(recoveryRsp: JRecoveryRsp)
    }
    interface AdminUpdataPassWordCallBack{
        fun updataPassWord(jAdminUpdataPasswordRsp:JAdminUpdataPasswordRsp)
    }
    interface ChatCallBack {
        fun sendMsg(FromId: String, ToId: String, FriendPublicKey:String,Msg: String);
        fun sendMsgV3(FromIndex: String, ToIndex: String, FriendPublicKey:String,Msg: String);
        fun sendMsgRsp(sendMsgRsp: JSendMsgRsp)
        fun pushMsgRsp(pushMsgRsp: JPushMsgRsp)
        fun pullMsgRsp(pushMsgRsp: JPullMsgRsp)
        fun delMsgRsp(delMsgRsp: JDelMsgRsp)
        fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp)
        fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp)
        fun readMsgPushRsp(jReadMsgPushRsp: JReadMsgPushRsp)
        fun sendToxFileRsp(jSendToxFileRsp: JSendToxFileRsp)
        fun pullFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp)
        fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp)
        fun queryFriend(FriendId :String)
        fun QueryFriendRep(jQueryFriendRsp: JQueryFriendRsp)
    }

    interface CoversationCallBack {
        fun sendMsgRsp(sendMsgRsp: JSendMsgRsp)
        fun pushMsgRsp(pushMsgRsp: JPushMsgRsp)
        fun pullMsgRsp(pushMsgRsp: JPullMsgRsp)
        fun delMsgRsp(delMsgRsp: JDelMsgRsp)
        fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp)
    }

    interface GlobalBack {
        fun pushMsgRsp(pushMsgRsp: JPushMsgRsp)
    }
}

