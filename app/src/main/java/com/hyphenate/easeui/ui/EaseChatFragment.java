package com.hyphenate.easeui.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
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
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.message.Message;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.DraftEntity;
import com.stratagile.pnrouter.db.DraftEntityDao;
import com.stratagile.pnrouter.db.MessageEntity;
import com.stratagile.pnrouter.db.MessageEntityDao;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.JDelMsgPushRsp;
import com.stratagile.pnrouter.entity.JPushMsgRsp;
import com.stratagile.pnrouter.entity.JSendMsgRsp;
import com.stratagile.pnrouter.entity.JSendToxFileRsp;
import com.stratagile.pnrouter.entity.JUserInfoPushRsp;
import com.stratagile.pnrouter.entity.PullFileReq;
import com.stratagile.pnrouter.entity.PullMsgReq;
import com.stratagile.pnrouter.entity.SendFileInfo;
import com.stratagile.pnrouter.entity.SendToxFileNotice;
import com.stratagile.pnrouter.entity.ToxFileData;
import com.stratagile.pnrouter.entity.events.ChatKeyboard;
import com.stratagile.pnrouter.entity.events.FileTransformEntity;
import com.stratagile.pnrouter.entity.events.FileTransformStatus;
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity;
import com.stratagile.pnrouter.ui.activity.file.SelectFileActivity;
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.CountDownTimerUtils;
import com.stratagile.pnrouter.utils.FileDownloadUtils;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.LibsodiumUtil;
import com.stratagile.pnrouter.utils.LogUtil;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.utils.RxEncryptTool;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.utils.StringUitl;
import com.stratagile.tox.toxcore.ToxCoreJni;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
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

    protected static final int CHOOSE_PIC = 88; //选择原图还是压缩图

    /**
     * params to fragment
     */
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUserId;
    protected int friendStatus = 0;
    protected EaseChatMessageList easeChatMessageList;
    public EaseChatInputMenu inputMenu;

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

    static final int ITEM_PICTURE = 1;
    static final int ITEM_TAKE_PICTURE = 2;
    static final int ITEM_SHORTVIDEO = 3;
    static final int ITEM_FILE = 4;
    static final int ITEM_LOCATION = 5;
    static final int ITEM_MEETING = 6;
    static final int ITEM_VIDEOCALL = 7;
    static final int ITEM_PRIVATEFILE = 8;

    protected int[] itemStrings = {R.string.attach_picture, R.string.attach_take_pic, R.string.attach_Short_video, R.string.attach_file};
    protected int[] itemdrawables = {R.drawable.ease_chat_image_selector, R.drawable.ease_chat_takepic_selector,
            R.drawable.ease_chat_shortvideo_selector, R.drawable.ease_chat_localdocument_selector};
    protected int[] itemIds = {ITEM_PICTURE, ITEM_TAKE_PICTURE, ITEM_SHORTVIDEO, ITEM_FILE, ITEM_LOCATION, ITEM_MEETING, ITEM_VIDEOCALL, ITEM_PRIVATEFILE};
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

    private int currentPage = 0;
    private int MsgStartId = 0;
    private EMMessage currentSendMsg;

    private UserEntity toChatUser;

    private HashMap<String, Boolean> sendMsgLocalMap = new HashMap<>();
    private HashMap<String, String> sendFilePathMap = new HashMap<>();
    private HashMap<String, Boolean> deleteFileMap = new HashMap<>();
    private HashMap<String, String> receiveToxFileNameMap = new HashMap<>();
    private HashMap<String, String> sendFileFriendKeyMap = new HashMap<>();
    private HashMap<String, String> sendFileKeyByteMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileFriendKeyByteMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileMyKeyByteMap = new HashMap<>();
    private HashMap<String, Boolean> sendFileResultMap = new HashMap<>();
    private HashMap<String, Message> receiveFileDataMap = new HashMap<>();
    private HashMap<String, Message> receiveToxFileDataMap = new HashMap<>();
    private HashMap<String, String> receiveToxFileIdMap = new HashMap<>();
    private long faBegin;
    private long faEnd;
    //是否正在录音，正在录音，其他点击不能生效
//    private boolean isRecording = false;

    private CountDownTimerUtils countDownTimerUtilsOnVpnServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        friendStatus = 0;
        isRoaming = roaming;
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
            AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
        }
        sendMsgLocalMap = new HashMap<>();
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

    public void setChatUserId(String id) {
        toChatUserId = id;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileTransformStatus(FileTransformStatus fileTransformStatus) {
        String msgId = fileTransformStatus.getMsgid();
        KLog.i("错误：onFileTransformStatus:" + msgId);

        String friend = fileTransformStatus.getFriendId();
        if (!friend.equals(toChatUserId)) {
            return;
        }
        String LogIdIdResult = fileTransformStatus.getLogIdIdResult();
        int status = fileTransformStatus.getStatus();
        if (status == 1) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EMMessage EMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
                    if (EMMessage != null && LogIdIdResult != null) {
                        conversation.removeMessage(msgId);
                        EMMessage.setMsgId(LogIdIdResult + "");
                        EMMessage.setAcked(true);
                        sendMessageTo(EMMessage);
                        conversation.updateMessage(EMMessage);
                        if (isMessageListInited) {
                            easeChatMessageList.refresh();
                        }
                    }

                }
            });

        } else if (status == 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EMMessage EMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
                    conversation.removeMessage(msgId);
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                    Toast.makeText(getActivity(), R.string.Files_100M, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EMMessage EMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
                    conversation.removeMessage(msgId);
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                    Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void lockContentHeight(ChatKeyboard chatKeyboard) {
//        if (chatKeyboard.isLock()) {
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) easeChatMessageList.getLayoutParams();
//            params.height = easeChatMessageList.getHeight();
//            params.weight = 0.0F;
//        } else {
//            ((LinearLayout.LayoutParams) easeChatMessageList.getLayoutParams()).weight = 1.0F;
//        }
    }

    /**
     * 释放被锁定的内容高度
     */
//    private void unLockContentHeight() {
//        ((LinearLayout.LayoutParams) easeChatMessageList.getLayoutParams()).weight = 1.0F;
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
        KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData5_initView" + currentPage);
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        // message list layout
        easeChatMessageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            easeChatMessageList.setShowUserNick(true);
//        easeChatMessageList.setAvatarShape(1);
        listView = easeChatMessageList.getListView();
        kickedForOfflineLayout = getView().findViewById(R.id.layout_alert_kicked_off);
        kickedForOfflineLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatRoomViewCreation();
            }
        });
        voiceRecorderView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        inputMenu.bindContentView(easeChatMessageList);
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
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    isRecording = true;
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    isRecording = false;
//                }
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

        swipeRefreshLayout = easeChatMessageList.getSwipeRefreshLayout();
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
        if (UserDataManger.curreantfriendUserData == null)
            return;
        String usernameSouce = new String(RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getNickName()));
        if (UserDataManger.curreantfriendUserData.getRemarks() != null && !UserDataManger.curreantfriendUserData.getRemarks().equals("")) {
            usernameSouce = new String(RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getRemarks()));
        }
        titleBar.setTitle(usernameSouce);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if (EaseUserUtils.getUserInfo(toChatUserId) != null) {
                EaseUser user = EaseUserUtils.getUserInfo(toChatUserId);
                if (user != null) {
                    titleBar.setTitle(user.getNick());
                }
            }
            titleBar.setRightImageResource(R.mipmap.data);
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

//        titleBar.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                easeChatMessageList.refreshSelectLast();
//            }
//        });

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }
        setDraft();
    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit() {
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        EMMessage lastMessage = conversation.getLastMessage();
       /* if (lastMessage != null)
            lastMessage.setUnread(false);*/
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
                        easeChatMessageList.refreshSelectLast();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void delMyMsgOnSuccess(String msgId) {
        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(msgId);
        if (forward_msg == null) {
            return;
        }
        if (conversation != null)
            conversation.removeMessage(msgId);
        //refresh ui
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
        if (forward_msg.getType().equals(EMMessage.Type.IMAGE)) {
            EMImageMessageBody imgBody = (EMImageMessageBody) forward_msg.getBody();
            String localUrl = imgBody.getLocalUrl();
            FileUtil.deleteFile(localUrl);
        } else if (forward_msg.getType().equals(EMMessage.Type.VIDEO)) {
            EMVideoMessageBody imgBody = (EMVideoMessageBody) forward_msg.getBody();
            String localUrl = imgBody.getLocalUrl();
            FileUtil.deleteFile(localUrl);
        } else if (forward_msg.getType().equals(EMMessage.Type.VOICE)) {
            EMVoiceMessageBody imgBody = (EMVoiceMessageBody) forward_msg.getBody();
            String localUrl = imgBody.getLocalUrl();
            FileUtil.deleteFile(localUrl);
        } else if (forward_msg.getType().equals(EMMessage.Type.FILE)) {
            EMNormalFileMessageBody imgBody = (EMNormalFileMessageBody) forward_msg.getBody();
            String localUrl = imgBody.getLocalUrl();
            FileUtil.deleteFile(localUrl);
        }
        if (conversation != null) {
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            EMMessage eMMessage = conversation.getLastMessage();
            Gson gson = new Gson();
            Message Message = new Message();
            Message.setMsg("");
            if (eMMessage != null) {
                switch (eMMessage.getType()) {
                    case LOCATION:
                        break;
                    case IMAGE:
                        Message.setMsgType(1);
                        break;
                    case VOICE:
                        Message.setMsgType(2);
                        break;
                    case VIDEO:
                        Message.setMsgType(4);
                        break;
                    case TXT:
                        Message.setMsgType(0);
                        Message.setMsg(((EMTextMessageBody) eMMessage.getBody()).getMessage());
                        break;
                    case FILE:
                        Message.setMsgType(5);
                        break;
                    default:
                        break;
                }
                Message.setFileName("abc");
                Message.setFrom(eMMessage.getFrom());
                Message.setTo(eMMessage.getTo());
                Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            } else {
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
            }

        }

    }

    public void delMyMsgOnSending(String msgId) {
        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(msgId);
        if (conversation != null)
            conversation.removeMessage(msgId);
        //refresh ui
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
        //deleteFileMap.put(msgId, true);
        if (conversation != null) {
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            EMMessage eMMessage = conversation.getLastMessage();
            Gson gson = new Gson();
            Message Message = new Message();
            Message.setMsg("");
            if (eMMessage != null) {
                switch (eMMessage.getType()) {
                    case LOCATION:
                        break;
                    case IMAGE:
                        Message.setMsgType(1);
                        break;
                    case VOICE:
                        Message.setMsgType(2);
                        break;
                    case VIDEO:
                        Message.setMsgType(4);
                        break;
                    case TXT:
                        Message.setMsgType(0);
                        Message.setMsg(((EMTextMessageBody) eMMessage.getBody()).getMessage());
                        break;
                    case FILE:
                        Message.setMsgType(5);
                        break;
                    default:
                        break;
                }
                Message.setFileName("abc");

                Message.setFrom(eMMessage.getFrom());
                Message.setTo(eMMessage.getTo());
                Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            } else {
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
            }

        }
    }

    public void refreshReadData(String readMsgs) {
        if (readMsgs == null || readMsgs.equals("")) {
            return;
        }
        String[] readMsgsArray = readMsgs.split(",");
        int length = readMsgsArray.length;
        for (int i = 0; i < length; i++) {
            if (!readMsgsArray[i].equals("")) {
                EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(readMsgsArray[i]);
                if (forward_msg != null) {
                    forward_msg.setAcked(true);
                    forward_msg.setUnread(false);
                    if (conversation != null)
                        conversation.updateMessage(forward_msg);
                }

            }
        }
        //refresh ui
        easeChatMessageList.refresh();
    }

    public void setFriendStatus(int status) {
        friendStatus = status;
    }

    public void onToxFileSendFinished(int fileNumber, String key) {

        ToxFileData toxFileData = ConstantValue.INSTANCE.getSendToxFileDataMap().get(fileNumber + "");
        if (toxFileData != null) {
            if (!deleteFileMap.get(toxFileData.getFileId() + "")) {
                SendToxFileNotice sendToxFileNotice = new SendToxFileNotice(toxFileData.getFromId(), toxFileData.getToId(), toxFileData.getFileName(), toxFileData.getFileMD5(), toxFileData.getWidthAndHeight(), toxFileData.getFileSize(), toxFileData.getFileType().value(), toxFileData.getFileId(), toxFileData.getSrcKey(), toxFileData.getDstKey(), "SendFile");
                BaseData baseData = new BaseData(sendToxFileNotice);
                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                if (ConstantValue.INSTANCE.isAntox()) {
                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                } else {
                    ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                }


            } else {
                EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(toxFileData.getFileId() + "");
                KLog.i("tox文件发送成功后取消！");
            }
        }
        ConstantValue.INSTANCE.getSendToxFileDataMap().remove(fileNumber + "");
    }

    public void onAgreeReceivwFileStart(int fileNumber, String key, String fileName) {
        if (ConstantValue.INSTANCE.isAntox()) {
            FriendKey friendKey = new FriendKey(key);
            if (friendKey != null) {
                receiveToxFileNameMap.put(fileNumber + "", fileName);
                MessageHelper.sendAgreeReceiveFileFromKotlin(AppConfig.instance, fileNumber, friendKey);
            }
        } else {
            receiveToxFileNameMap.put(fileNumber + "", fileName);
        }

    }

    public String getToxReceiveFileName(int fileNumber, String key) {
        String fileName = receiveToxFileNameMap.get(fileNumber + "");
        return fileName;
    }

    public void onToxReceiveFileFinished(String fileName) {
        Message message = receiveToxFileDataMap.get(fileName);
        String msgId = receiveToxFileIdMap.get(fileName);
        if (message != null) {
            conversation.removeMessage(msgId);
            String files_dir = "";
            EMMessage messageData = null;
            if (conversation != null && ConstantValue.INSTANCE.getUserId() != null) {
                if (message != null) {
                    String fileNameTemp = message.getFileName();
                    String base58files_dir = PathUtils.getInstance().getTempPath() + "/" + fileNameTemp;
                    String files_dirTemp = PathUtils.getInstance().getFilePath() + "/" + fileNameTemp;
                    String fileKey = "";

                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                        fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(message.getPriKey());
                    } else {
                        //fileKey =  RxEncodeTool.getAESKey(message.getUserKey());
                    }
                    int code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir, files_dirTemp, fileKey);
                    if (code == 1) {
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        if (messageData != null) {
                            switch (message.getMsgType()) {
                                case 1:
                                    files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
                                    messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                                    break;
                                case 2:
                                    files_dir = PathUtils.getInstance().getVoicePath() + "/" + message.getFileName();
                                    int longTime = FileUtil.getAmrDuration(new File(files_dir));
                                    messageData = EMMessage.createVoiceSendMessage(files_dir, longTime, toChatUserId);
                                    break;
                                case 4:
                                    files_dir = PathUtils.getInstance().getVideoPath() + "/" + message.getFileName();
                                    String videoName = files_dir.substring(files_dir.lastIndexOf("/") + 1, files_dir.lastIndexOf(".") + 1);
                                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                    Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir);
                                    FileUtil.saveBitmpToFile(bitmap, thumbPath);
                                    messageData = EMMessage.createVideoSendMessage(files_dir, thumbPath, 1000, toChatUserId);
                                    break;
                                case 5:
                                    files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
                                    messageData = EMMessage.createFileSendMessage(files_dir, toChatUserId);
                                    break;
                            }
                            messageData.setFrom(message.getFrom());
                            messageData.setTo(message.getTo());
                            messageData.setUnread(false);

                            if (message.getFrom() == null) {
                                if (message.getSender() == 0) {
                                    messageData.setFrom(userId);
                                    messageData.setTo(toChatUserId);
                                    switch (message.getStatus()) {
                                        case 0:
                                            messageData.setDelivered(true);
                                            messageData.setAcked(false);
                                            messageData.setUnread(true);
                                            break;
                                        case 1:
                                            messageData.setAttribute("wh", message.getFileInfo());
                                            messageData.setDelivered(true);
                                            messageData.setAcked(true);
                                            messageData.setUnread(true);
                                            break;
                                        case 2:
                                            messageData.setDelivered(true);
                                            messageData.setAcked(true);
                                            messageData.setUnread(false);
                                            break;
                                        default:
                                            break;
                                    }
                                    messageData.setDirection(EMMessage.Direct.SEND);
                                } else {
                                    messageData.setFrom(toChatUserId);
                                    messageData.setTo(userId);
                                    messageData.setDirection(EMMessage.Direct.RECEIVE);
                                }
                            } else {
                                if (message.getFrom() != null && message.getFrom().equals(userId)) {
                                    messageData.setDirection(EMMessage.Direct.SEND);
                                } else {
                                    messageData.setDirection(EMMessage.Direct.RECEIVE);
                                }
                            }

                            messageData.setMsgTime(message.getTimeStamp() * 1000);
                            messageData.setMsgId(message.getMsgId() + "");
                            sendMessageTo(messageData);
                        }
                    }

                }
            }

        }
    }

    public void updatFriendName(JUserInfoPushRsp jUserInfoPushRsp) {
        if (jUserInfoPushRsp.getParams().getFriendId().equals(toChatUserId)) {

        }
    }

    public void onToxFileSendRsp(JSendToxFileRsp jSendToxFileRsp) {
        if (jSendToxFileRsp.getParams().getRetCode() == 0) {
            String msgId = jSendToxFileRsp.getParams().getFileId() + "";
            String msgServerId = jSendToxFileRsp.getParams().getMsgId() + "";
            EMMessage EMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
            conversation.removeMessage(msgId);
            EMMessage.setMsgId(msgServerId);
            EMMessage.setAcked(true);
            sendMessageTo(EMMessage);
            conversation.updateMessage(EMMessage);

            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void refreshData(List<Message> messageList, String UserId, String FriendId) {
        toChatUserId = FriendId;
        KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData0_" + conversation + "_" + toChatUserId);
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(FriendId, EaseCommonUtils.getConversationType(chatType), true);
        KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData1_" + conversation + "_" + toChatUserId);
        if (conversation != null) {
            if (currentPage == 0) {
                conversation.clearAllMessages();
                KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData2_" + conversation.getAllMessages().size());
            }
            if (isMessageListInited) {
//                easeChatMessageList.refresh();
            }
        }
        int size = messageList.size();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 50);
        if (size > 0) {
            currentPage++;
        } else {
            if (currentPage != 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.nomore, Toast.LENGTH_SHORT).show();
                    }
                }, 50);
            }
            return;
        }
        if (messageListTemp == null) {
            messageListTemp = messageList;
        }
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData3_" + conversation.getAllMessages().size());
        ArrayList<EMMessage> messages = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Message Message = messageList.get(i);
            EMMessage message = null;
            String msgSouce = "";
            if (Message.getMsg() != null && !Message.getMsg().equals("")) {
                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                    if (Message.getSender() == 0) {
                        msgSouce = LibsodiumUtil.INSTANCE.DecryptMyMsg(Message.getMsg(), Message.getNonce(), Message.getPriKey());
                    } else {
                        msgSouce = LibsodiumUtil.INSTANCE.DecryptFriendMsg(Message.getMsg(), Message.getNonce(), FriendId, Message.getSign());
                    }
                } else {
                    //msgSouce =  RxEncodeTool.RestoreMessage( Message.getUserKey(),Message.getMsg());
                }
            }
            if (msgSouce != null && !msgSouce.equals("")) {
                Message.setMsg(msgSouce);
            }
            switch (Message.getMsgType()) {
                case 0:
                    message = EMMessage.createTxtSendMessage(Message.getMsg(), toChatUserId);
                    break;
                case 1:
                    String ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_bg.xml";
                    String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + Message.getFileName();
                    File filesFile = new File(files_dir);
                    if (filesFile.exists()) {
                        message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                        if (Message.getFileInfo() != null) {
                            message.setAttribute("wh", Message.getFileInfo());
                        }
                    } else {
                        message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                        if (Message.getFileInfo() != null) {
                            message.setAttribute("wh", Message.getFileInfo());
                        }
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getImagePath() + "/";
                            if (Message.getSender() == 0) {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getSign(),"0");
                            } else {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getPriKey(),"0");
                            }

                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            if (Message.getSender() == 0) {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 1, 1, "PullFile");
                            } else {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 2, 1, "PullFile");
                            }
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                case 2:
                    String ease_default_amr = PathUtils.getInstance().getVoicePath() + "/" + "ease_default_amr.amr";
                    String files_dir_amr = PathUtils.getInstance().getVoicePath() + "/" + Message.getFileName();
                    File filesFileAmr = new File(files_dir_amr);
                    if (filesFileAmr.exists()) {
                        long sizea = filesFileAmr.length();
                        int longTime = FileUtil.getAmrDuration(filesFileAmr);
                        message = EMMessage.createVoiceSendMessage(files_dir_amr, longTime, toChatUserId);
                    } else {
                        message = EMMessage.createVoiceSendMessage(ease_default_amr, 1, toChatUserId);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getVoicePath() + "/";
                            if (Message.getSender() == 0) {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getSign(),"0");
                            } else {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getPriKey(),"0");
                            }

                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            if (Message.getSender() == 0) {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 1, 1, "PullFile");
                            } else {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 2, 1, "PullFile");
                            }
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                case 3:
                    break;
                case 4:
                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + "ease_default_image.png";
                    String files_dir_video = PathUtils.getInstance().getVideoPath() + "/" + Message.getFileName();
                    File filesFileAmrVideo = new File(files_dir_video);
                    if (filesFileAmrVideo.exists()) {
                        String videoName = files_dir_video.substring(files_dir_video.lastIndexOf("/") + 1, files_dir_video.lastIndexOf(".") + 1);
                        thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                        Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir_video);
                        FileUtil.saveBitmpToFile(bitmap, thumbPath);
                        message = EMMessage.createVideoSendMessage(files_dir_video, thumbPath, 1000, toChatUserId);
                    } else {
                        String videoPath = PathUtils.getInstance().getVideoPath() + "/" + "ease_default_vedio.mp4";
                        message = EMMessage.createVideoSendMessage(videoPath, thumbPath, 1000, toChatUserId);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getVideoPath() + "/";
                            if (Message.getSender() == 0) {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getSign(),"0");
                            } else {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getPriKey(),"0");
                            }
                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            if (Message.getSender() == 0) {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 1, 1, "PullFile");
                            } else {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 2, 1, "PullFile");
                            }
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

                            if (ConstantValue.INSTANCE.isAntox()) {
                                FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                case 5:
                    String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + "file_downloading.*";
                    String file_dir = PathUtils.getInstance().getImagePath().toString() + "/" + Message.getFileName();
                    File fileFile = new File(file_dir);
                    if (fileFile.exists()) {
                        message = EMMessage.createFileSendMessage(file_dir, toChatUserId);
                    } else {
                        message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getFilePath() + "/";
                            if (Message.getSender() == 0) {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getSign(),"0");
                            } else {
                                FileDownloadUtils.doDownLoadWork(filledUri, save_dir, getActivity(), Message.getMsgId(), handlerDown, Message.getPriKey(),"0");
                            }
                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            if (Message.getSender() == 0) {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 1, 1, "PullFile");
                            } else {
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 2, 1, "PullFile");
                            }
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                default:
                    break;
            }
            if (message == null) {
                continue;
            }
            message.setUnread(false);
            if (Message.getSender() == 0) {
                message.setFrom(userId);
                message.setTo(toChatUserId);
                switch (Message.getStatus()) {
                    case 0:
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        break;
                    case 1:
                        message.setDelivered(true);
                        message.setAcked(true);
                        message.setUnread(true);
                        break;
                    case 2:
                        message.setDelivered(true);
                        message.setAcked(true);
                        message.setUnread(false);
                        break;
                    default:
                        break;
                }
                message.setDirection(EMMessage.Direct.SEND);
            } else {
                message.setFrom(toChatUserId);
                message.setTo(userId);
                message.setDirection(EMMessage.Direct.RECEIVE);
            }
            message.setMsgTime(Message.getTimeStamp() * 1000);
            if (i == 0) {
                MsgStartId = Message.getMsgId();
            }
            message.setMsgId(Message.getMsgId() + "");
            if (Message.getMsgType() != 0) {
                receiveFileDataMap.put(Message.getMsgId() + "", Message);
            }
            messages.add(message);
//            sendMessageTo(message);
            if (i == size - 1) {
                Gson gson = new Gson();
                Message.setUnRead(false);
                Message.setStatus(2);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                if (currentPage == 1) {
                    if (Message.getSender() == 0) {
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                    } else {
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                    }
                }
            }
        }
        sendMessageTo(messages);
        List<MessageEntity> messageEntityList = AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().queryBuilder().where(MessageEntityDao.Properties.UserId.eq(userId), MessageEntityDao.Properties.FriendId.eq(toChatUserId)).list();
        KLog.i("开始插入没有发送成功的文本消息查询：userId：" + userId + " friendId:" + toChatUserId + " messageEntityList:" + messageEntityList);
        if (messageEntityList != null) {
            Collections.sort(messageEntityList, new Comparator<MessageEntity>() {
                @Override
                public int compare(MessageEntity lhs, MessageEntity rhs) {
                    long lsize = Long.valueOf(lhs.getSendTime());
                    long rsize = Long.valueOf(rhs.getSendTime());
                    return lsize == rsize ? 0 : (lsize > rsize ? 1 : -1);
                }
            });
            int len = messageEntityList.size();
            for (int i = 0; i < len; i++) {
                KLog.i("开始插入没有发送成功的文本消息：" + len);
                MessageEntity messageEntity = messageEntityList.get(i);
                String filePath = messageEntity.getFilePath();
                String msgId = messageEntity.getMsgId();
                String userIdL = messageEntity.getUserId();
                String friendId = messageEntity.getFriendId();
                String type = messageEntity.getType();
                int length = messageEntity.getVoiceTimeLen();
                if (messageEntity.getType().equals("0")) {
                    BaseData baseData = new Gson().fromJson(messageEntity.getBaseData(), BaseData.class);
                    LinkedTreeMap tm = (LinkedTreeMap) baseData.getParams();
                    Iterator it = tm.keySet().iterator();
                    String Msg = "";
                    String Nonce = "";
                    String PriKey = "";
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        Msg = (String) tm.get("Msg");
                        Nonce = (String) tm.get("Nonce");
                        PriKey = (String) tm.get("PriKey");
                        break;
                    }
                    if (!PriKey.equals("") && !Nonce.equals("") && !PriKey.equals("")) {
                        String msgSouce = LibsodiumUtil.INSTANCE.DecryptMyMsg(Msg, Nonce, PriKey);
                        EMMessage message = EMMessage.createTxtSendMessage(msgSouce, toChatUserId);
                        message.setFrom(userIdL);
                        message.setTo(friendId);
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        message.setMsgId(messageEntity.getMsgId());
                        sendMessageTo(message);
                    }

                } else {
                    if (!filePath.equals("")) {
                        switch (type) {
                            case "1":
                                EMMessage message = EMMessage.createImageSendMessage(filePath, true, friendId);
                                Bitmap bitmap1 = BitmapFactory.decodeFile(filePath);
                                String widthAndHeight = "," + bitmap1.getWidth() + "*" + bitmap1.getHeight();
                                if (bitmap1 != null) {
                                    message.setAttribute("wh", widthAndHeight.replace(",", ""));
                                }
                                message.setFrom(userIdL);
                                message.setTo(friendId);
                                message.setDelivered(true);
                                message.setAcked(false);
                                message.setUnread(true);
                                message.setMsgId(msgId);
                                sendMessageTo(message);
                                break;
                            case "2":
                                message = EMMessage.createVoiceSendMessage(filePath, length, friendId);
                                message.setFrom(userIdL);
                                message.setTo(friendId);
                                message.setDelivered(true);
                                message.setAcked(false);
                                message.setUnread(true);
                                message.setMsgId(msgId);
                                sendMessageTo(message);
                                break;
                            case "3":
                                String videoName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".") + 1);
                                String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                Bitmap bitmap = EaseImageUtils.getVideoPhoto(filePath);
                                int videoLength = EaseImageUtils.getVideoDuration(filePath);
                                message = EMMessage.createVideoSendMessage(filePath, thumbPath, videoLength, friendId);
                                message.setFrom(userIdL);
                                message.setTo(friendId);
                                message.setDelivered(true);
                                message.setAcked(false);
                                message.setUnread(true);
                                message.setMsgId(msgId);
                                sendMessageTo(message);
                                break;
                            case "4":
                                message = EMMessage.createFileSendMessage(filePath, friendId);
                                message.setFrom(userIdL);
                                message.setTo(friendId);
                                message.setDelivered(true);
                                message.setAcked(false);
                                message.setUnread(true);
                                message.setMsgId(msgId);
                                sendMessageTo(message);
                                break;
                        }

                    }

                }

            }
        }
    }

    public void removeLastMessage() {
        if (conversation != null) {
            EMMessage eMMessage = conversation.getLastMessage();
            if (eMMessage != null) {
                conversation.removeMessage(eMMessage.getMsgId());
            }
        }
    }

    public void delFreindMsg(JDelMsgPushRsp jDelMsgRsp) {
        try {
            EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(jDelMsgRsp.getParams().getMsgId() + "");
            /*EMTextMessageBody var3 = new EMTextMessageBody(getResources().getString(R.string.withdrawn));
            forward_msg.addBody(var3);*/
            if (conversation != null)
                conversation.removeMessage(jDelMsgRsp.getParams().getMsgId() + "");
            //conversation.removeMessage(jDelMsgRsp.getParams().getDeleteMsgId()+"");
            //refresh ui
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
            if (forward_msg.getType().equals(EMMessage.Type.IMAGE)) {
                EMImageMessageBody imgBody = (EMImageMessageBody) forward_msg.getBody();
                String localUrl = imgBody.getLocalUrl();
                FileUtil.deleteFile(localUrl);
            } else if (forward_msg.getType().equals(EMMessage.Type.VIDEO)) {
                EMVideoMessageBody imgBody = (EMVideoMessageBody) forward_msg.getBody();
                String localUrl = imgBody.getLocalUrl();
                FileUtil.deleteFile(localUrl);
            } else if (forward_msg.getType().equals(EMMessage.Type.VOICE)) {
                EMVoiceMessageBody imgBody = (EMVoiceMessageBody) forward_msg.getBody();
                String localUrl = imgBody.getLocalUrl();
                FileUtil.deleteFile(localUrl);
            } else if (forward_msg.getType().equals(EMMessage.Type.FILE)) {
                EMNormalFileMessageBody imgBody = (EMNormalFileMessageBody) forward_msg.getBody();
                String localUrl = imgBody.getLocalUrl();
                FileUtil.deleteFile(localUrl);
            }

            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            EMMessage eMMessage = conversation.getLastMessage();
            Gson gson = new Gson();
            Message Message = new Message();
            Message.setMsg("");
            if (eMMessage != null) {
                switch (eMMessage.getType()) {
                    case LOCATION:
                        break;
                    case IMAGE:
                        Message.setMsgType(1);
                        break;
                    case VOICE:
                        Message.setMsgType(2);
                        break;
                    case VIDEO:
                        Message.setMsgType(4);
                        break;
                    case TXT:
                        Message.setMsgType(0);
                        Message.setMsg(((EMTextMessageBody) eMMessage.getBody()).getMessage());
                        break;
                    case FILE:
                        Message.setMsgType(5);
                        break;
                    default:
                        break;
                }
                Message.setFileName("abc");
                Message.setFrom(eMMessage.getFrom());
                Message.setTo(eMMessage.getTo());
                Message.setTimeStamp(System.currentTimeMillis() / 1000);

                /*String cachStr = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId,"");
                Message MessageLocal = gson.fromJson(cachStr, Message.class);
                int unReadCount = 0;
                if(MessageLocal != null && Integer.valueOf(MessageLocal.getUnReadCount()) != null ){
                    unReadCount = MessageLocal.getUnReadCount();
                }
                if(unReadCount >0)
                {
                    Message.setUnReadCount(unReadCount -1);
                }else{
                    Message.setUnReadCount(0);
                }*/
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            } else {
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
            }


        } catch (Exception e) {

        }
    }

    protected void onMessageListInit() {
        easeChatMessageList.init(toChatUserId, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        easeChatMessageList.getListView().setOnTouchListener(new OnTouchListener() {

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
        easeChatMessageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
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
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
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
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        PullMsgReq pullMsgList = new PullMsgReq(userId, toChatUserId, 1, MsgStartId, 10, "PullMsg");
        BaseData sendData = new BaseData(pullMsgList);
        if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
            sendData = new BaseData(3, pullMsgList);
        }

        if (ConstantValue.INSTANCE.isWebsocketConnected()) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData);
        } else if (ConstantValue.INSTANCE.isToxConnected()) {
            BaseData baseData = sendData;
            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

            if (ConstantValue.INSTANCE.isAntox()) {
                FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
            } else {
                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
            }


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()) {
                    //chooseOriginalImage(cameraFile.getAbsolutePath());
                    sendImageMessage(cameraFile.getAbsolutePath(), true);
                    //sendCameraImageMessage(cameraFile.getAbsolutePath());
                }
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                if (videoFile != null && videoFile.exists()) {
                    //String thumbPath = EaseImageUtils.saveVideoThumb(videoFile,128,128,1);
                    sendVideoMessage(videoFile.getAbsolutePath(), false);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                KLog.i("选照片或者视频返回。。。");
                List<LocalMedia> list = data.getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
                KLog.i(list);
                if (list != null && list.size() > 0) {
                    inputMenu.hideExtendMenuContainer();

                    new Thread(() -> {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPictureType().contains("image")) {
                                sendImageMessage(list.get(i).getPath(), true);
                            } else {
                                //发视频
                                sendVideoMsg(list.get(i).getPath());
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.select_resource_error), Toast.LENGTH_SHORT).show();
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
            } else if (requestCode == REQUEST_CODE_FILE) {
                String filePath = data.getStringExtra("path");
                File file = new File(filePath);
                String md5Data = "";
                if (file.exists()) {
                    sendFileMessage(filePath);
                }
            } else if (requestCode == CHOOSE_PIC) {
                String filePath = data.getStringExtra("path");
                Boolean isCheck = data.getBooleanExtra("isCheck", false);
                sendImageMessage(filePath, !isCheck);
            }
        }
    }

    private void sendVideoMsg(String filePath) {
        KLog.i("要发送的文件的路径为：" +filePath);
        File file = new File(PathUtils.getInstance().getVideoPath(), System.currentTimeMillis() + ".mp4");
        KLog.i("要发送的文件的路径为111: " + file.getName());
        FileUtil.copyFile(filePath, file.getPath());
        inputMenu.post(() -> sendVideoMessage(file.getAbsolutePath(), false));
    }

    /**
     * 生成uri
     *
     * @param cameraFile
     * @return
     */
    private Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getActivity().getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(getActivity(), authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }


    @Override
    public void onResume() {
        super.onResume();
       /* if(isMessageListInited)
            easeChatMessageList.refresh();*/
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
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

        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUserId);
        }
    }

    /**
     * 设置草稿内容
     */
    private void setDraft() {
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        String content = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
        List<DraftEntity> drafts = AppConfig.instance.getMDaoMaster().newSession().getDraftEntityDao().queryBuilder().where(DraftEntityDao.Properties.UserId.eq(userId)).where(DraftEntityDao.Properties.ToUserId.eq(toChatUserId)).list();
        if (drafts != null && drafts.size() > 0) {
            DraftEntity draftEntity = drafts.get(0);
            inputMenu.setEdittext(draftEntity.getContent());
            KLog.i("设置草稿: " + draftEntity.getContent());
        }
        KLog.i("设置草稿: " + content);
//        if(content != null  && !content.equals(""))
//        {
//            Message message = new Gson().fromJson(content, Message.class);
//            if (message != null && message.getMsg()!= null && message.getMsg().contains("/[draft]/")) {
//                inputMenu.setEdittext(message.getMsg().replace("/[draft]/", ""));
//                LogUtil.addLog("设置的草稿为：" + message.getMsg().replace("/[draft]/", ""));
//                KLog.i("设置的草稿为：" + message.getMsg().replace("/[draft]/", ""));
//            }
//        }
    }

    /**
     * 保存草稿
     */
    private void saveDraft() {
        String draft = inputMenu.getEdittext().trim();
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        List<DraftEntity> drafts = AppConfig.instance.getMDaoMaster().newSession().getDraftEntityDao().queryBuilder().where(DraftEntityDao.Properties.UserId.eq(userId)).where(DraftEntityDao.Properties.ToUserId.eq(toChatUserId)).list();
        if (drafts != null && drafts.size() > 0) {
            DraftEntity draftEntity = drafts.get(0);
            draftEntity.setContent(draft);
            draftEntity.setTaimeStamp(Calendar.getInstance().getTimeInMillis());
            AppConfig.instance.getMDaoMaster().newSession().getDraftEntityDao().update(draftEntity);
        } else {
            DraftEntity draftEntity = new DraftEntity();
            draftEntity.setMsgType(0);
            draftEntity.setUserId(userId);
            draftEntity.setToUserId(toChatUserId);
            draftEntity.setContent(draft);
            draftEntity.setTaimeStamp(Calendar.getInstance().getTimeInMillis());
            AppConfig.instance.getMDaoMaster().newSession().getDraftEntityDao().insert(draftEntity);
        }
        KLog.i("草稿为：" + draft);
//        Message message = new Message();
//        message.setMsg("");
//        message.setMsgType(0);
//        message.setMsg("/[draft]/" +draft);
//        message.setFrom(userId);
//        message.setTo(toChatUserId);
//        message.setTimeStamp(System.currentTimeMillis() / 1000);
//        message.setUnReadCount(0);
//        String baseDataJson = new Gson().toJson(message);
//        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
    }

    public void onBackPressed() {
        hideKeyboard();
        saveDraft();
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
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
                        if (getActivity().isFinishing() || !toChatUserId.equals(value.getId()))
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
                easeChatMessageList.refreshSelectLast();
                //conversation.markMessageAsRead(message.getDeleteMsgId());
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
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
    }

    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_PICTURE:
                    if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
                        AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
                    }
                    selectPicFromLocal();
                    break;
                case ITEM_TAKE_PICTURE:
                    if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
                        AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
                    }
                    AndPermission.with(AppConfig.instance)
                            .requestCode(101)
                            .permission(
                                    Manifest.permission.CAMERA
                            )
                            .callback(permission)
                            .start();
                    break;
                case ITEM_SHORTVIDEO:
                    if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
                        AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
                    }
                    AndPermission.with(AppConfig.instance)
                            .requestCode(101)
                            .permission(
                                    Manifest.permission.CAMERA
                            )
                            .callback(permissionVideo)
                            .start();
                    break;
                case ITEM_LOCATION:
                    if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
                        AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
                    }
                    //startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    Toast.makeText(getActivity(), R.string.wait, Toast.LENGTH_SHORT).show();
                    break;
                case ITEM_FILE:
                    if (friendStatus == 0 && AppConfig.instance.getMessageReceiver() != null) {
                        AppConfig.instance.getMessageReceiver().getChatCallBack().queryFriend(UserDataManger.curreantfriendUserData.getUserId());
                    }
                    startActivityForResult(new Intent(getActivity(), SelectFileActivity.class).putExtra("fileType", 2), REQUEST_CODE_FILE);
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
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNick();
        }
        if (autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserId);
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            //String userIndex =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserIndex(),"");
            String msgId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            ;
            if (AppConfig.instance.getMessageReceiver() != null && UserDataManger.curreantfriendUserData.getSignPublicKey() != null) {
                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                    msgId = AppConfig.instance.getMessageReceiver().getChatCallBack().sendMsgV3(userId, UserDataManger.curreantfriendUserData.getUserId(), UserDataManger.curreantfriendUserData.getMiPublicKey(), content);
                } else {
                    AppConfig.instance.getMessageReceiver().getChatCallBack().sendMsg(userId, UserDataManger.curreantfriendUserData.getUserId(), UserDataManger.curreantfriendUserData.getSignPublicKey(), content);
                }

                if (message == null) {
                    return;
                }
                message.setFrom(userId);
                message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                message.setDelivered(true);
                message.setAcked(false);
                message.setUnread(true);
                message.setMsgId(msgId);
                currentSendMsg = message;
                ConstantValue.INSTANCE.getSendFileMsgMap().put(msgId, message);
                Gson gson = new Gson();
                Message Message = new Message();
                Message.setMsg(content);
                Message.setFrom(userId);
                Message.setTo(toChatUserId);
                Message.setStatus(0);
                Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                sendMessageTo(message);
            }

        }
    }

    public void deleteMessage() {
        if (conversation != null) {
            conversation.removeMessage(currentSendMsg.getMsgId());
            KLog.i("insertMessage:" + "EaseChatFragment" + "_upateMessage");
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            EMMessage eMMessage = conversation.getLastMessage();
            Gson gson = new Gson();
            Message Message = new Message();
            Message.setMsg("");
            if (eMMessage != null) {
                switch (eMMessage.getType()) {
                    case LOCATION:
                        break;
                    case IMAGE:
                        Message.setMsgType(1);
                        break;
                    case VOICE:
                        Message.setMsgType(2);
                        break;
                    case VIDEO:
                        Message.setMsgType(4);
                        break;
                    case TXT:
                        Message.setMsgType(0);
                        Message.setMsg(((EMTextMessageBody) eMMessage.getBody()).getMessage());
                        break;
                    case FILE:
                        Message.setMsgType(5);
                        break;
                    default:
                        break;
                }
                Message.setFileName("abc");
                Message.setFrom(eMMessage.getFrom());
                Message.setTo(eMMessage.getTo());
                Message.setTimeStamp(System.currentTimeMillis() / 1000);

               /* String cachStr = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId,"");
                Message MessageLocal = gson.fromJson(cachStr, Message.class);
                int unReadCount = 0;
                if(MessageLocal != null && Integer.valueOf(MessageLocal.getUnReadCount()) != null ){
                    unReadCount = MessageLocal.getUnReadCount();
                }
                Message.setUnReadCount(unReadCount);*/
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            } else {
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
            }
        }

    }

    public void upateMessage(JSendMsgRsp jSendMsgRsp) {
        if (jSendMsgRsp.getParams().getRetCode() == 0) {

        }

        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(jSendMsgRsp.getMsgid() + "");
        KLog.i("upateMessage:" + "forward_msg" + (forward_msg != null));
        LogUtil.addLog("upateMessage:", "forward_msg" + (forward_msg != null));
        switch (jSendMsgRsp.getParams().getRetCode()) {
            case 0:
                if (conversation != null) {
                    if (forward_msg != null) {
                        conversation.removeMessage(jSendMsgRsp.getMsgid() + "");
                        forward_msg.setMsgId(jSendMsgRsp.getParams().getMsgId() + "");
                        forward_msg.setAcked(true);
                        conversation.insertMessage(forward_msg);
                        KLog.i("insertMessage:" + "EaseChatFragment" + "_upateMessage");
                        if (isMessageListInited) {
                            easeChatMessageList.refresh();
                        }
                    }

                }
                break;
            case 1:
                if (conversation != null) {
                    conversation.removeMessage(jSendMsgRsp.getMsgid() + "");
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.DestinationUnreachable, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                if (conversation != null) {
                    conversation.removeMessage(jSendMsgRsp.getMsgid() + "");
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                }
                friendStatus = 1;
                String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jSendMsgRsp.getParams().getToId(), "");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserId);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessageTo(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUserId, name, identityCode);
        sendMessageTo(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File file = new File(filePath);
            boolean isHas = file.exists();
            if (isHas) {
                if (file.length() > 1024 * 1024 * 100) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.Files_100M, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                String imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                String typeName = filePath.substring(filePath.lastIndexOf("."));
                String leftName = "";
                if (imgeSouceName.contains("_")) {
                    leftName = imgeSouceName.substring(0, imgeSouceName.lastIndexOf("_"));
                    leftName = StringUitl.replaceALL(leftName, "_");
                    if (StringUitl.isNumeric(leftName)) {
                        leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                    }
                } else {
                    leftName = imgeSouceName;
                }
                if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
                {
                    leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
                }
                String fileName = leftName + "_" + ((int) (System.currentTimeMillis() / 1000)) + typeName;

                EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUserId);
                String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                message.setFrom(userId);
                message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                message.setDelivered(true);
                message.setAcked(false);
                message.setUnread(true);

                if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    message.setMsgId(uuid);
                    currentSendMsg = message;
                    //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                    /*sendMsgLocalMap.put(uuid, false);
                    sendFilePathMap.put(uuid, filePath);
                    deleteFileMap.put(uuid, false);
                    sendFileFriendKeyMap.put(uuid, UserDataManger.curreantfriendUserData.getSignPublicKey());*/


                    //数据库记录
                    /*MessageEntity messageEntity  = new MessageEntity();
                    messageEntity.setUserId(userId);
                    messageEntity.setFriendId(UserDataManger.curreantfriendUserData.getUserId());
                    messageEntity.setSendTime(System.currentTimeMillis() +"");
                    messageEntity.setType("2");//这里要改
                    messageEntity.setMsgId(uuid);
                    messageEntity.setComplete(false);
                    messageEntity.setFilePath(filePath);
                    messageEntity.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                    messageEntity.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                    messageEntity.setVoiceTimeLen(length);
                    KLog.i("消息数据增加语音文件：userId："+userId +" friendId:"+UserDataManger.curreantfriendUserData.getUserId());
                    AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);
*/

                   /* String fileKey = RxEncryptTool.generateAESKey();
                    byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                    byte[] friend = RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getSignPublicKey());
                    byte[] SrcKey = new byte[256];
                    byte[] DstKey = new byte[256];
                    try {

                        if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                            DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                        } else {
                            SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                            DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                        }
                        sendFileKeyByteMap.put(uuid, fileKey.substring(0, 16));
                        sendFileMyKeyByteMap.put(uuid, SrcKey);
                        sendFileFriendKeyByteMap.put(uuid, DstKey);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                    SendFileInfo SendFileInfo = new SendFileInfo();
                    SendFileInfo.setUserId(userId);
                    SendFileInfo.setFriendId(toChatUserId);
                    SendFileInfo.setFiles_dir(filePath);
                    SendFileInfo.setMsgId(uuid);
                    SendFileInfo.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                    SendFileInfo.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                    SendFileInfo.setVoiceTimeLen(length);
                    SendFileInfo.setType("2");
                    SendFileInfo.setPorperty("0");
                    SendFileInfo.setSendTime(System.currentTimeMillis() + "");

                    AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);

                    /*String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                } else {
                    String strBase58 = Base58.encode(fileName.getBytes());
                    String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                    String fileKey = RxEncryptTool.generateAESKey();
                    int code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, fileKey.substring(0, 16));
                    if (code == 1) {
                        int uuid = (int) (System.currentTimeMillis() / 1000);
                        message.setMsgId(uuid + "");
                        currentSendMsg = message;
                        ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                        sendMsgLocalMap.put(uuid + "", false);
                        sendFilePathMap.put(uuid + "", base58files_dir);
                        deleteFileMap.put(uuid + "", false);
                        sendFileFriendKeyMap.put(uuid + "", UserDataManger.curreantfriendUserData.getSignPublicKey());
                        ToxFileData toxFileData = new ToxFileData();
                        toxFileData.setFromId(userId);
                        toxFileData.setToId(UserDataManger.curreantfriendUserData.getUserId());
                        File fileMi = new File(base58files_dir);
                        long fileSize = fileMi.length();
                        String fileMD5 = FileUtil.getFileMD5(fileMi);
                        toxFileData.setFileName(strBase58);
                        toxFileData.setFileMD5(fileMD5);
                        toxFileData.setFilePath(base58files_dir);
                        toxFileData.setFileSize((int) fileSize);
                        toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_AUDIO);
                        toxFileData.setFileId(uuid);
                        toxFileData.setPorperty("0");
                        String FriendPublicKey = UserDataManger.curreantfriendUserData.getSignPublicKey();
                        byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                        byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                        byte[] SrcKey = new byte[256];
                        byte[] DstKey = new byte[256];
                        try {

                            if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                            }
                        } catch (Exception e) {

                        }
                        toxFileData.setSrcKey(new String(SrcKey));
                        toxFileData.setDstKey(new String(DstKey));
                        String fileNumber = "";
                        if (ConstantValue.INSTANCE.isAntox()) {
                            FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);
                        } else {
                            fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64)) + "";
                        }
                        ConstantValue.INSTANCE.getSendToxFileDataMap().put(fileNumber, toxFileData);
                    }
                }
                Gson gson = new Gson();
                Message Message = new Message();
                Message.setMsgType(2);
                Message.setFileName(fileName);
                Message.setMsg("");
                Message.setFrom(userId);
                Message.setTo(toChatUserId);
                Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.Chat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                sendMessageTo(message);
            }
        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * 发送图片消息
     *
     * @param imagePath
     * @param isCompress
     */
    protected void sendImageMessage(String imagePath, boolean isCompress) {
        if (friendStatus != 0) {
            inputMenu.post(() -> Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show());
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    File file = new File(imagePath);
                    boolean isHas = file.exists();
                    if (isHas) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        String widthAndHeight = "," + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
                        KLog.i("图片的宽高为：" + widthAndHeight);
                        bitmap.recycle();
                        if (file.length() > 1024 * 1024 * 100) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.Files_100M, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        String imgeSouceName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));
                        String typeName = imagePath.substring(imagePath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0, imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName, "_");
                            if (StringUitl.isNumeric(leftName)) {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        } else {
                            leftName = imgeSouceName;
                        }
                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
                        {
                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
                        }
                        String fileName = leftName + "_" + ((int) (System.currentTimeMillis() / 1000)) + typeName;
                        String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + fileName;
                        int codeSave = FileUtil.copySdcardPicAndCompress(imagePath, files_dir, isCompress);
                        EMMessage message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setAttribute("wh", widthAndHeight.replace(",", ""));
                        message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            message.setMsgId(uuid);
                            currentSendMsg = message;
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                            /*sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, files_dir);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.curreantfriendUserData.getSignPublicKey());*/

                            if (codeSave == 1) {
                                SendFileInfo SendFileInfo = new SendFileInfo();
                                SendFileInfo.setUserId(userId);
                                SendFileInfo.setFriendId(toChatUserId);
                                SendFileInfo.setFiles_dir(files_dir);
                                SendFileInfo.setMsgId(uuid);
                                SendFileInfo.setWidthAndHeight(widthAndHeight);
                                SendFileInfo.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                                SendFileInfo.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                                SendFileInfo.setVoiceTimeLen(0);
                                SendFileInfo.setType("1");
                                SendFileInfo.setSendTime(System.currentTimeMillis() + "");
                                SendFileInfo.setPorperty("0");
                                AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                                //AppConfig.instance.getPNRouterServiceMessageSender().sendImageMessage(userId,toChatUserId,files_dir,uuid,UserDataManger.curreantfriendUserData.getSignPublicKey(), UserDataManger.curreantfriendUserData.getMiPublicKey());
                               /* String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                                EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                            } else {
                                Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = RxEncryptTool.generateAESKey();
                            int code = FileUtil.copySdcardToxPicAndEncrypt(imagePath, base58files_dir, fileKey.substring(0, 16), isCompress);
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.curreantfriendUserData.getSignPublicKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.curreantfriendUserData.getUserId());
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setWidthAndHeight(widthAndHeight.substring(1, widthAndHeight.length()));
                                toxFileData.setPorperty("0");
                                String FriendPublicKey = UserDataManger.curreantfriendUserData.getSignPublicKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);
                                } else {
                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64)) + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileDataMap().put(fileNumber, toxFileData);
                            } else {
                                Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                        Gson gson = new Gson();
                        Message Message = new Message();
                        Message.setMsgType(1);
                        Message.setFileName(fileName);
                        Message.setMsg("");
                        Message.setFrom(userId);
                        Message.setTo(toChatUserId);
                        Message.setTimeStamp(System.currentTimeMillis() / 1000);
                        Message.setUnReadCount(0);
                        Message.setChatType(ChatType.Chat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageTo(message);
                    } else {
                        Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }).start();

    }

    protected void sendCameraImageMessage(String imagePath) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                    File file = new File(imagePath);
                    boolean isHas = file.exists();
                    if (isHas) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        String widthAndHeight = "," + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
                        bitmap.recycle();
                        EMMessage message = EMMessage.createImageSendMessage(imagePath, true, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        currentSendMsg = message;

                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                            sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, imagePath);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.curreantfriendUserData.getSignPublicKey());

                            String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                } else {
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                }
                                sendFileKeyByteMap.put(uuid, fileKey.substring(0, 16));
                                sendFileMyKeyByteMap.put(uuid, SrcKey);
                                sendFileFriendKeyByteMap.put(uuid, DstKey);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin", widthAndHeight));
                        } else {
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = RxEncryptTool.generateAESKey();
                            int code = FileUtil.copySdcardToxFileAndEncrypt(imagePath, base58files_dir, fileKey.substring(0, 16));
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.curreantfriendUserData.getSignPublicKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.curreantfriendUserData.getUserId());
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setWidthAndHeight(widthAndHeight.substring(1, widthAndHeight.length()));
                                toxFileData.setPorperty("0");
                                String FriendPublicKey = UserDataManger.curreantfriendUserData.getSignPublicKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);
                                } else {
                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64)) + "";
                                }
                                ConstantValue.INSTANCE.getSendToxFileDataMap().put(fileNumber, toxFileData);
                            }
                        }

                        sendMessageTo(message);
                    } else {
                        Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            }

        }).start();

    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUserId);
        sendMessageTo(message);
    }

    protected void sendVideoMessage(String videoPath, boolean isLocal) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    File file = new File(videoPath);
                    boolean isHas = file.exists();
                    if (isHas) {
                        if (file.length() > 1024 * 1024 * 100) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.Files_100M, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        String imgeSouceName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf("."));
                        String typeName = videoPath.substring(videoPath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0, imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName, "_");
                            if (StringUitl.isNumeric(leftName)) {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        } else {
                            leftName = imgeSouceName;
                        }
                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
                        {
                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
                        }
                        String videoFileName = leftName + "_" + ((int) (System.currentTimeMillis() / 1000)) + typeName;
                        //String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf("."));
                        String thumbPath = PathUtils.getInstance().getImagePath() + "/" + leftName + ".png";
                        Bitmap bitmap = EaseImageUtils.getVideoPhoto(videoPath);
                        int videoLength = EaseImageUtils.getVideoDuration(videoPath);
                        FileUtil.saveBitmpToFile(bitmap, thumbPath);
                        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);

                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            message.setMsgId(uuid);
                            currentSendMsg = message;
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                          /*  sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, videoPath);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.curreantfriendUserData.getSignPublicKey());*/


                            //数据库记录
                            /*MessageEntity messageEntity  = new MessageEntity();
                            messageEntity.setUserId(userId);
                            messageEntity.setFriendId(UserDataManger.curreantfriendUserData.getUserId());
                            messageEntity.setSendTime(System.currentTimeMillis() +"");
                            messageEntity.setType("3");//这里要改
                            messageEntity.setMsgId(uuid);
                            messageEntity.setComplete(false);
                            messageEntity.setFilePath(videoPath);
                            messageEntity.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            messageEntity.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                            KLog.i("消息数据增加视频文件：userId："+userId +" friendId:"+UserDataManger.curreantfriendUserData.getUserId());
                            AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);*/


                         /*   String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                } else {
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                }
                                sendFileKeyByteMap.put(uuid, fileKey.substring(0, 16));
                                sendFileMyKeyByteMap.put(uuid, SrcKey);
                                sendFileFriendKeyByteMap.put(uuid, DstKey);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }*/
                            SendFileInfo SendFileInfo = new SendFileInfo();
                            SendFileInfo.setUserId(userId);
                            SendFileInfo.setFriendId(toChatUserId);
                            SendFileInfo.setFiles_dir(videoPath);
                            SendFileInfo.setMsgId(uuid);
                            SendFileInfo.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            SendFileInfo.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                            SendFileInfo.setVoiceTimeLen(0);
                            SendFileInfo.setType("3");
                            SendFileInfo.setSendTime(System.currentTimeMillis() + "");
                            SendFileInfo.setPorperty("0");
                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                            //AppConfig.instance.getPNRouterServiceMessageSender().sendVideoMessage(userId,toChatUserId,videoPath,uuid,UserDataManger.curreantfriendUserData.getSignPublicKey(), UserDataManger.curreantfriendUserData.getMiPublicKey());

                            /*String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/

                        } else {
                            String strBase58 = Base58.encode(videoFileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = RxEncryptTool.generateAESKey();
                            int code = FileUtil.copySdcardToxFileAndEncrypt(videoPath, base58files_dir, fileKey.substring(0, 16));
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.curreantfriendUserData.getSignPublicKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.curreantfriendUserData.getUserId());
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_MEDIA);
                                toxFileData.setFileId(uuid);
                                toxFileData.setPorperty("0");
                                String FriendPublicKey = UserDataManger.curreantfriendUserData.getSignPublicKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);
                                } else {
                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64)) + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileDataMap().put(fileNumber, toxFileData);
                            }
                        }
                        Gson gson = new Gson();
                        Message Message = new Message();
                        Message.setMsgType(4);
                        Message.setFileName(videoFileName);
                        Message.setMsg("");
                        Message.setFrom(userId);
                        Message.setTo(toChatUserId);
                        Message.setTimeStamp(System.currentTimeMillis() / 1000);
                        Message.setUnReadCount(0);
                        Message.setChatType(ChatType.Chat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageTo(message);
                    } else {
                        Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }).start();


    }

    protected void sendFileMessage(String filePath) {
       /* EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUserId);
        sendMessageTo(message);*/
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    File file = new File(filePath);
                    boolean isHas = file.exists();
                    if (isHas) {
                        if (file.length() > 1024 * 1024 * 100) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.Files_100M, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        String imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                        String typeName = filePath.substring(filePath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0, imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName, "_");
                            if (StringUitl.isNumeric(leftName)) {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        } else {
                            leftName = imgeSouceName;
                        }
                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
                        {
                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
                        }
                        String fileName = leftName + "_" + ((int) (System.currentTimeMillis() / 1000)) + typeName;
                        String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + fileName;
                        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.curreantfriendUserData.getUserId());
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);

                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            int result = FileUtil.copyAppFileToSdcard(filePath, files_dir);
                            if (result == 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            message.setMsgId(uuid);
                            currentSendMsg = message;
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                            /*sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, files_dir);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.curreantfriendUserData.getSignPublicKey());*/


                            //数据库记录
                           /* MessageEntity messageEntity  = new MessageEntity();
                            messageEntity.setUserId(userId);
                            messageEntity.setFriendId(UserDataManger.curreantfriendUserData.getUserId());
                            messageEntity.setSendTime(System.currentTimeMillis() +"");
                            messageEntity.setType("4");//这里要改
                            messageEntity.setMsgId(uuid);
                            messageEntity.setComplete(false);
                            messageEntity.setFilePath(filePath);
                            messageEntity.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            messageEntity.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                            KLog.i("消息数据增加文件文件：userId："+userId +" friendId:"+UserDataManger.curreantfriendUserData.getUserId());
                            AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);*/


                           /* String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                } else {
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                }
                                sendFileKeyByteMap.put(uuid, fileKey.substring(0, 16));
                                sendFileMyKeyByteMap.put(uuid, SrcKey);
                                sendFileFriendKeyByteMap.put(uuid, DstKey);
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }*/
                            SendFileInfo SendFileInfo = new SendFileInfo();
                            SendFileInfo.setUserId(userId);
                            SendFileInfo.setFriendId(toChatUserId);
                            SendFileInfo.setFiles_dir(files_dir);
                            SendFileInfo.setMsgId(uuid);
                            SendFileInfo.setFriendSignPublicKey(UserDataManger.curreantfriendUserData.getSignPublicKey());
                            SendFileInfo.setFriendMiPublicKey(UserDataManger.curreantfriendUserData.getMiPublicKey());
                            SendFileInfo.setVoiceTimeLen(0);
                            SendFileInfo.setType("4");
                            SendFileInfo.setSendTime(System.currentTimeMillis() + "");
                            SendFileInfo.setPorperty("0");
                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                            //AppConfig.instance.getPNRouterServiceMessageSender().sendFileMessage(userId,toChatUserId,files_dir,uuid,UserDataManger.curreantfriendUserData.getSignPublicKey(), UserDataManger.curreantfriendUserData.getMiPublicKey());

                           /* String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                        } else {
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = RxEncryptTool.generateAESKey();
                            int code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, fileKey.substring(0, 16));
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.curreantfriendUserData.getSignPublicKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.curreantfriendUserData.getUserId());
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_FILE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setPorperty("0");
                                String FriendPublicKey = UserDataManger.curreantfriendUserData.getSignPublicKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);
                                } else {
                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64)) + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileDataMap().put(fileNumber, toxFileData);
                            }

                        }
                        Gson gson = new Gson();
                        Message Message = new Message();
                        Message.setMsgType(5);
                        Message.setFileName(fileName);
                        Message.setMsg("");
                        Message.setFrom(userId);
                        Message.setTo(toChatUserId);
                        Message.setTimeStamp(System.currentTimeMillis() / 1000);
                        Message.setUnReadCount(0);
                        Message.setChatType(ChatType.Chat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageTo(message);
                    } else {
                        Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }).start();
    }

    /**
     * 接受文字和表情消息
     *
     * @param jPushMsgRsp
     */
    public void receiveTxtMessage(JPushMsgRsp jPushMsgRsp) {
        String msgSouce = RxEncodeTool.RestoreMessage(jPushMsgRsp.getParams().getDstKey(), jPushMsgRsp.getParams().getMsg());
        if (msgSouce != null && !msgSouce.equals("")) {
            jPushMsgRsp.getParams().setMsg(msgSouce);
        }
        EMMessage message = EMMessage.createTxtSendMessage(jPushMsgRsp.getParams().getMsg(), toChatUserId);
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(jPushMsgRsp.getParams().getMsgId() + "");
        message.setFrom(jPushMsgRsp.getParams().getFromId());
        message.setTo(jPushMsgRsp.getParams().getToId());

        Gson gson = new Gson();
        Message Message = new Message();
        Message.setMsg(jPushMsgRsp.getParams().getMsg());
        Message.setMsgId(jPushMsgRsp.getParams().getMsgId());
        Message.setFrom(jPushMsgRsp.getParams().getFromId());
        Message.setTo(jPushMsgRsp.getParams().getToId());
        Message.setTimeStamp(System.currentTimeMillis() / 1000);
        Message.setUnReadCount(0);
        Message.setChatType(ChatType.Chat);
        String baseDataJson = gson.toJson(Message);
        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
        if (Message.getSender() == 0) {
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jPushMsgRsp.getParams().getFromId(), baseDataJson);
        } else {
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jPushMsgRsp.getParams().getFromId(), baseDataJson);
        }
        sendMessageTo(message);
    }

    /**
     * 接受文字和表情消息V3
     *
     * @param jPushMsgRsp
     */
    public void receiveTxtMessageV3(JPushMsgRsp jPushMsgRsp) {
        String msgSouce = LibsodiumUtil.INSTANCE.DecryptFriendMsg(jPushMsgRsp.getParams().getMsg(), jPushMsgRsp.getParams().getNonce(), jPushMsgRsp.getParams().getFrom(), jPushMsgRsp.getParams().getSign());
        if (msgSouce != null && !msgSouce.equals("")) {
            jPushMsgRsp.getParams().setMsg(msgSouce);
        }
        EMMessage message = EMMessage.createTxtSendMessage(jPushMsgRsp.getParams().getMsg(), toChatUserId);
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(jPushMsgRsp.getParams().getMsgId() + "");
        message.setFrom(jPushMsgRsp.getParams().getFrom());
        message.setTo(jPushMsgRsp.getParams().getTo());

        Gson gson = new Gson();
        Message Message = new Message();
        Message.setMsg(jPushMsgRsp.getParams().getMsg());
        Message.setMsgId(jPushMsgRsp.getParams().getMsgId());
        Message.setFrom(jPushMsgRsp.getParams().getFrom());
        Message.setTo(jPushMsgRsp.getParams().getTo());
        Message.setTimeStamp(System.currentTimeMillis() / 1000);
        Message.setUnReadCount(0);
        Message.setChatType(ChatType.Chat);
        String baseDataJson = gson.toJson(Message);
        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
        if (Message.getSender() == 0) {
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jPushMsgRsp.getParams().getFrom(), baseDataJson);
        } else {
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jPushMsgRsp.getParams().getFrom(), baseDataJson);
        }
        sendMessageTo(message);
    }

    /**
     * 接受文件消息
     *
     * @param url
     * @param msgId
     * @param fromId
     * @param toId
     */
    public void receiveFileMessage(String url, String msgId, String fromId, String toId, int FileType, String fileInfo) {
        String files_dir = "";
        EMMessage message = null;
        KLog.i("收到了文件消息，。。。");
        switch (FileType) {
            case 1:
                files_dir = PathUtils.getInstance().getFilePath() + "/" + url;
                message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                message.setAttribute("wh", fileInfo);
                break;
            case 2:
                files_dir = PathUtils.getInstance().getFilePath() + "/" + url;
                int longTime = FileUtil.getAmrDuration(new File(files_dir));
                message = EMMessage.createVoiceSendMessage(files_dir, longTime, toChatUserId);
                break;
            case 4:
                files_dir = PathUtils.getInstance().getFilePath() + "/" + url;
                String videoName = files_dir.substring(files_dir.lastIndexOf("/") + 1, files_dir.lastIndexOf(".") + 1);
                String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir);
                FileUtil.saveBitmpToFile(bitmap, thumbPath);
                message = EMMessage.createVideoSendMessage(files_dir, thumbPath, 1000, toChatUserId);
                break;
            case 5:
                files_dir = PathUtils.getInstance().getFilePath() + "/" + url;
                message = EMMessage.createFileSendMessage(files_dir, toChatUserId);
                break;
            default:
                break;
        }
        if (message != null) {
            message.setDirection(EMMessage.Direct.RECEIVE);
            message.setMsgId(msgId);
            message.setFrom(fromId);
            message.setTo(toId);
            sendMessageTo(message);
        }

    }

    protected void sendMessageTo(EMMessage message) {
        if (message == null) {
            return;
        }
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }

        message.setMessageStatusCallback(messageStatusCallback);

        // Send message.
        //EMClient.getInstance().chatManager().sendMessageTo(message);
        //message.setDirection(EMMessage.Direct.RECEIVE );
        message.setStatus(EMMessage.Status.SUCCESS);
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        if (conversation != null) {
            //KLog.i("insertMessage:" + "EaseChatFragment"+"_sendMessageTo1");
            KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData4_" + conversation.getAllMessages().size());
            conversation.insertMessage(message);
            //refresh ui
            if (isMessageListInited) {
                //KLog.i("insertMessage:" + "EaseChatFragment"+"_sendMessageTo2_"+conversation.getAllMessages().size());
                easeChatMessageList.refresh();
//                easeChatMessageList.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        easeChatMessageList.refreshSelectLast();
//                    }
//                }, 500);
            }
        } else {
            new Thread(new Runnable() {

                public void run() {

                    try {
                        Thread.sleep(1000);
                        if (conversation == null)
                            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
                        if (conversation != null) {
                            KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData5_" + conversation.getAllMessages().size());
                            conversation.insertMessage(message);
                            //SpUtil.INSTANCE.putString(conversation.conversationId());
                            //refresh ui
                            if (isMessageListInited) {
                                easeChatMessageList.refreshSelectLast();
                            }
                        }
                    } catch (Exception e) {

                    }
                }

            }).start();
        }

        /*if(isMessageListInited) {
            easeChatMessageList.refreshSelectLast();
        }*/
    }

    protected void sendMessageTo(ArrayList<EMMessage> message) {
        KLog.i("批量添加数据");
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
        for (int i = 0; i < message.size(); i++) {
            if (chatFragmentHelper != null) {
                //set extension
                chatFragmentHelper.onSetMessageAttributes(message.get(i));
            }
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                message.get(i).setChatType(ChatType.GroupChat);
            } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                message.get(i).setChatType(ChatType.ChatRoom);
            }
            message.get(i).setMessageStatusCallback(messageStatusCallback);
            message.get(i).setStatus(EMMessage.Status.SUCCESS);
            if (conversation == null)
                conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
            if (conversation != null) {
                //KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData4_" + conversation.getAllMessages().size());
                conversation.insertMessage(message.get(i));
                //refresh ui
            } else {
                new Thread(new Runnable() {

                    public void run() {

                        try {
                            Thread.sleep(1000);
                            if (conversation == null)
                                conversation = EMClient.getInstance().chatManager().getConversation(toChatUserId, EaseCommonUtils.getConversationType(chatType), true);
                            if (conversation != null) {
                                KLog.i("insertMessage:" + "EaseChatFragment" + "_refreshData5_" + conversation.getAllMessages().size());
                                conversation.insertMessage(message.get(0));
                                //SpUtil.INSTANCE.putString(conversation.conversationId());
                                //refresh ui
                                if (isMessageListInited) {
                                    easeChatMessageList.refreshSelectLast();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                }).start();
            }
        }
        easeChatMessageList.refreshTo(message.size());
    }


    //===================================================================================

    protected EMCallBack messageStatusCallback = new EMCallBack() {
        @Override
        public void onSuccess() {
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            Log.i("EaseChatRowPresenter", "onError: " + code + ", error: " + error);
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            Log.i(TAG, "onProgress: " + progress);
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
        }
    };

    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
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
            chooseOriginalImage(picturePath);
            //sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            chooseOriginalImage(file.getAbsolutePath());
            //sendImageMessage(file.getAbsolutePath());
        }
        inputMenu.hideExtendMenuContainer();
    }

    protected void chooseOriginalImage(String path) {
        Intent intent = new Intent(getContext(), EaseShowChooseImageActivity.class);
        File file = new File(path);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
            intent.putExtra("path", path);
            startActivityForResult(intent, CHOOSE_PIC);
        } else {
            Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
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
        cameraFile = new File(PathUtils.getInstance().getTempPath(), System.currentTimeMillis() / 1000 + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        try {
            cameraFile.getParentFile().mkdirs();
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                    REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.Permissionerror, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * capture new video
     */
    protected void selectVideoFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        videoFile = new File(PathUtils.getInstance().getVideoPath(), System.currentTimeMillis() / 1000 + ".mp4");
        KLog.i(videoFile.getPath());
        //noinspection ResultOfMethodCallIgnored
        videoFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_VIDEO_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), videoFile)).putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30).putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0),
                REQUEST_CODE_VIDEO);
    }

    /**
     * select local image
     * //todo
     */
    protected void selectPicFromLocal() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .forResult(REQUEST_CODE_LOCAL);
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
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    easeChatMessageList.refresh();
                    haveMoreData = true;
                }
            }
        }, true).show();
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
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
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
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
                    sendImageMessage(filePath, false);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
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

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
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
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
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
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

    protected Handler handlerDown = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x404:
                    Bundle errdata = msg.getData();
                    String errmsgId = errdata.getInt("msgID") + "";
                    Message messageError = receiveFileDataMap.get(errmsgId);
                   /* getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),messageError.getFileName()+ " Download failed", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    break;
                case 0x55:
                    if (conversation != null && ConstantValue.INSTANCE.getUserId() != null) {
                        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                        Bundle data = msg.getData();
                        String msgId = data.getInt("msgID") + "";
                        Message message = receiveFileDataMap.get(msgId);
                        conversation.removeMessage(msgId);
                        String files_dir = "";
                        EMMessage messageData = null;
                        if (message != null) {
                            switch (message.getMsgType()) {
                                case 1:
                                    files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
                                    messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                                    if (messageData == null) {
                                        return;
                                    }
                                    if (message.getFileInfo() != null) {
                                        messageData.setAttribute("wh", message.getFileInfo());
                                    } else {
                                        messageData.setAttribute("wh", "");
                                    }
                                    break;
                                case 2:
                                    files_dir = PathUtils.getInstance().getVoicePath() + "/" + message.getFileName();
                                    int longTime = FileUtil.getAmrDuration(new File(files_dir));
                                    messageData = EMMessage.createVoiceSendMessage(files_dir, longTime, toChatUserId);
                                    break;
                                case 4:
                                    files_dir = PathUtils.getInstance().getVideoPath() + "/" + message.getFileName();
                                    int beginIndex = files_dir.lastIndexOf("/") + 1;
                                    int endIndex = files_dir.lastIndexOf(".") + 1;
                                    if (endIndex < beginIndex) {
                                        return;
                                    }
                                    String videoName = files_dir.substring(beginIndex, endIndex);
                                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                    Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir);
                                    FileUtil.saveBitmpToFile(bitmap, thumbPath);
                                    messageData = EMMessage.createVideoSendMessage(files_dir, thumbPath, 1000, toChatUserId);
                                    break;
                                case 5:
                                    files_dir = PathUtils.getInstance().getFilePath() + "/" + message.getFileName();
                                    messageData = EMMessage.createFileSendMessage(files_dir, toChatUserId);
                                    break;
                            }
                            if (messageData != null) {
                                messageData.setFrom(message.getFrom());
                                messageData.setTo(message.getTo());
                                messageData.setUnread(false);

                                if (message.getFrom() == null) {
                                    if (message.getSender() == 0) {
                                        messageData.setFrom(userId);
                                        messageData.setTo(toChatUserId);
                                        switch (message.getStatus()) {
                                            case 0:
                                                messageData.setDelivered(true);
                                                messageData.setAcked(false);
                                                messageData.setUnread(true);
                                                break;
                                            case 1:

                                                messageData.setDelivered(true);
                                                messageData.setAcked(true);
                                                messageData.setUnread(true);
                                                break;
                                            case 2:
                                                messageData.setDelivered(true);
                                                messageData.setAcked(true);
                                                messageData.setUnread(false);
                                                break;
                                            default:
                                                break;
                                        }
                                        messageData.setDirection(EMMessage.Direct.SEND);
                                    } else {
                                        messageData.setFrom(toChatUserId);
                                        messageData.setTo(userId);
                                        messageData.setDirection(EMMessage.Direct.RECEIVE);
                                    }
                                } else {
                                    if (message.getFrom() != null && message.getFrom().equals(userId)) {
                                        messageData.setDirection(EMMessage.Direct.SEND);
                                    } else {
                                        messageData.setDirection(EMMessage.Direct.RECEIVE);
                                    }
                                }

                                messageData.setMsgTime(message.getTimeStamp() * 1000);
                                messageData.setMsgId(message.getMsgId() + "");
                                sendMessageTo(messageData);
                            }
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    };
}
