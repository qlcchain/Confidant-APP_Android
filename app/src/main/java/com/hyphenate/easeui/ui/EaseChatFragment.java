package com.hyphenate.easeui.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView.EaseVoiceRecorderCallback;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.message.Message;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.JDelMsgPushRsp;
import com.stratagile.pnrouter.entity.JDelMsgRsp;
import com.stratagile.pnrouter.entity.JPushMsgRsp;
import com.stratagile.pnrouter.entity.JSendMsgRsp;
import com.stratagile.pnrouter.entity.PullMsgReq;
import com.stratagile.pnrouter.entity.SendFileData;
import com.stratagile.pnrouter.entity.events.ChatKeyboard;
import com.stratagile.pnrouter.entity.events.FileTransformEntity;
import com.stratagile.pnrouter.entity.events.TransformFileMessage;
import com.stratagile.pnrouter.entity.events.TransformReceiverFileMessage;
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity;
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity;
import com.stratagile.pnrouter.utils.CRC16Util;
import com.stratagile.pnrouter.utils.CountDownTimerUtils;
import com.stratagile.pnrouter.utils.FileDownloadUtils;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.FormatTransfer;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.utils.WiFiUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 *
 */
public class EaseChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_FILE = 5;
    protected static final int REQUEST_CODE_VIDEO = 6;

    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";

    protected static final int TYPING_SHOW_TIME = 5000;

    /**
     * params to fragment
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUserId;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected File cameraFile;
    protected File videoFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    private View kickedForOfflineLayout;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected ChatRoomListener chatRoomListener;
    protected EMMessage contextMenuMessage;

    static final int ITEM_PICTURE= 1;
    static final int ITEM_TAKE_PICTURE = 2;
    static final int ITEM_SHORTVIDEO = 3;
    static final int ITEM_FILE = 4;
    static final int ITEM_LOCATION = 5;
    static final int ITEM_MEETING= 6;
    static final int ITEM_VIDEOCALL = 7;
    static final int ITEM_PRIVATEFILE = 8;

    protected static final int sendFileSizeMax = 1024 *100;
    protected int[] itemStrings = {  R.string.attach_picture,R.string.attach_take_pic, R.string.attach_Short_video, R.string.attach_file,R.string.attach_location, R.string.attach_Meeting, R.string.attach_Video_call, R.string.attach_Privatefile };
    protected int[] itemdrawables = {  R.drawable.ease_chat_image_selector,R.drawable.ease_chat_takepic_selector,
            R.drawable.ease_chat_shortvideo_selector, R.drawable.ease_chat_localdocument_selector,R.drawable.ease_chat_location_selector, R.drawable.ease_chat_meeting_selector,
            R.drawable.ease_chat_videocall_selector, R.drawable.ease_chat_pirvatedocument_selector  };
    protected int[] itemIds = { ITEM_PICTURE,ITEM_TAKE_PICTURE,ITEM_SHORTVIDEO ,ITEM_FILE,ITEM_LOCATION,ITEM_MEETING,ITEM_VIDEOCALL,ITEM_PRIVATEFILE};
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected boolean isRoaming = false;
    private ExecutorService fetchQueue;
    // to handle during-typing actions.
    private Handler typingHandler = null;
    // "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
    private boolean turnOnTyping;
    protected Handler handler = new Handler();
    List<Message> messageListTemp;

    private  int currentPage = 0;
    private int MsgStartId = 0;
    private EMMessage currentSendMsg;

    private UserEntity toChatUser;

    private HashMap<String, EMMessage> sendMsgMap = new HashMap<>();
    private HashMap<String, String> sendFilePathMap = new HashMap<>();
    private HashMap<String, Boolean> sendFileResultMap = new HashMap<>();
    private HashMap<String, String> sendFileNameMap = new HashMap<>();
    private HashMap<String, Integer> sendFileLastByteSizeMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileLeftByteMap = new HashMap<>();
    private HashMap<String, String> sendMsgIdMap = new HashMap<>();
    private HashMap<String, Message> receiveFileDataMap = new HashMap<>();


    private CountDownTimerUtils countDownTimerUtilsOnVpnServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        isRoaming = roaming;
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sendMsgMap = new HashMap<>();
        sendFilePathMap = new HashMap<>();
        sendFileResultMap = new HashMap<>();
        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            List<UserEntity> userEntities = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().loadAll();
            for (int i = 0; i < userEntities.size(); i++) {
                if (userEntities.get(i).getUserId().equals(toChatUserId)) {
                    toChatUser = userEntities.get(i);
                    break;
                }
            }
        }
        // userId you are chat with or group id
        toChatUserId = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);

        this.turnOnTyping = turnOnTyping();

        super.onActivityCreated(savedInstanceState);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectWebSocket(FileTransformEntity fileTransformEntity){

        if(fileTransformEntity.getMessage() == 0)
        {
            return;
        }
        switch (fileTransformEntity.getMessage())
        {
            case 1:
                EMMessage  EMMessage = sendMsgMap.get(fileTransformEntity.getToId());
                String filePath = sendFilePathMap.get(fileTransformEntity.getToId());
                String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                File file = new File(filePath);
                if(file.exists())
                {
                    long fileSize = file.length();
                    String fileMD5 = FileUtil.getFileMD5(file);
                   /*  SendStrMsg SendStrMsg = new SendStrMsg(EMMessage.getFrom(),EMMessage.getTo(),fileName,fileSize,fileMD5,"SendFile");
                    String jsonData = JSONObject.toJSON(new BaseData(SendStrMsg)).toString();
                    EventBus.getDefault().post(new TransformStrMessage(fileTransformEntity.getToId(),jsonData));*/
                    byte[] fileBuffer= FileUtil.file2Byte(filePath);
                    int fileId = (int)System.currentTimeMillis()/1000;
                    sendFileByteData(fileBuffer,fileName,EMMessage.getFrom(),EMMessage.getTo(),fileTransformEntity.getToId(),fileId,1);
                }
                break;
            case 2:
                break;
            case 3:
                Gson gson = new Gson();
                String filePathOk = sendFilePathMap.get(fileTransformEntity.getToId());
                File fileOk = new File(filePathOk);
                String retMsg = fileTransformEntity.getRetMsg();
                byte[] aa = retMsg.getBytes();
                String aabb = "";
                /*switch (JSONObject.parseObject((JSONObject.parseObject(retMsg)).get("params").toString()).getString("Action"))
                {
                    case "SendFile":
                        JSendFileRsp jSendFileRsp = gson.fromJson(retMsg, JSendFileRsp.class);
                        if(fileOk.exists() && jSendFileRsp.getParams().getRetCode()== 0)
                        {
                            sendFileResultMap.put(fileTransformEntity.getToId(),false);
                            //EventBus.getDefault().post(new TransformFileMessage(fileTransformEntity.getToId(),fileOk));
                        }
                        Timer timer = new Timer();// 实例化Timer类
                        timer.schedule(new TimerTask() {
                            public void run() {
                                Log.i("sendFileTimer","beginCounTimer");
                                if(!sendFileResultMap.get(fileTransformEntity.getToId()))
                                {
                                    //EventBus.getDefault().post(new TransformFileMessage(fileTransformEntity.getToId(),fileOk));
                                }
                                this.cancel();
                            }
                        }, 20000);// 这里百毫秒
                        break;
                    case "SendFileEnd":
                        sendFileResultMap.put(fileTransformEntity.getToId(),true);
                        JSendFileEndRsp jSendFileEndRsp = gson.fromJson(retMsg, JSendFileEndRsp.class);
                        if(jSendFileEndRsp.getParams().getRetCode() != 0)
                        {
                            //EventBus.getDefault().post(new TransformFileMessage(fileTransformEntity.getToId(),fileOk));
                        }else {
                            String pAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
                            String wssUrl = "https://"+pAddress+ ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(fileTransformEntity.getToId(),4,"",wssUrl,"lws-pnr-bin"));
                        }
                        break;
                    default:
                        break;
                }*/
                break;
            default:
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectWebSocket(TransformReceiverFileMessage transformReceiverFileMessage){
        byte[] retMsg = transformReceiverFileMessage.getMessage();
        byte[] Action = new byte[4];
        byte[] FileId = new byte[4];
        byte[] SegSeq = new byte[4];
        byte[] CRC = new byte[2];
        byte[] Code = new byte[2];
        byte[] FromId = new byte[76];
        byte[] ToId = new byte[76];
        System.arraycopy(retMsg, 0, Action, 0, 4);
        System.arraycopy(retMsg, 4, FileId, 0, 4);
        System.arraycopy(retMsg, 8, SegSeq, 0, 4);
        System.arraycopy(retMsg, 12, CRC, 0, 2);
        System.arraycopy(retMsg, 14, Code, 0, 2);
        System.arraycopy(retMsg, 16, FromId, 0, 76);
        System.arraycopy(retMsg, 93, ToId, 0, 76);
        int ActionResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(Action)) ;
        int FileIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(FileId));
        int SegSeqResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(SegSeq));
        short CRCResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(CRC));
        short CodeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(Code));
        String FromIdResult  = new String(FromId);
        String ToIdResult  = new String(ToId);
        String aa = "";
        switch (CodeResult)
        {
            case 0:
                int lastSendSize = sendFileLastByteSizeMap.get(FileIdResult+"");
                byte[] fileBuffer = sendFileLeftByteMap.get(FileIdResult+"");
                int leftSize =fileBuffer.length - lastSendSize;
                String msgId = sendMsgIdMap.get(FileIdResult+"");
                if(leftSize >0)
                {
                    byte[] fileLeftBuffer = new byte[leftSize];
                    System.arraycopy(fileBuffer, sendFileSizeMax, fileLeftBuffer, 0, leftSize);
                    String fileName = sendFileNameMap.get(FileIdResult+"");
                    sendFileByteData(fileLeftBuffer,fileName,FromIdResult+"",ToIdResult+"",msgId,FileIdResult,SegSeqResult +1);
                }else{
                    String pAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
                    String wssUrl = "https://"+pAddress + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
                    KLog.i("文件发送成功！");
                }
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
    private void sendFileByteData(byte[] fileLeftBuffer,String fileName,String From,String To,String msgId,int fileId,int segSeq)
    {
        String MsgType = fileName.substring(fileName.lastIndexOf(".")+1);
        int action = 1;
        switch (MsgType)
        {
            case "png":
            case "jpg":
                action = 1;
                break;
            case "amr":
                action = 2;
                break;
        }
        SendFileData sendFileData = new SendFileData();
        int segSize = fileLeftBuffer.length > sendFileSizeMax ? sendFileSizeMax : (int)fileLeftBuffer.length;
        sendFileData.setMagic(FormatTransfer.reverseInt(0x0dadc0de));
        sendFileData.setAction(FormatTransfer.reverseInt(action));
        sendFileData.setSegSize(FormatTransfer.reverseInt(segSize));
        int aa = FormatTransfer.reverseInt(9437440);
        sendFileData.setSegSeq(FormatTransfer.reverseInt(segSeq));
        int fileOffset = 0;
        fileOffset = (segSeq -1) * sendFileSizeMax;
        sendFileData.setFileOffset(FormatTransfer.reverseInt(fileOffset));
        sendFileData.setFileId(FormatTransfer.reverseInt(fileId));
        sendFileData.setCRC(FormatTransfer.reverseShort((short)0));
        int segMore = fileLeftBuffer.length>sendFileSizeMax ? 1: 0;
        sendFileData.setSegMore((byte) segMore);
        sendFileData.setCotinue((byte) 0);
        sendFileData.setFileName(fileName.getBytes());
        sendFileData.setFromId(From.getBytes());
        sendFileData.setToId(To.getBytes());
        byte[] content = new byte[sendFileSizeMax];
        System.arraycopy(fileLeftBuffer, 0, content, 0, segSize);
        sendFileData.setContent(content);
        byte[] sendData = sendFileData.toByteArray();
        int newCRC = CRC16Util.getCRC(sendData,sendData.length);
        sendFileData.setCRC(FormatTransfer.reverseShort((short)newCRC));
        sendData = sendFileData.toByteArray();
        sendFileNameMap.put(fileId+"",fileName);
        sendFileLastByteSizeMap.put(fileId+"",segSize);
        sendFileLeftByteMap.put(fileId+"",fileLeftBuffer);
        sendMsgIdMap.put(fileId+"",msgId);
        EventBus.getDefault().post(new TransformFileMessage(msgId,sendData));
        String s = new String(content);
        String aabb =  FileUtil.bytesToHex(content);
        //KLog.i("发送中>>>内容"+"content:"+aabb);
        KLog.i("发送中>>>"+"segMore:"+segMore+"  " +"segSize:"+ segSize  +"   " + "left:"+ (fileLeftBuffer.length -segSize) +"  segSeq:"+segSeq  +"  fileOffset:"+fileOffset +"  setSegSize:"+sendFileData.getSegSize()+" CRC:"+newCRC);
    }
    /**
     * 锁定内容高度，防止跳闪
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void lockContentHeight(ChatKeyboard chatKeyboard){
//        if (chatKeyboard.isLock()) {
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageList.getLayoutParams();
//            params.height = messageList.getHeight();
//            params.weight = 0.0F;
//        } else {
//            ((LinearLayout.LayoutParams) messageList.getLayoutParams()).weight = 1.0F;
//        }
    }

    /**
     * 释放被锁定的内容高度
     */
//    private void unLockContentHeight() {
//        ((LinearLayout.LayoutParams) messageList.getLayoutParams()).weight = 1.0F;
//    }
//


    protected boolean turnOnTyping() {
        return false;
    }

    /**
     * init view
     */
    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        currentPage = 0;
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        if(chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
//        messageList.setAvatarShape(1);
        listView = messageList.getListView();
        kickedForOfflineLayout = getView().findViewById(R.id.layout_alert_kicked_off);
        kickedForOfflineLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatRoomViewCreation();
            }
        });

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        inputMenu.bindContentView(messageList);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                // send action:TypingBegin cmd msg.
                typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
            }

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
//        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
//                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (isRoaming) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }

        // to handle during-typing actions.
        typingHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case MSG_TYPING_BEGIN: // Notify typing start

                        if (!turnOnTyping) return;

                        // Only support single-chat type conversation.
                        if (chatType != EaseConstant.CHATTYPE_SINGLE)
                            return;

                        if (hasMessages(MSG_TYPING_END)) {
                            // reset the MSG_TYPING_END handler msg.
                            removeMessages(MSG_TYPING_END);
                        } else {
                            // Send TYPING-BEGIN cmd msg
                            EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                            EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
                            // Only deliver this cmd msg to online users
                            body.deliverOnlineOnly(true);
                            beginMsg.addBody(body);
                            beginMsg.setTo(toChatUserId);
                            EMClient.getInstance().chatManager().sendMessage(beginMsg);
                        }

                        sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
                        break;
                    case MSG_TYPING_END:

                        if (!turnOnTyping) return;

                        // Only support single-chat type conversation.
                        if (chatType != EaseConstant.CHATTYPE_SINGLE)
                            return;

                        // remove all pedding msgs to avoid memory leak.
                        removeCallbacksAndMessages(null);
                        // Send TYPING-END cmd msg
                        EMMessage endMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_END);
                        // Only deliver this cmd msg to online users
                        body.deliverOnlineOnly(true);
                        endMsg.addBody(body);
                        endMsg.setTo(toChatUserId);
                        EMClient.getInstance().chatManager().sendMessage(endMsg);
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };

    }
    protected void setUpView() {
        titleBar.setTitle( UserDataManger.curreantfriendUserData.getNickName());
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if(EaseUserUtils.getUserInfo(toChatUserId) != null){
                EaseUser user = EaseUserUtils.getUserInfo(toChatUserId);
                if (user != null) {
                    titleBar.setTitle(user.getNick());
                }
            }
            titleBar.setRightImageResource(R.mipmap.icon_person);
        } else {
            titleBar.setRightImageResource(R.drawable.ease_to_group_details_normal);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                //group chat
                EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
                if (group != null)
                    titleBar.setTitle(group.getGroupName());
                // listen the event that user moved out group or group is dismissed
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
                chatRoomListener = new ChatRoomListener();
                EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
                onChatRoomViewCreation();
            }

        }
        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//                    emptyHistory();
                    toPersonDetails();
                } else {
                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }
    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem(){
        for(int i = 0; i < itemStrings.length; i++){
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit(){
        if(conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        EMMessage lastMessage = conversation.getLastMessage();
        if(lastMessage != null)
            lastMessage.setUnread(false);
        //conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number

        if (!isRoaming) {
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            }
        } else {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                                toChatUserId, EaseCommonUtils.getConversationType(chatType), pagesize, "");
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                        }
                        messageList.refreshSelectLast();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void  refreshData(List<Message> messageList)
    {
        if(conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        if (conversation != null) {
            if(currentPage == 0)
            {
                conversation.clearAllMessages();
            }

        }
        int size = messageList.size();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 50);
        if(size > 0)
            currentPage ++;
        else {
            if(currentPage !=0)
            {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.nomore, Toast.LENGTH_SHORT).show();
                    }
                }, 50);
            }

        }
        if(messageListTemp == null)
        {
            messageListTemp = messageList;
        }
        String userId =   SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        for(int i= 0 ; i < size ;i++)
        {
            Message Message = messageList.get(i);
            EMMessage message = null;
            switch (Message.getMsgType())
            {
                case 0:
                    message = EMMessage.createTxtSendMessage(Message.getMsg(), toChatUserId);
                    break;
                case 1:
                    String ease_default_image = PathUtils.getInstance().getImagePath()+"/"  + "ease_default_image.png";
                    String files_dir =  PathUtils.getInstance().getImagePath()+"/" +Message.getFileName();
                    File filesFile = new File(files_dir);
                    if(filesFile.exists())
                    {
                        message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                    }else{
                        message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                        String ipAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
                        String filledUri = "https://" + ipAddress + ConstantValue.INSTANCE.getPort()+Message.getFilePath();
                        String save_dir = PathUtils.getInstance().getImagePath()+"/";
                        FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(),Message.getMsgId(), handlerDown);
                    }
                    break;
                case 2:
                    String ease_default_amr =  PathUtils.getInstance().getVoicePath()+"/" + "ease_default_amr.amr";
                    String files_dir_amr = PathUtils.getInstance().getVoicePath()+"/" +Message.getFileName();
                    File filesFileAmr = new File(files_dir_amr);
                    if(filesFileAmr.exists())
                    {
                        long sizea = filesFileAmr.length();
                        int longTime = FileUtil.getAmrDuration(filesFileAmr);
                        message = EMMessage.createVoiceSendMessage(files_dir_amr, longTime, toChatUserId);
                    }else{
                        message = EMMessage.createVoiceSendMessage(ease_default_amr, 2, toChatUserId);
                        String ipAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
                        String filledUri = "https://" + ipAddress + ConstantValue.INSTANCE.getPort()+Message.getFilePath();
                        String save_dir =  PathUtils.getInstance().getVoicePath()+"/";
                        FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(),Message.getMsgId(), handlerDown);
                    }
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    break;
            }
            if(message == null)
            {
                continue;
            }
            message.setFrom(Message.getFrom());
            message.setTo(Message.getTo());
            message.setUnread(false);
            if(Message.getFrom().equals(userId))
            {
                message.setDirection(EMMessage.Direct.SEND );
            }else {
                message.setDirection(EMMessage.Direct.RECEIVE );
            }
            message.setMsgTime(Message.getTimeStatmp()* 1000);
            if(i == 0)
            {
                MsgStartId = Message.getMsgId();
            }
            message.setMsgId( Message.getMsgId()+"");
            if(Message.getMsgType() != 0)
            {
                receiveFileDataMap.put(Message.getMsgId()+"",Message);
            }

            sendMessageTo(message);
        }

    }
    public void delMyMsg(JDelMsgRsp jDelMsgRsp)
    {
        if(conversation !=null )
            conversation.removeMessage(jDelMsgRsp.getParams().getMsgId()+"");
        //refresh ui
        if(isMessageListInited) {
            messageList.refresh();
        }
    }
    public void  delFreindMsg(JDelMsgPushRsp jDelMsgRsp)
    {
        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(jDelMsgRsp.getParams().getMsgId()+"");
        EMTextMessageBody var3 = new EMTextMessageBody(getResources().getString(R.string.withdrawn));
        forward_msg.addBody(var3);
        if(conversation !=null )
            conversation.updateMessage(forward_msg);
        //conversation.removeMessage(jDelMsgRsp.getParams().getMsgId()+"");
        //refresh ui
        if(isMessageListInited) {
            messageList.refresh();
        }
    }
    protected void onMessageListInit(){
        messageList.init(toChatUserId, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if(chatFragmentHelper != null){
                    chatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                EMLog.i(TAG, "onResendClick");
                new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        message.setStatus(EMMessage.Status.CREATE);
                        sendMessageTo(message);
                    }
                }, true).show();
                return true;
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if(chatFragmentHelper != null){
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if(chatFragmentHelper != null){
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if(chatFragmentHelper == null){
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(messageStatusCallback);
            }
        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadMoreRoamingMessages();
                    }
                }, 600);
            }
        });
    }

    private void loadMoreRoamingMessages() {

        swipeRefreshLayout.setRefreshing(true);
        String userId =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        PullMsgReq pullMsgList = new PullMsgReq( userId,toChatUserId,1,MsgStartId,10,"PullMsg");
        AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(pullMsgList));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            }else if(requestCode == REQUEST_CODE_VIDEO)
            {
                //if (videoFile != null && videoFile.exists())
                //sendImageMessage(videoFile.getAbsolutePath());
            }else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                String msgContent = data.getStringExtra("msg");
                EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
                // Send the ding-type msg.
                EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(toChatUserId, msgContent);
                sendMessageTo(dingMsg);
            }else  if (requestCode == REQUEST_CODE_FILE) {
                String filePath = data.getStringExtra("path");
                File file = new File(filePath);
                long fileSize = file.length();
                String md5Data = "";
                if(file.exists())
                {
                    md5Data = FileUtil.getFileMD5(file);
                }
                sendFileMessage(filePath);
                KLog.i("返回。。。。。。。SELECT_PROFILE" + md5Data);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if(chatType == EaseConstant.CHATTYPE_GROUP){
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUserId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());

        // Remove all padding actions in handler
        handler.removeCallbacksAndMessages(null);
        typingHandler.sendEmptyMessage(MSG_TYPING_END);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        if (chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        }

        if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUserId);
        }
    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if(chatType == EaseConstant.CHATTYPE_GROUP){
                EaseAtMessageHelper.get().removeAtMeGroup(toChatUserId);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUserId);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUserId, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity().isFinishing() || !toChatUserId.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUserId);
                        if (room != null) {
                            titleBar.setTitle(room.getName());
                            EMLog.d(TAG, "join room success : " + room.getName());
                        } else {
                            titleBar.setTitle(toChatUserId);
                        }
                        onConversationInit();
                        onMessageListInit();

                        // Dismiss the click-to-rejoin layout.
                        kickedForOfflineLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            if (username.equals(toChatUserId) || message.getTo().equals(toChatUserId) || message.conversationId().equals(toChatUserId)) {
                messageList.refreshSelectLast();
                //conversation.markMessageAsRead(message.getMsgId());
            }
            EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (final EMMessage msg : messages) {
            final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ACTION_TYPING_BEGIN.equals(body.action()) && msg.getFrom().equals(toChatUserId)) {
                        titleBar.setTitle(getString(R.string.alert_during_typing));
                    } else if (ACTION_TYPING_END.equals(body.action()) && msg.getFrom().equals(toChatUserId)) {
                        titleBar.setTitle(toChatUserId);
                    }
                }
            });
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    /**
     * handle the click event for extend menu
     *
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener{

        @Override
        public void onClick(int itemId, View view) {
            if(chatFragmentHelper != null){
                if(chatFragmentHelper.onExtendMenuItemClick(itemId, view)){
                    return;
                }
            }
            switch (itemId) {
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_TAKE_PICTURE:
                    AndPermission.with(AppConfig.instance)
                            .requestCode(101)
                            .permission(
                                    Manifest.permission.CAMERA
                            )
                            .callback(permission)
                            .start();
                    break;
                case ITEM_SHORTVIDEO:
                    AndPermission.with(AppConfig.instance)
                            .requestCode(101)
                            .permission(
                                    Manifest.permission.CAMERA
                            )
                            .callback(permissionVideo)
                            .start();
                    break;
                case ITEM_LOCATION:
                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;
                case ITEM_FILE:
                    startActivityForResult(new Intent(getActivity(), FileChooseActivity.class), REQUEST_CODE_FILE);
                    break;
                default:
                    Toast.makeText(getActivity(), R.string.wait, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
    private PermissionListener permission = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                selectPicFromCamera();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败");

            }
        }
    };
    private PermissionListener permissionVideo = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                selectVideoFromCamera();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败");

            }
        }
    };
    /**
     * input @
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol){
        if(EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP){
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null){
            username = user.getNick();
        }
        if(autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }


    /**
     * input @
     * @param username
     */
    protected void inputAtUsername(String username){
        inputAtUsername(username, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if(EaseAtMessageHelper.get().containsAtUsername(content)){
            sendAtMessage(content);
        }else{
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserId);
            String userId =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(),"");
            AppConfig.instance.getMessageReceiver().getChatCallBack().sendMsg(userId, UserDataManger.curreantfriendUserData.getUserId(),content);
            message.setFrom(userId);
            message.setTo( UserDataManger.curreantfriendUserData.getUserId());
            message.setUnread(false);
            currentSendMsg = message;
            sendMessageTo(message);
        }
    }
    public void upateMessage(JSendMsgRsp jSendMsgRsp)
    {
        if(conversation !=null )
        {
            conversation.removeMessage(currentSendMsg.getMsgId());
            currentSendMsg.setMsgId(jSendMsgRsp.getParams().getMsgId()+"");
            conversation.insertMessage(currentSendMsg);
        }
    }
    /**
     * send @ message, only support group chat message
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content){
        if(chatType != EaseConstant.CHATTYPE_GROUP){
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserId);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
        if(EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)){
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        }else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessageTo(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode){
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUserId, name, identityCode);
        sendMessageTo(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUserId);
        String userId =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(),"");
        message.setFrom(userId);
        message.setTo( UserDataManger.curreantfriendUserData.getUserId());
        message.setUnread(false);
        currentSendMsg = message;
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        sendMsgMap.put(uuid,message);
        sendFilePathMap.put(uuid,filePath);
        String pAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
        String wssUrl = "https://"+pAddress + ConstantValue.INSTANCE.getFilePort();
        EventBus.getDefault().post(new FileTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));
        String files_dir = getActivity().getFilesDir().getAbsolutePath() + "/voice/" + filePath.substring(filePath.lastIndexOf("/")+1);
        //int result =  FileUtil.copySdcardFile(filePath,files_dir);
        sendMessageTo(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, true, toChatUserId);
        String userId =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(),"");
        message.setFrom(userId);
        message.setTo( UserDataManger.curreantfriendUserData.getUserId());
        message.setUnread(false);
        currentSendMsg = message;
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        sendMsgMap.put(uuid,message);
        sendFilePathMap.put(uuid,imagePath);
        String pAddress = WiFiUtil.INSTANCE.getGateWay(AppConfig.instance);
        String wssUrl = "https://"+pAddress + ConstantValue.INSTANCE.getFilePort();
        EventBus.getDefault().post(new FileTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));
        String files_dir = getActivity().getFilesDir().getAbsolutePath() + "/image/" + imagePath.substring(imagePath.lastIndexOf("/")+1);
        int result =  FileUtil.copySdcardFile(imagePath,files_dir);
        sendMessageTo(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUserId);
        sendMessageTo(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUserId);
        sendMessageTo(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUserId);
        sendMessageTo(message);
    }

    /**
     * 接受文字和表情消息
     * @param jPushMsgRsp
     */
    public void receiveTxtMessage(JPushMsgRsp jPushMsgRsp)
    {
        EMMessage message = EMMessage.createTxtSendMessage(jPushMsgRsp.getParams().getMsg(), toChatUserId);
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(jPushMsgRsp.getParams().getMsgId() + "");
        message.setFrom(jPushMsgRsp.getParams().getFromId());
        message.setTo(jPushMsgRsp.getParams().getToId());
        sendMessageTo(message);
    }
    /**
     * 接受文件消息
     * @param url
     * @param msgId
     * @param fromId
     * @param toId
     */
    public void receiveFileMessage(String url,String msgId, String fromId,String toId,int FileType)
    {
        String files_dir = "";
        EMMessage message = null;
        switch (FileType)
        {
            case 1:
                files_dir = PathUtils.getInstance().getImagePath() + "/" +url;
                message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                break;
            case 2:
                files_dir = PathUtils.getInstance().getVoicePath() + "/" +url;
                message = EMMessage.createVoiceSendMessage(files_dir, 4, toChatUserId);
                break;
            default:
                break;
        }
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(msgId);
        message.setFrom(fromId);
        message.setTo(toId);
        sendMessageTo(message);
    }
    protected void sendMessageTo(EMMessage message){
        if (message == null) {
            return;
        }
        if(conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        if(chatFragmentHelper != null){
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP){
            message.setChatType(ChatType.GroupChat);
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            message.setChatType(ChatType.ChatRoom);
        }

        message.setMessageStatusCallback(messageStatusCallback);

        // Send message.
        //EMClient.getInstance().chatManager().sendMessageTo(message);
        //message.setDirection(EMMessage.Direct.RECEIVE );
        message.setStatus(EMMessage.Status.SUCCESS);
        if(conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        if(conversation != null)
        {
            conversation.insertMessage(message);
            //refresh ui
            if(isMessageListInited) {
                messageList.refresh();
            }
        }else{
            new Thread(new Runnable(){

                public void run(){

                    try
                    {
                        Thread.sleep(1000);
                        if(conversation == null)
                            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
                        if(conversation != null)
                        {
                            conversation.insertMessage(message);
                            //refresh ui
                            if(isMessageListInited) {
                                messageList.refresh();
                            }
                        }
                    }catch (Exception e)
                    {

                    }
                }

            }).start();
        }

        /*if(isMessageListInited) {
            messageList.refreshSelectLast();
        }*/
    }


    //===================================================================================

    protected EMCallBack messageStatusCallback = new EMCallBack() {
        @Override
        public void onSuccess() {
            if(isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            Log.i("EaseChatRowPresenter", "onError: " + code + ", error: " + error);
            if(isMessageListInited) {
                messageList.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            Log.i(TAG, "onProgress: " + progress);
            if(isMessageListInited) {
                messageList.refresh();
            }
        }
    };

    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     * @param uri
     */
    protected void sendFileByUri(Uri uri){
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = new File(PathUtils.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }
    /**
     * capture new video
     */
    protected void selectVideoFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        videoFile = new File(PathUtils.getInstance().getVideoPath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".mp4");
        KLog.i(videoFile.getPath());
        //noinspection ResultOfMethodCallIgnored
        videoFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_VIDEO_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), videoFile)).putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10).putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0),
                REQUEST_CODE_VIDEO);
    }
    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    private void toPersonDetails() {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            List<UserEntity> userEntities = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().loadAll();
            if (toChatUser != null) {
                intent.putExtra("user", toChatUser);
                startActivity(intent);
            } else {
                for (int i = 0; i < userEntities.size(); i++) {
                    if (userEntities.get(i).getUserId().equals(toChatUserId)) {
                        intent.putExtra("user", userEntities.get(i));
                        startActivity(intent);
                        break;
                    }
                }
            }
        }
    }


    /**
     * clear the conversation history
     *
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(),null, msg, null,new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if(confirmed){
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    messageList.refresh();
                    haveMoreData = true;
                }
            }
        }, true).show();
    }

    /**
     * open group detail
     *
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if(chatFragmentHelper != null){
                chatFragmentHelper.onEnterToChatDetails();
            }
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            if(chatFragmentHelper != null){
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if(forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                }else{
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if(forward_msg.getChatType() == ChatType.ChatRoom){
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
     *
     */
    class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    if (toChatUserId.equals(groupId)) {
                        Toast.makeText(getActivity(), R.string.you_are_group, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            // prompt group is dismissed and finish this activity
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (toChatUserId.equals(groupId)) {
                        Toast.makeText(getActivity(), R.string.the_current_group_destroyed, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }
    }

    /**
     * listen chat room event
     */
    class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUserId)) {
                        Toast.makeText(getActivity(), R.string.the_current_chat_room_destroyed, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUserId)) {
                        if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                            Toast.makeText(getActivity(), R.string.quiting_the_chat_room, Toast.LENGTH_LONG).show();
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.finish();
                            }
                        } else { // BE_KICKED_FOR_OFFLINE
                            // Current logged in user be kicked out by server for current user offline,
                            // show disconnect title bar, click to rejoin.
                            Toast.makeText(getActivity(), "User be kicked for offline", Toast.LENGTH_SHORT).show();
                            kickedForOfflineLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }



        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (roomId.equals(toChatUserId)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member join:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (roomId.equals(toChatUserId)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member exit:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


    }

    protected EaseChatFragmentHelper chatFragmentHelper;
    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper){
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper{
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }
    protected Handler handlerDown = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x404:

                    break;
                case 0x55:
                    if(conversation !=null )
                    {
                        String userId =   SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        Bundle data = msg.getData();
                        String msgId = data.getInt("msgID")+"";
                        Message message = receiveFileDataMap.get(msgId);
                        conversation.removeMessage(msgId);
                        String files_dir = "";
                        EMMessage messageData = null;
                        switch (message.getMsgType())
                        {
                            case 1:
                                files_dir = PathUtils.getInstance().getImagePath()+"/" +message.getFileName();
                                messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                                break;
                            case 2:
                                files_dir = PathUtils.getInstance().getVoicePath()+"/" +message.getFileName();
                                messageData = EMMessage.createVoiceSendMessage(files_dir, 2, toChatUserId);
                                break;
                        }
                        if(messageData != null)
                        {
                            messageData.setFrom(message.getFrom());
                            messageData.setTo(message.getTo());
                            messageData.setUnread(false);
                            if(message.getFrom().equals(userId))
                            {
                                messageData.setDirection(EMMessage.Direct.SEND );
                            }else {
                                messageData.setDirection(EMMessage.Direct.RECEIVE );
                            }
                            messageData.setMsgTime(message.getTimeStatmp()* 1000);
                            messageData.setMsgId( message.getMsgId()+"");
                            sendMessageTo(messageData);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
