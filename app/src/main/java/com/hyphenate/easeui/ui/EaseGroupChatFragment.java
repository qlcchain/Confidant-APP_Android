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
import android.os.Environment;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
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
import com.luck.picture.lib.observable.ImagesObservable;
import com.message.Message;
import com.smailnet.eamil.Utils.AESCipher;
import com.smailnet.eamil.Utils.AESToolsCipher;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.DraftEntity;
import com.stratagile.pnrouter.db.DraftEntityDao;
import com.stratagile.pnrouter.db.FriendEntity;
import com.stratagile.pnrouter.db.FriendEntityDao;
import com.stratagile.pnrouter.db.GroupEntity;
import com.stratagile.pnrouter.db.MessageEntity;
import com.stratagile.pnrouter.db.MessageEntityDao;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.db.UserEntityDao;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.FileForwardReq;
import com.stratagile.pnrouter.entity.GroupMsgPullReq;
import com.stratagile.pnrouter.entity.GroupSendFileDoneReq;
import com.stratagile.pnrouter.entity.JFileForwardRsp;
import com.stratagile.pnrouter.entity.JGroupMsgPushRsp;
import com.stratagile.pnrouter.entity.JGroupQuitRsp;
import com.stratagile.pnrouter.entity.JGroupSendFileDoneRsp;
import com.stratagile.pnrouter.entity.JGroupSendMsgRsp;
import com.stratagile.pnrouter.entity.JGroupSysPushRsp;
import com.stratagile.pnrouter.entity.JPullFileListRsp;
import com.stratagile.pnrouter.entity.JUserInfoPushRsp;
import com.stratagile.pnrouter.entity.PullFileReq;
import com.stratagile.pnrouter.entity.SendFileInfo;
import com.stratagile.pnrouter.entity.ToxFileData;
import com.stratagile.pnrouter.entity.UpdateAvatarReq;
import com.stratagile.pnrouter.entity.events.BeginDownloadForwad;
import com.stratagile.pnrouter.entity.events.ChatKeyboard;
import com.stratagile.pnrouter.entity.events.DownloadForwadSuccess;
import com.stratagile.pnrouter.entity.events.FileGroupTransformStatus;
import com.stratagile.pnrouter.entity.events.FileTransformEntity;
import com.stratagile.pnrouter.entity.events.FromChat;
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity;
import com.stratagile.pnrouter.ui.activity.file.SelectFileActivity;
import com.stratagile.pnrouter.ui.activity.group.GroupInfoActivity;
import com.stratagile.pnrouter.ui.activity.group.GroupMembersActivity;
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
import org.libsodium.jni.Sodium;

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

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 */
public class EaseGroupChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_FILE = 5;
    protected static final int REQUEST_CODE_VIDEO = 6;
    protected static final int REQUEST_CODE_ENTER_GROUP = 7;
    protected static final int SELECT_AT = 8;

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
    protected GroupEntity groupEntity;
    protected int friendStatus = 0;
    protected EaseChatMessageList easeChatMessageList;
    protected FrameLayout easeChatMessageListParent;
    public EaseChatInputMenu inputMenu;
    private String imputOld;
    private LinearLayout tipsparentRoot;
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
    private String fromActivity = "";

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
    private HashMap<String, String> sendTLogIdFileIdMap = new HashMap<>();
    private HashMap<String, String> forwordFileIdMap = new HashMap<>();
    List<LocalMedia> previewImages = new ArrayList<LocalMedia>();
    private long faBegin;
    private long faEnd;
    private boolean isContainAt = false;
    private Message sendMessageData;
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
        sendMsgLocalMap = new HashMap<>();
        sendFilePathMap = new HashMap<>();
        sendFileResultMap = new HashMap<>();
        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
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
        groupEntity = fragmentArgs.getParcelable(EaseConstant.EXTRA_CHAT_GROUP);
        this.turnOnTyping = turnOnTyping();
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessageAT() + userId + "_" + toChatUserId, "0");
        super.onActivityCreated(savedInstanceState);
    }
    public void setChatUserId(String id) {
        toChatUserId = id;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void groupInfoChange(GroupEntity groupEntity) {
        UserDataManger.currentGroupData = groupEntity;
        this.groupEntity = groupEntity;
        setUpView();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void groupQuit(JGroupQuitRsp groupEntity) {
        getActivity().finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeginDownloadForwad(BeginDownloadForwad beginDownloadForwad ) {
        receiveFileDataMap.put(beginDownloadForwad.getMsgId() + "", beginDownloadForwad.getMessage());
        JPullFileListRsp.ParamsBean.PayloadBean fileData = beginDownloadForwad.getFileData();
        if(fileData.getFileType() != 1)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.Start_downloading, Toast.LENGTH_SHORT).show();
                }
            });

        }

        EMMessage eMMessage = EMClient.getInstance().chatManager().getMessage(beginDownloadForwad.getMsgId());

        eMMessage.setAttribute("kong","1");
        eMMessage.setAttribute("fileSize",fileData.getFileSize());
        conversation.updateMessage(eMMessage);
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }

        /*conversation.removeMessage(beginDownloadForwad.getMsgId() + "");
        String ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.xml";
        EMMessage message = EMMessage.createFileSendMessage(ease_default_image, toChatUserId);
        String fileMiName = fileData.getFileName();
        String msgId = fileData.getMsgId()+"";
        String fileOrginName = new String(Base58.decode(fileMiName));
        String filePath = PathUtils.getInstance().getFilePath().toString() + "/" + fileOrginName;
        String fileMiPath = PathUtils.getInstance().getTempPath().toString() + "/" + fileOrginName;
        File file = new File(filePath);

        switch (fileData.getFileType())
        {
            case 1:
                ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.xml";
                message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                if (fileData.getFileInfo() != null) {
                    message.setAttribute("wh", fileData.getFileInfo());
                }
                break;
            case 2:
                break;
            case 4:
                String thumbPath = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.png";
                String videoPath = PathUtils.getInstance().getVideoPath() + "/" + "ease_default_fileForward_vedio.mp4";
                message = EMMessage.createVideoSendMessage(videoPath, thumbPath, fileData.getFileSize(), toChatUserId);
                break;
            case 5:
                File resultFile =FileUtil.getKongFile(fileOrginName);
                if(resultFile == null )
                {
                    break;
                }
                //FileUtil.drawableToFile(AppConfig.instance,R.mipmap.kong,fileOrginName,5);
                String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + fileOrginName;
                message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                break;
        }


        message.setMsgId(fileData.getMsgId()+"");
        message.setAttribute("kong","1");
        message.setAttribute("fileSize",fileData.getFileSize());
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        message.setFrom(userId);
        message.setTo(UserDataManger.curreantfriendUserData.getUserId());
        message.setDelivered(eMMessage.isDelivered());
        message.setAcked(eMMessage.isAcked());
        message.setUnread(eMMessage.isUnread());
        sendMessageTo(message);*/
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadForwadSuccess(DownloadForwadSuccess downloadForwadSuccess ) {
        String msgId = downloadForwadSuccess.getMsgId();
        if (conversation != null && ConstantValue.INSTANCE.getUserId() != null) {
            String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
            Message message = receiveFileDataMap.get(msgId);
            message.setMsgId(Integer.valueOf(msgId));
            conversation.removeMessage(msgId);
            String files_dir = "";
            EMMessage messageData = null;
            if (message != null) {
                switch (message.getMsgType()) {
                    case 1:
                        files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
                        messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                        messageData.setAttribute("wh", message.getFileInfo());

                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setCompressed(false);
                        localMedia.setDuration(0);
                        localMedia.setHeight(100);
                        localMedia.setWidth(100);
                        localMedia.setChecked(false);
                        localMedia.setCut(false);
                        localMedia.setMimeType(0);
                        localMedia.setNum(0);
                        localMedia.setPath(files_dir);
                        localMedia.setPictureType("image/jpeg");
                        localMedia.setPosition((int)message.getTimeStamp());
                        localMedia.setSortIndex((int)message.getMsgId());
                        previewImages.add(localMedia);
                        ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
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
                        FileUtil.saveBitmpToFileNoThread(bitmap, thumbPath,80);
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

                    //messageData.setMsgTime(message.getTimeStamp() * 1000);
                    messageData.setMsgId(msgId + "");
                    messageData.setMsgTime(Long.valueOf(msgId));
                    sendMessageTo(messageData);
                }
            }

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileTransformStatus(FileGroupTransformStatus fileTransformStatus) {
        String msgId = fileTransformStatus.getMsgid();
        KLog.i("错误：onFileTransformStatus:" + msgId);

        String friend = fileTransformStatus.getFriendId();
        if (!friend.equals(toChatUserId)) {
            return;
        }
        String LogIdIdResult = fileTransformStatus.getLogIdIdResult();
        int FileIdIdResult = fileTransformStatus.getFileId();
        int status = fileTransformStatus.getStatus();
        if (status == 1) {

            sendTLogIdFileIdMap.put(FileIdIdResult+"",msgId);
            /*getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EMMessage EMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
                    if (EMMessage != null && LogIdIdResult != null) {
                        //conversation.removeMessage(msgId);
                        EMMessage.setMsgId(FileIdIdResult + "");
                        //EMMessage.setAcked(true);
                        //sendMessageTo(EMMessage);
                        conversation.updateMessage(EMMessage);
                        *//*if (isMessageListInited) {
                            easeChatMessageList.refresh();
                        }*//*
                    }

                }
            });*/

        } else if (status == 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EMMessage eMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
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
        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData5_initView" + currentPage);
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        // message list layout
        easeChatMessageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        easeChatMessageListParent = (FrameLayout) getView().findViewById(R.id.message_listParent);
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

        tipsparentRoot = (LinearLayout) getView().findViewById(R.id.tipsparentRoot);
        //tipsparentRoot.setVisibility(View.VISIBLE);
        RelativeLayout tipsParent0 = (RelativeLayout) getView().findViewById(R.id.tipsParent0);
        tipsParent0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setVisibility(View.GONE);
            }
        });

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        inputMenu.bindContentView(easeChatMessageListParent);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                // send action:TypingBegin cmd msg.
                String inputxt = s.toString();
                typingHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
                String temp = imputOld;
                if(isContainAt)
                {
                    isContainAt = false;
                    return;
                }
                if(inputxt.contains("@"))
                {

                    if(imputOld != null && !imputOld.equals(""))
                    {
                        if(imputOld.lastIndexOf("@") != imputOld.length() -2)
                        {
                            if(inputxt.lastIndexOf("@") == 0 && inputxt.lastIndexOf("@") == inputxt.length() -1)
                            {
                                Intent intent1  = new Intent(getActivity(), GroupMembersActivity.class);
                                intent1.putExtra("from", "EaseGroupChatFragment");
                                intent1.putExtra(EaseConstant.EXTRA_CHAT_GROUP, groupEntity);
                                startActivityForResult(intent1, SELECT_AT);
                            }else if(inputxt.lastIndexOf("@") == inputxt.length() -1)
                            {
                                String preStr = inputxt.substring(inputxt.length() -2,inputxt.length() -1);
                                boolean result = StringUitl.isLetterDigit(preStr);
                                if(!result)
                                {
                                    Intent intent1  = new Intent(getActivity(), GroupMembersActivity.class);
                                    intent1.putExtra("from", "EaseGroupChatFragment");
                                    intent1.putExtra(EaseConstant.EXTRA_CHAT_GROUP, groupEntity);
                                    startActivityForResult(intent1, SELECT_AT);
                                }
                            }
                        }
                    }else{
                        if(inputxt.lastIndexOf("@") == 0 && inputxt.lastIndexOf("@") == inputxt.length() -1)
                        {
                            Intent intent1  = new Intent(getActivity(), GroupMembersActivity.class);
                            intent1.putExtra("from", "EaseGroupChatFragment");
                            intent1.putExtra(EaseConstant.EXTRA_CHAT_GROUP, groupEntity);
                            startActivityForResult(intent1, SELECT_AT);
                        }else if(inputxt.lastIndexOf("@") == inputxt.length() -1)
                        {
                            String preStr = inputxt.substring(inputxt.length() -2,inputxt.length() -1);
                            boolean result = StringUitl.isLetterDigit(preStr);
                            if(!result)
                            {
                                Intent intent1  = new Intent(getActivity(), GroupMembersActivity.class);
                                intent1.putExtra("from", "EaseGroupChatFragment");
                                intent1.putExtra(EaseConstant.EXTRA_CHAT_GROUP, groupEntity);
                                startActivityForResult(intent1, SELECT_AT);
                            }
                        }
                    }


                }
                imputOld = inputxt;
            }

            @Override
            public void onSendMessage(String content,String point,String AssocId,String AssocContent,String userName) {
                if(point.toLowerCase().contains("all"))
                {
                    point = "all";
                }
                sendTextMessage(content,point.toLowerCase(),AssocId,AssocContent,userName);
            }
            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content,"","","","");
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

    @Override
    protected void setUpView() {
        if (UserDataManger.currentGroupData == null) {
            return;
        }
        titleBar.setRightImageResource(R.mipmap.icon_more_3);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
        if (group != null) {
        }
        if (groupEntity.getRemark() == null || "".equals(groupEntity.getRemark())) {
            titleBar.setTitle(new String(RxEncodeTool.base64Decode(groupEntity.getGName())));
        } else {
            titleBar.setTitle(new String(RxEncodeTool.base64Decode(groupEntity.getRemark())));
        }
        // listen the event that user moved out group or group is dismissed
        groupListener = new GroupListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);

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
        int msgIdIndex = Integer.valueOf(forward_msg.getMsgId());
        ImagesObservable.getInstance().removeLocalMedia(msgIdIndex,"chat");
        if (conversation != null)
            conversation.removeMessage(msgId);
        //refresh ui
        if (isMessageListInited) {
            easeChatMessageList.refresh();
        }
        /*if (forward_msg.getType().equals(EMMessage.Type.IMAGE)) {
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
        }*/
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
                Message.setTo(toChatUserId);
                Message.setTimeStamp(eMMessage.getMsgTime() / 1000);
                //Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.GroupChat);
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
        if(forward_msg != null)
        {
            int msgIdIndex = Integer.valueOf(forward_msg.getMsgId());
            ImagesObservable.getInstance().removeLocalMedia(msgIdIndex,"chat");
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
                Message.setTo(toChatUserId);
                Message.setTimeStamp(eMMessage.getMsgTime() / 1000);
                //Message.setTimeStamp(System.currentTimeMillis() / 1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.GroupChat);
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

        ToxFileData toxFileData = ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().get(fileNumber + "");
        if (toxFileData != null) {
            if (!deleteFileMap.get(toxFileData.getFileId() + "")) {
                String fileBase58Name = toxFileData.getFileName();
                String fileMD5 = FileUtil.getFileMD5(new File(toxFileData.getFilePath()));
                String fileInfo = "";
                if (toxFileData.getFileType().value() == 1) {
                    if (toxFileData.getWidthAndHeight() != null && !toxFileData.getWidthAndHeight().equals("")) {
                        fileInfo = toxFileData.getWidthAndHeight();
                    } else {
                        fileInfo = "200.0000000*200.0000000";
                    }

                } else if (toxFileData.getFileType().value() == 4) {
                    fileInfo = "200.0000000*200.0000000";
                }
                int size = toxFileData.getFileSize();
                GroupSendFileDoneReq GroupSendFileDoneReq = new GroupSendFileDoneReq(toxFileData.getFromId(), toxFileData.getToId(), fileBase58Name, fileMD5, fileInfo, size, toxFileData.getFileType().value(), toxFileData.getFileId() + "", "GroupSendFileDone");
                if (ConstantValue.INSTANCE.isWebsocketConnected()) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(4, GroupSendFileDoneReq));
                } else if (ConstantValue.INSTANCE.isToxConnected()) {
                    BaseData baseData = new BaseData(4, GroupSendFileDoneReq);
                    String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                }

            } else {
                EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(toxFileData.getFileId() + "");
                KLog.i("tox文件发送成功后取消！");
            }
        }
        ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().remove(fileNumber + "");
    }

    public void onAgreeReceivwFileStart(int fileNumber, String key, String fileName) {
        if (ConstantValue.INSTANCE.isAntox()) {
            /*FriendKey friendKey = new FriendKey(key);
            if (friendKey != null) {
                receiveToxFileNameMap.put(fileNumber + "", fileName);
                MessageHelper.sendAgreeReceiveFileFromKotlin(AppConfig.instance, fileNumber, friendKey);
            }*/
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

                    if(message.getFileKey() != null && !message.getFileKey().equals(""))
                    {
                        fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(message.getFileKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());//,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!
                    }else{
                        fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    }

                    int code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir, files_dirTemp, fileKey);
                    if (code == 1) {
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        switch (message.getMsgType()) {
                            case 1:
                                files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
                                messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                                messageData.setAttribute("wh", message.getFileInfo());
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
                                FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                                messageData = EMMessage.createVideoSendMessage(files_dir, thumbPath, 1000, toChatUserId);
                                break;
                            case 5:
                                files_dir = PathUtils.getInstance().getImagePath() + "/" + message.getFileName();
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

                            //messageData.setMsgTime(message.getTimeStamp() * 1000);
                            messageData.setMsgTime(Long.valueOf(message.getMsgId()));
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

    public void onToxFileSendRsp(JGroupSendFileDoneRsp jSendToxFileRsp) {
        if (jSendToxFileRsp.getParams().getRetCode() == 0) {

            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            Gson gson = new Gson();
            sendMessageData.setTimeStamp(jSendToxFileRsp.getTimestamp());

            String baseDataJson = gson.toJson(sendMessageData);
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);


            String msgId = sendTLogIdFileIdMap.get(jSendToxFileRsp.getParams().getFileId() + "");
            String msgServerId = jSendToxFileRsp.getParams().getMsgId() + "";
            EMMessage eMMessage = EMClient.getInstance().chatManager().getMessage(msgId);
            conversation.removeMessage(msgId);
            eMMessage.setMsgId(msgServerId);
            eMMessage.setAcked(true);
            eMMessage.setMsgTime(Long.valueOf(msgServerId));
            sendMessageTo(eMMessage);
            conversation.updateMessage(eMMessage);
            if (eMMessage.getType().equals(EMMessage.Type.IMAGE))
            {
                LocalMedia localMedia = ImagesObservable.getInstance().getLocalMedia(Integer.valueOf(msgId),"chat");
                if(localMedia != null)
                {
                    previewImages.remove(localMedia);
                    localMedia.setSortIndex(Integer.valueOf(msgServerId));
                    previewImages.add(localMedia);
                    ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
                    ImagesObservable.getInstance().removeLocalMedia(Integer.valueOf(msgId),"chat");
                }

            }
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msgId = jSendToxFileRsp.getParams().getFileId() + "";
                    conversation.removeMessage(msgId);
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                    Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void refreshData(List<Message> messageList, String UserId, String FriendId) {
        toChatUserId = FriendId;
        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData0_" + conversation + "_" + toChatUserId);
        if (conversation == null)
            conversation = EMClient.getInstance().chatManager().getConversation(FriendId, EaseCommonUtils.getConversationType(chatType), true);
        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData1_" + conversation + "_" + toChatUserId);
        if (conversation != null) {
            if (currentPage == 0) {
                conversation.clearAllMessages();
                if(messageList.size() == 0)
                {
                    previewImages  = new ArrayList<LocalMedia>();
                    String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                    SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
                }
                KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData2_" + conversation.getAllMessages().size());
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
        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData3_" + conversation.getAllMessages().size());
        ArrayList<EMMessage> messages = new ArrayList<>();
        String msgIdStrLocal = SpUtil.INSTANCE.getString(AppConfig.instance,"insertTipMessage"+ "_" + toChatUserId,"");
        String msgIdStr = "";
        String userIdStr = "";
        ArrayList<String> nameArray =  new ArrayList<>();
        ArrayList<String> userIdArray =  new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            Message Message = messageList.get(i);
            if(Message.getAssocId() != 0)
            {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadAssocIdMessages(Message.getAssocId(),Message.getMsgId());
                    }
                }, 10);
            }
            if (Message.getFrom().equals("")) {
                Message.setFrom(userId);
            } else {
                if (!Message.getFrom().equals(userId)) {
                    List<UserEntity> userList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(Message.getFrom())).list();
                    if (userList.size() == 0)//群聊非好友成员数据
                    {
                        UserEntity UserEntityLocal = new UserEntity();
                        UserEntityLocal.setNickName(Message.getUserName());
                        UserEntityLocal.setUserId(Message.getFrom());
                        UserEntityLocal.setIndex("");
                        UserEntityLocal.setSignPublicKey(Message.getUserKey());
                        UserEntityLocal.setRouteId("");
                        UserEntityLocal.setRouteName("");
                        byte[] dst_public_MiKey_Friend = new byte[32];
                        int crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(Message.getUserKey()));
                        if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                            UserEntityLocal.setMiPublicKey(RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend));
                        }
                        UserEntityLocal.setRemarks("");
                        UserEntityLocal.setTimestamp(Calendar.getInstance().getTimeInMillis());
                        AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().insert(UserEntityLocal);
                    }
                    List<FriendEntity> friendList = AppConfig.instance.getMDaoMaster().newSession().getFriendEntityDao().queryBuilder().where(FriendEntityDao.Properties.UserId.eq(Message.getFrom())).list();
                    if (friendList.size() == 0)//群聊非好友成员数据
                    {
                        String fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(Message.getUserKey()));
                        String filePath = PathUtils.getInstance().getFilePath().toString() + "/" + fileBase58Name + ".jpg";
                        String fileMD5 = FileUtil.getFileMD5(new File(filePath));
                        if (fileMD5 == null) {
                            fileMD5 = "";
                        }
                        UpdateAvatarReq updateAvatarReq = new UpdateAvatarReq(userId, Message.getFrom(), fileMD5, "UpdateAvatar");
                        if (ConstantValue.INSTANCE.isWebsocketConnected()) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(4, updateAvatarReq));
                        } else if (ConstantValue.INSTANCE.isToxConnected()) {
                            BaseData baseData = new BaseData(4, updateAvatarReq);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                        }
                    }
                }

            }
            EMMessage message = null;
            String msgSouce = "";
            if (Message.getMsg() != null && !Message.getMsg().equals("")) {
                try {
                    String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    byte[] base64Scoure = RxEncodeTool.base64Decode(Message.getMsg());
                    msgSouce = new String(AESCipher.aesDecryptBytes(base64Scoure, aesKey.getBytes()));
                } catch (Exception e) {

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
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setCompressed(false);
                        localMedia.setDuration(0);
                        localMedia.setHeight(100);
                        localMedia.setWidth(100);
                        localMedia.setChecked(false);
                        localMedia.setCut(false);
                        localMedia.setMimeType(0);
                        localMedia.setNum(0);
                        localMedia.setPath(files_dir);
                        localMedia.setPictureType("image/jpeg");
                        localMedia.setPosition((int)Message.getTimeStamp());
                        localMedia.setSortIndex(Message.getMsgId());
                        previewImages.add(localMedia);
                    } else {
                        message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                        if (Message.getFileInfo() != null) {
                            message.setAttribute("wh", Message.getFileInfo());
                        }
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getImagePath() + "/";
                            if(Message.getFileKey() != null && !Message.getFileKey().equals(""))//判断是从文件管理转发还是聊天转发
                            {
                                String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                                String fileKey = RxEncodeTool.getSouceKey(Message.getFileKey(),aesKey);
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, fileKey,"1");
                            }else{
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()),save_dir, getActivity(), Message.getMsgId(), handlerDown, UserDataManger.currentGroupData.getUserKey(),"0");
                            }

                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 5, 1, "PullFile");
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
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
                            if(Message.getFileKey() != null && !Message.getFileKey().equals(""))//判断是从文件管理转发还是聊天转发
                            {
                                String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                                String fileKey = RxEncodeTool.getSouceKey(Message.getFileKey(),aesKey);
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, fileKey,"1");
                            }else{
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, UserDataManger.currentGroupData.getUserKey(),"0");
                            }
                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 5, 1, "PullFile");
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                case 3:
                    break;
                case 4:
                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_bg.xml";
                    String files_dir_video = PathUtils.getInstance().getVideoPath() + "/" + Message.getFileName();
                    File filesFileAmrVideo = new File(files_dir_video);
                    if (filesFileAmrVideo.exists()) {
                        String videoName = files_dir_video.substring(files_dir_video.lastIndexOf("/") + 1, files_dir_video.lastIndexOf(".") + 1);
                        thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                        Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir_video);
                        FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                        message = EMMessage.createVideoSendMessage(files_dir_video, thumbPath, 1000, toChatUserId);
                    } else {
                        String videoPath = PathUtils.getInstance().getVideoPath() + "/" + "ease_default_vedio.mp4";
                        message = EMMessage.createVideoSendMessage(videoPath, thumbPath, 1000, toChatUserId);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getVideoPath() + "/";
                            if(Message.getFileKey() != null && !Message.getFileKey().equals(""))//判断是从文件管理转发还是聊天转发
                            {
                                String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                                String fileKey = RxEncodeTool.getSouceKey(Message.getFileKey(),aesKey);
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, fileKey,"1");
                            }else{
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, UserDataManger.currentGroupData.getUserKey(),"0");
                            }
                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 5, 1, "PullFile");
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            } else {
                                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }

                        }

                    }
                    break;
                case 5:
                    String file_dir = PathUtils.getInstance().getImagePath().toString() + "/" + Message.getFileName();
                    File fileFile = new File(file_dir);
                    if (fileFile.exists()) {
                        if(fileFile.length() == 0 && Message.getFileSize() != null && Message.getFileSize() > 0)
                        {
                            File resultFile =  FileUtil.getKongFile(Message.getFileName());
                            if(resultFile == null )
                            {
                                continue;
                            }
//                            FileUtil.drawableToFile(AppConfig.instance,R.mipmap.doc_img_default,Message.getFileName(),5);
                            String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + Message.getFileName();
                            message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                            message.setAttribute("kong","1");
                            if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                                String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                                String save_dir = PathUtils.getInstance().getFilePath() + "/";
                                if(Message.getFileKey() != null && !Message.getFileKey().equals(""))//判断是从文件管理转发还是聊天转发
                                {
                                    String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                                    String fileKey = RxEncodeTool.getSouceKey(Message.getFileKey(),aesKey);
                                    FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown,fileKey,"1");
                                }else{
                                    FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, UserDataManger.currentGroupData.getUserKey(),"0");
                                }
                            } else {
                                receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                                receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                                String base58Name = Base58.encode(Message.getFileName().getBytes());
                                PullFileReq msgData;
                                msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 5, 1, "PullFile");
                                BaseData baseData = new BaseData(msgData);
                                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                                } else {
                                    ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                }

                            }
                        }else{
                            message = EMMessage.createFileSendMessage(file_dir, toChatUserId);
                        }

                    } else {
                        File resultFile =  FileUtil.getKongFile(Message.getFileName());
                        if(resultFile == null )
                        {
                            continue;
                        }
//                        FileUtil.drawableToFile(AppConfig.instance,R.mipmap.doc_img_default,Message.getFileName(),5);
                        String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + Message.getFileName();
                        message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                        message.setAttribute("kong","1");
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + Message.getFilePath();
                            String save_dir = PathUtils.getInstance().getFilePath() + "/";
                            if(Message.getFileKey() != null && !Message.getFileKey().equals(""))//判断是从文件管理转发还是聊天转发
                            {
                                String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                                String fileKey = RxEncodeTool.getSouceKey(Message.getFileKey(),aesKey);
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown,fileKey,"1");
                            }else{
                                FileDownloadUtils.doDownLoadWork(filledUri,Base58.encode(Message.getFileName().getBytes()), save_dir, getActivity(), Message.getMsgId(), handlerDown, UserDataManger.currentGroupData.getUserKey(),"0");
                            }
                        } else {
                            receiveToxFileDataMap.put(Base58.encode(Message.getFileName().getBytes()), Message);
                            receiveToxFileIdMap.put(Base58.encode(Message.getFileName().getBytes()), Message.getMsgId() + "");
                            String base58Name = Base58.encode(Message.getFileName().getBytes());
                            PullFileReq msgData;
                            msgData = new PullFileReq(toChatUserId, userId, base58Name, Message.getMsgId(), 5, 1, "PullFile");
                            BaseData baseData = new BaseData(msgData);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
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
            if (Message.getFrom().equals(userId)) {
                message.setFrom(Message.getFrom());
                message.setTo(toChatUserId);
                message.setDelivered(true);
                message.setAcked(true);
                message.setUnread(true);
               /* switch (Message.getStatus()) {
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
                        message.setUnread(true);
                        break;
                    default:
                        break;
                }*/
                message.setDirection(EMMessage.Direct.SEND);
            } else {
                message.setFrom(Message.getFrom());
                message.setTo(toChatUserId);
                message.setDirection(EMMessage.Direct.RECEIVE);
            }
            //message.setMsgTime(Message.getTimeStamp() * 1000);
            message.setMsgTime(Message.getMsgId());
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
                Message.setChatType(ChatType.GroupChat);
                String baseDataJson = gson.toJson(Message);
                if (currentPage == 1) {
                    SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                }
            }
            if(Message.getPoint() == 1 || Message.getPoint() == 2)
            {
               /* if(!userIdStr.contains(Message.getFrom()) && !msgIdStrLocal.contains(Message.getMsgId()+""))
                {
                    nameArray.add(Message.getUserName());
                    userIdArray.add(Message.getFrom());
                    userIdStr += Message.getFrom() +",";
                    msgIdStr += Message.getMsgId() +",";
                }*/
                nameArray.add(Message.getUserName());
                userIdArray.add(Message.getFrom());
                userIdStr += Message.getFrom() +",";
                msgIdStr += Message.getMsgId() +",";
            }
            SpUtil.INSTANCE.putString(AppConfig.instance,"insertTipMessage"+ "_" + toChatUserId,msgIdStrLocal+msgIdStr);
        }

        for(int k = 0 ; k< nameArray.size() ;k++)
        {
            String name = new String(RxEncodeTool.base64Decode(nameArray.get(k)));
            //(userIdArray.get(k),name +" "+ getString(R.string.remind_you_to_check_the_message),"1");
        }
       /* if(nameArray.size() >0)
        {
            new Thread(new Runnable(){
                public void run(){

                    try {
                        Thread.sleep(300);
                        if(tipsparentRoot !=null)
                        {
                            tipsparentRoot.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e)
                    {

                    }
                }
            }).start();

        }*/
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
                        String msgSouce = LibsodiumUtil.INSTANCE.DecryptMyMsg(Msg, Nonce, PriKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey(), ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
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

                                int time = (int) (System.currentTimeMillis() / 1000) +i;
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setCompressed(false);
                                localMedia.setDuration(0);
                                localMedia.setHeight(100);
                                localMedia.setWidth(100);
                                localMedia.setChecked(false);
                                localMedia.setCut(false);
                                localMedia.setMimeType(0);
                                localMedia.setNum(0);
                                localMedia.setPath(filePath);
                                localMedia.setPictureType("image/jpeg");
                                localMedia.setPosition(time);
                                localMedia.setSortIndex(time);
                                previewImages.add(localMedia);
                                ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
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
        ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
    }

    public void removeLastMessage() {
        if (conversation != null) {
            EMMessage eMMessage = conversation.getLastMessage();
            if (eMMessage != null) {
                conversation.removeMessage(eMMessage.getMsgId());
            }
        }
    }

    public void delFreindMsg(JGroupSysPushRsp jDelMsgRsp) {
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
            if(forward_msg != null)
            {
                int msgIdIndex = Integer.valueOf(forward_msg.getMsgId());
                ImagesObservable.getInstance().removeLocalMedia(msgIdIndex,"chat");
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
                Message.setTo(toChatUserId);
                Message.setTimeStamp(eMMessage.getMsgTime() / 1000);
                //Message.setTimeStamp(System.currentTimeMillis() / 1000);

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
                Message.setChatType(ChatType.GroupChat);
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
            public void onUserAvatarClick(String fromUserId) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(fromUserId);
                }
                String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                if(!userId.equals(fromUserId))
                {
                    List<UserEntity> userList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(fromUserId)).list();
                    if(userList.size() > 0)
                    {
                        UserEntity user = userList.get(0);
                        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }

                }
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                EMLog.i(TAG, "onResendClick");
                new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
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
            public void onUserAvatarLongClick(String userId) {
                /*if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }*/
                inputAtUsername(userId,true);
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
                }, 50);
            }
        });
    }

    private void loadMoreRoamingMessages() {

        swipeRefreshLayout.setRefreshing(true);
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        GroupMsgPullReq pullMsgList = new GroupMsgPullReq(userId, ConstantValue.INSTANCE.getCurrentRouterId(), UserDataManger.currentGroupData.getGId() + "", 0, MsgStartId, 10, 0,"GroupMsgPull");
        BaseData sendData = new BaseData(pullMsgList);
        if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
            sendData = new BaseData(4, pullMsgList);
        }

        if (ConstantValue.INSTANCE.isWebsocketConnected()) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData);
        } else if (ConstantValue.INSTANCE.isToxConnected()) {
            BaseData baseData = sendData;
            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

            if (ConstantValue.INSTANCE.isAntox()) {
                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
            } else {
                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
            }


        }

        if(currentPage > 1)
        {
            easeChatMessageList.scrollLast(0);
        }
    }
    private void loadAssocIdMessages(int AssocId,int msgID) {

        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        GroupMsgPullReq pullMsgList = new GroupMsgPullReq(userId, ConstantValue.INSTANCE.getCurrentRouterId(), UserDataManger.currentGroupData.getGId() + "", 0,AssocId+1, 1,msgID, "GroupMsgPull");
        BaseData sendData = new BaseData(pullMsgList);
        if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
            sendData = new BaseData(4, pullMsgList);
        }

        if (ConstantValue.INSTANCE.isWebsocketConnected()) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData);
        } else if (ConstantValue.INSTANCE.isToxConnected()) {
            BaseData baseData = sendData;
            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

            if (ConstantValue.INSTANCE.isAntox()) {
                //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
            } else {
                ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
            }


        }
    }
    private void initPictureSelector() {
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
                .setPictureLongClick((GroupChatActivity)getActivity());
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initPictureSelector();
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
                if(data.hasExtra("path"))
                {
                    String filePath = data.getStringExtra("path");
                    if(filePath != null)
                    {
                        File file = new File(filePath);
                        String md5Data = "";
                        if (file.exists()) {
                            sendFileMessage(filePath);
                        }
                    }
                }
                else{
                    JPullFileListRsp.ParamsBean.PayloadBean fileData = data.getParcelableExtra("fileData");
                    sendFileFileForward(fileData);
                }
            } else if (requestCode == CHOOSE_PIC) {
                String filePath = data.getStringExtra("path");
                Boolean isCheck = data.getBooleanExtra("isCheck", false);
                sendImageMessage(filePath, !isCheck);
            } else if (requestCode == REQUEST_CODE_ENTER_GROUP) {

            } else if (requestCode == SELECT_AT) {
                if(data.hasExtra("choose"))
                {
                    inputAtUsername("All",true);
                }else{
                    UserEntity user = data.getParcelableExtra("user");
                    if(user !=null)
                    {
                        inputAtUsername(user.getUserId(),true);
                    }
                }
            }
        }
        if (requestCode == SELECT_AT)
        {
            showKeyboard();
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
        super.onDestroy();
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
            if(draftEntity.getContent().contains("@"))
            {
                isContainAt = true;
            }
            inputMenu.setEdittext(draftEntity.getContent(),true);
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
//        message.setSortIndex(System.currentTimeMillis() / 1000);
//        message.setUnReadCount(0);
//        String baseDataJson = new Gson().toJson(message);
//        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
    }

    public void setFrom(String from)
    {
        fromActivity = from;
    }
    public void onBackPressed() {
        hideKeyboard();
        saveDraft();
        if(fromActivity.equals("CreateGroupActivity"))
        {
            EventBus.getDefault().post(new FromChat());
        }
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
                    //startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    Toast.makeText(getActivity(), R.string.wait, Toast.LENGTH_SHORT).show();
                    break;
                case ITEM_FILE:
                    //startActivityForResult(new Intent(getActivity(), FileChooseActivity.class).putExtra("fileType", 2), REQUEST_CODE_FILE);
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
    public void inputReplyMsg(String msgId, String content) {
        int inserResult = inputMenu.insertReplyText(content,msgId);
    }
    /**
     * input @
     *
     * @param userId
     */
    public void inputAtUsername(String userId, boolean autoAddAtSymbol) {
        String userSelftId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
        if(userSelftId.equals(userId))
        {
            return;
        }
        if(userId.equals("All"))
        {
            int inserResult = inputMenu.insertATText("@All" + " ",userId);
            if(inserResult == 3)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.The_maximum_number_is) +" "+ConstantValue.INSTANCE.getAtMaxNum() +" "+getString(R.string.in_the_group_at_once), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            UserEntity userEntity = null;
            List<UserEntity> userList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(userId)).list();
            if (userList.size() != 0)
            {
                userEntity = userList.get(0);
            }
            if(userEntity != null)
            {
                String usernameSouce = new  String(RxEncodeTool.base64Decode(userEntity.getNickName()));
                String remarks = userEntity.getRemarks();
                if(remarks != null && !remarks.equals(""))
                {
                    usernameSouce = new  String(RxEncodeTool.base64Decode(remarks));
                }
                if (autoAddAtSymbol)
                {
                    int inserResult = inputMenu.insertATText("@" + usernameSouce + " ",userId);
                    if(inserResult == 2)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.The_maximum_number_is) +" "+ConstantValue.INSTANCE.getAtMaxNum()+" "+getString(R.string.in_the_group_at_once), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
                else
                {
                    inputMenu.insertATText(usernameSouce + " ",userId);
                }

            }
        }
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }


    /**
     *   这里单独提出来，撤回消息显示在对应消息处
     * @param from 谁操作的
     * @param tip 消息撤回系统提示内容
     */
    public void insertMsgTipMessage(String from,String tip,long msgTime) {
        EMMessage message = EMMessage.createLocationSendMessage(0,0,tip, toChatUserId);
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        int uuid = (int) (System.currentTimeMillis() / 1000);
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(Message.SpecialId.GroupSpecialIdABDFEGEKLOIHHYGJHKIUFEGHEL.toString()+"_"+uuid);
        message.setFrom(from);
        if(msgTime != 0)
        {
            message.setMsgTime(msgTime);
        }
        message.setTo(toChatUserId);
        KLog.i("插入提示消息");
        sendMessageTo(message);
    }
    /**
     *
     * @param from 谁操作的
     * @param tip 系统提示内容
     */
    public void insertTipMessage(String from,String tip,String color) {
        int flag0 = 0;
        int flag1 = 0;
        if(color != null && color.equals("1"))
        {
            flag0 = 1;
            flag1 = 1;
        }
        EMMessage message = EMMessage.createLocationSendMessage(flag0,flag1,tip, toChatUserId);
        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
        int uuid = (int) (System.currentTimeMillis() / 1000);
        message.setDirection(EMMessage.Direct.RECEIVE);
        message.setMsgId(Message.SpecialId.GroupSpecialIdABDFEGEKLOIHHYGJHKIUFEGHEL.toString()+"_"+uuid);
        message.setFrom(from);
        message.setTo(toChatUserId);
        KLog.i("插入提示消息");
        sendMessageTo(message);
    }

    //send message
    public void sendTextMessage(String content,String point,String AssocId,String AssocContent,String userName) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        String contentTemp = content;
        if(!AssocId.equals(""))
        {
            //contentTemp = AssocContent +"\n……………………………………\n" +contentTemp;
        }
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(contentTemp, toChatUserId);
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            //String userIndex =  SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserIndex(),"");
            String msgId = UUID.randomUUID().toString().replace("-", "").toLowerCase();

            if (AppConfig.instance.getMessageReceiver() != null) {
                msgId = AppConfig.instance.getMessageReceiver().getGroupchatCallBack().sendGroupMsg(userId, UserDataManger.currentGroupData.getGId() + "", point, content, UserDataManger.currentGroupData.getUserKey(),AssocId);

                if(msgId.equals("0"))
                {
                    return;
                }
                //String name = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUsername(), "");
                message.setAttribute("username",userName);
                message.setAttribute("AssocContent",AssocContent);
                message.setFrom(userId);
                message.setTo(UserDataManger.currentGroupData.getGId() + "");
                message.setDelivered(true);
                message.setAcked(false);
                message.setUnread(true);
                message.setMsgId(msgId);
                currentSendMsg = message;
                ConstantValue.INSTANCE.getSendFileMsgMap().put(msgId, message);
                Gson gson = new Gson();
                Message Message = new Message();

                Message.setMsg(contentTemp);
                Message.setFrom(userId);
                Message.setTo(toChatUserId);
                Message.setStatus(0);
                Message.setTimeStamp(System.currentTimeMillis()/1000);
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.GroupChat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                sendMessageData = Message;
                sendMessageTo(message);
            }

        }
    }

    public void deleteMessage() {
        if (conversation != null) {
            conversation.removeMessage(currentSendMsg.getMsgId());
            KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_upateMessage");
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }
            int msgIdIndex = Integer.valueOf(currentSendMsg.getMsgId());
            ImagesObservable.getInstance().removeLocalMedia(msgIdIndex,"chat");
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
                Message.setTo(toChatUserId);
                Message.setTimeStamp(eMMessage.getMsgTime() / 1000);
                //Message.setTimeStamp(System.currentTimeMillis() / 1000);

               /* String cachStr = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId,"");
                Message MessageLocal = gson.fromJson(cachStr, Message.class);
                int unReadCount = 0;
                if(MessageLocal != null && Integer.valueOf(MessageLocal.getUnReadCount()) != null ){
                    unReadCount = MessageLocal.getUnReadCount();
                }
                Message.setUnReadCount(unReadCount);*/
                Message.setUnReadCount(0);
                Message.setChatType(ChatType.GroupChat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            } else {
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");
            }
        }

    }
    public void upateAssocIdMessage(Message messageData,int SrcMsgId) {
        if(currentPage == 1)
        {
            easeChatMessageList.scrollLast(1);
        }else{
            easeChatMessageList.scrollLast(0);
        }
        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(SrcMsgId+ "");
        KLog.i("upateMessage:" + "forward_msg" + (forward_msg != null));
        LogUtil.addLog("upateMessage:", "forward_msg" + (forward_msg != null));
        if (forward_msg != null) {
            String msgSouce = "";
            if (messageData.getMsg() != null && !messageData.getMsg().equals("")) {
                try {
                    String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    byte[] base64Scoure = RxEncodeTool.base64Decode(messageData.getMsg());
                    msgSouce = new String(AESCipher.aesDecryptBytes(base64Scoure, aesKey.getBytes()));
                } catch (Exception e) {

                }
            }
            if (msgSouce != null && !msgSouce.equals("")) {
                messageData.setMsg(msgSouce);
            }
            //final EMTextMessageBody body = (EMTextMessageBody) forward_msg.getBody();
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            String from = messageData.getFrom();
            if(!from.equals( ""))
            {
                List<UserEntity> userList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(from)).list();
                if (userList.size() > 0) {
                    UserEntity user = userList.get(0);
                    String username = new String(RxEncodeTool.base64Decode(user.getNickName()));
                    forward_msg.setAttribute("username",username);
                }
            }else{
                String name = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUsername(), "");
                forward_msg.setAttribute("username",name);
            }
            switch (messageData.getMsgType()) {
                case 0:
                    forward_msg.setAttribute("AssocContent",messageData.getMsg());
                    break;
                case 5:
                    forward_msg.setAttribute("AssocContent",messageData.getFileName());
                    break;
            }
            conversation.updateMessage(forward_msg);
            if (isMessageListInited) {
                easeChatMessageList.refresh();
            }

        }

    }
    public void upateMessage(JGroupSendMsgRsp jSendMsgRsp) {
        if (jSendMsgRsp.getParams().getRetCode() == 0) {

        }

        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(jSendMsgRsp.getMsgid() + "");
        KLog.i("upateMessage:" + "forward_msg" + (forward_msg != null));
        LogUtil.addLog("upateMessage:", "forward_msg" + (forward_msg != null));
        switch (jSendMsgRsp.getParams().getRetCode()) {
            case 0:
                String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                Gson gson = new Gson();
                sendMessageData.setTimeStamp(jSendMsgRsp.getTimestamp());
                String baseDataJson = gson.toJson(sendMessageData);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                if (conversation != null) {
                    if (forward_msg != null) {
                        conversation.removeMessage(jSendMsgRsp.getMsgid() + "");
                        forward_msg.setMsgId(jSendMsgRsp.getParams().getMsgId() + "");
                        forward_msg.setAcked(true);
                        //forward_msg.setMsgTime(jSendMsgRsp.getTimestamp() *1000);
                        forward_msg.setMsgTime(jSendMsgRsp.getParams().getMsgId());
                        conversation.insertMessage(forward_msg);
                        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_upateMessage");
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
                //friendStatus = 1;
                /*String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + jSendMsgRsp.getParams().getGId(), "");*/
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

    }
    public void upateForwardMessage(JFileForwardRsp jSendMsgRsp) {
        if (jSendMsgRsp.getParams().getRetCode() == 0) {

        }

        EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(jSendMsgRsp.getParams().getMsgId() + "");
        KLog.i("upateMessage:" + "forward_msg" + (forward_msg != null));
        LogUtil.addLog("upateMessage:", "forward_msg" + (forward_msg != null));
        switch (jSendMsgRsp.getParams().getRetCode()) {
            case 0:
                String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                Gson gson = new Gson();
                sendMessageData.setTimeStamp(jSendMsgRsp.getTimestamp());
                String baseDataJson = gson.toJson(sendMessageData);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                if (conversation != null) {
                    if (forward_msg != null) {
                        conversation.removeMessage(jSendMsgRsp.getParams().getMsgId() + "");
                        forward_msg.setMsgId(jSendMsgRsp.getParams().getNewId() + "");
                        forward_msg.setMsgTime(jSendMsgRsp.getParams().getNewId());
                        forward_msg.setAcked(true);
                        conversation.insertMessage(forward_msg);
                        KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_upateMessage");
                        if (isMessageListInited) {
                            easeChatMessageList.refresh();
                        }
                    }

                }
                break;
            case 1:
                if (conversation != null) {
                    conversation.removeMessage(jSendMsgRsp.getParams().getMsgId() + "");
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.uid_error, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                if (conversation != null) {
                    conversation.removeMessage(jSendMsgRsp.getParams().getMsgId() + "");
                    if (isMessageListInited) {
                        easeChatMessageList.refresh();
                    }
                }
                //friendStatus = 1;
               /* String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");*/
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.DestinationUnreachable, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
        for (String value : forwordFileIdMap.values()) {
            if(value.indexOf(jSendMsgRsp.getParams().getMsgId()+"_") > -1)
            {
                forward_msg = EMClient.getInstance().chatManager().getMessage(value);
                KLog.i("upateMessage:" + "forward_msg" + (forward_msg != null));
                LogUtil.addLog("upateMessage:", "forward_msg" + (forward_msg != null));
                switch (jSendMsgRsp.getParams().getRetCode()) {
                    case 0:
                        if (conversation != null) {
                            if (forward_msg != null) {
                                conversation.removeMessage(value+ "");
                                forward_msg.setMsgId(value + "");
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
                            conversation.removeMessage(value + "");
                            if (isMessageListInited) {
                                easeChatMessageList.refresh();
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.uid_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 2:
                        if (conversation != null) {
                            conversation.removeMessage(value + "");
                            if (isMessageListInited) {
                                easeChatMessageList.refresh();
                            }
                        }
                        //friendStatus = 1;
                       /* String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, "");*/
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.DestinationUnreachable, Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                    default:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
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
//                if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
//                {
//                    leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
//                }
//                String fileName = leftName + "_" + ((int) (System.currentTimeMillis() / 1000)) + typeName;
                String fileName = Base58.getBase58TwoName(leftName, "_" + ((int) (System.currentTimeMillis() / 1000)), typeName);
                EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUserId);
                String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                message.setFrom(userId);
                message.setTo(UserDataManger.currentGroupData.getGId() + "");
                message.setDelivered(true);
                message.setAcked(false);
                message.setUnread(true);
                message.setMsgTime(System.currentTimeMillis());
                if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    message.setMsgId(uuid);
                    currentSendMsg = message;
                    //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                    /*sendMsgLocalMap.put(uuid, false);
                    sendFilePathMap.put(uuid, filePath);
                    deleteFileMap.put(uuid, false);
                    sendFileFriendKeyMap.put(uuid, UserDataManger.currentGroupData.getUserKey());*/


                    //数据库记录
                    /*MessageEntity messageEntity  = new MessageEntity();
                    messageEntity.setUserId(userId);
                    messageEntity.setFriendId(UserDataManger.currentGroupData.getGId()+"");
                    messageEntity.setSendTime(System.currentTimeMillis() / 1000 +"");
                    messageEntity.setType("2");//这里要改
                    messageEntity.setMsgId(uuid);
                    messageEntity.setComplete(false);
                    messageEntity.setFilePath(filePath);
                    messageEntity.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                    messageEntity.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                    messageEntity.setVoiceTimeLen(length);
                    KLog.i("消息数据增加语音文件：userId："+userId +" friendId:"+UserDataManger.currentGroupData.getGId()+"");
                    AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);
*/

                   /* String fileKey = RxEncryptTool.generateAESKey();
                    byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                    byte[] friend = RxEncodeTool.base64Decode(UserDataManger.currentGroupData.getUserKey());
                    byte[] SrcKey = new byte[256];
                    byte[] DstKey = new byte[256];
                    try {

                        if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                            DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
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
                    SendFileInfo.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                    SendFileInfo.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                    SendFileInfo.setVoiceTimeLen(length);
                    SendFileInfo.setType("2");
                    SendFileInfo.setSendTime(System.currentTimeMillis() / 1000 + "");
                    SendFileInfo.setPorperty("1");
                    AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);

                    /*String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                } else {
                    String strBase58 = Base58.encode(fileName.getBytes());
                    String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                    String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    int code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, fileKey.substring(0, 16));
                    if (code == 1) {
                        int uuid = (int) (System.currentTimeMillis() / 1000);
                        message.setMsgId(uuid + "");
                        currentSendMsg = message;
                        ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                        sendMsgLocalMap.put(uuid + "", false);
                        sendFilePathMap.put(uuid + "", base58files_dir);
                        deleteFileMap.put(uuid + "", false);
                        sendFileFriendKeyMap.put(uuid + "", UserDataManger.currentGroupData.getUserKey());
                        ToxFileData toxFileData = new ToxFileData();
                        toxFileData.setFromId(userId);
                        toxFileData.setToId(UserDataManger.currentGroupData.getGId() + "");
                        File fileMi = new File(base58files_dir);
                        long fileSize = fileMi.length();
                        String fileMD5 = FileUtil.getFileMD5(fileMi);
                        toxFileData.setFileName(strBase58);
                        toxFileData.setFilePath(base58files_dir);
                        toxFileData.setFileMD5(fileMD5);
                        toxFileData.setFileSize((int) fileSize);
                        toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_AUDIO);
                        toxFileData.setFileId(uuid);
                        toxFileData.setPorperty("1");
                       /* String FriendPublicKey = UserDataManger.currentGroupData.getUserKey();
                        byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                        byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                        byte[] SrcKey = new byte[256];
                        byte[] DstKey = new byte[256];
                        try {

                            if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                            }
                        } catch (Exception e) {

                        }*/
                        toxFileData.setSrcKey(UserDataManger.currentGroupData.getUserKey());
                        toxFileData.setDstKey(UserDataManger.currentGroupData.getUserKey());
                        String fileNumber = "";
                        if (ConstantValue.INSTANCE.isAntox()) {
                            /*FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);*/
                        } else {
                            String groupIdPre = UserDataManger.currentGroupData.getGId();
                            groupIdPre = groupIdPre.substring(0, groupIdPre.indexOf("_"));
                            fileNumber = ToxCoreJni.getInstance().senToxFileInGoupChat(groupIdPre, base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64), uuid + "") + "";
                        }
                        ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().put(fileNumber, toxFileData);
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
                Message.setChatType(ChatType.GroupChat);
                String baseDataJson = gson.toJson(Message);
                SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                sendMessageData = Message;
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
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    File file = new File(imagePath);
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
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        String widthAndHeight = "," + bitmap.getWidth() + "*" + bitmap.getHeight();
                        KLog.i("图片的宽高为：" + widthAndHeight);
                        bitmap.recycle();
                        String imgeSouceName = imagePath.substring(imagePath.lastIndexOf("/") + 1,imagePath.lastIndexOf("."));
                        String typeName = imagePath.substring(imagePath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0,imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName,"_");
                            if(StringUitl.isNumeric(leftName))
                            {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        }else{
                            leftName = imgeSouceName;
                        }
//                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
//                        {
//                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
//                        }
//                        String fileName = leftName+ "_" +((int) (System.currentTimeMillis() / 1000)) + typeName;
                        String fileName = Base58.getBase58TwoName(leftName, "_" + ((int) (System.currentTimeMillis() / 1000)), typeName);
                        String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + fileName;
                        int codeSave = FileUtil.copySdcardPicAndCompress(imagePath, files_dir, isCompress);
                        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            if(codeSave == 1)
                            {
                                DeleteUtils.deleteFile(imagePath);
                            }
                        }*/
                        EMMessage message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setAttribute("wh", widthAndHeight.replace(",", ""));
                        message.setTo(UserDataManger.currentGroupData.getGId() + "");
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        message.setMsgTime(System.currentTimeMillis());
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = (int) (System.currentTimeMillis() / 1000) +"";
                            message.setMsgId(uuid);
                            currentSendMsg = message;
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                            /*sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, files_dir);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.currentGroupData.getUserKey());*/

                            if (codeSave == 1) {
                                SendFileInfo SendFileInfo = new SendFileInfo();
                                SendFileInfo.setUserId(userId);
                                SendFileInfo.setFriendId(toChatUserId);
                                SendFileInfo.setFiles_dir(files_dir);
                                SendFileInfo.setMsgId(uuid);
                                SendFileInfo.setWidthAndHeight(widthAndHeight);
                                SendFileInfo.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                                SendFileInfo.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                                SendFileInfo.setVoiceTimeLen(0);
                                SendFileInfo.setType("1");
                                SendFileInfo.setSendTime(System.currentTimeMillis() / 1000 + "");
                                SendFileInfo.setPorperty("1");
                                AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                                //AppConfig.instance.getPNRouterServiceMessageSender().sendImageMessage(userId,toChatUserId,files_dir,uuid,UserDataManger.currentGroupData.getUserKey(), UserDataManger.currentGroupData.getUserKey());
                               /* String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                                EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                            } else {
                                Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {

                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                            int code = FileUtil.copySdcardToxPicAndEncrypt(imagePath, base58files_dir, fileKey.substring(0, 16), isCompress);
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.currentGroupData.getUserKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.currentGroupData.getGId() + "");
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setWidthAndHeight(widthAndHeight.substring(1, widthAndHeight.length()));
                                toxFileData.setPorperty("1");
                                /*String FriendPublicKey = UserDataManger.currentGroupData.getUserKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }*/
                                toxFileData.setSrcKey(UserDataManger.currentGroupData.getUserKey());
                                toxFileData.setDstKey(UserDataManger.currentGroupData.getUserKey());

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                   /* FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);*/
                                } else {
                                    String groupIdPre = UserDataManger.currentGroupData.getGId();
                                    groupIdPre = groupIdPre.substring(0, groupIdPre.indexOf("_"));
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInGoupChat(groupIdPre, base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64), uuid + "") + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().put(fileNumber, toxFileData);
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
                        Message.setChatType(ChatType.GroupChat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageData = Message;
                        sendMessageTo(message);


                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setCompressed(false);
                        localMedia.setDuration(0);
                        localMedia.setHeight(100);
                        localMedia.setWidth(100);
                        localMedia.setChecked(false);
                        localMedia.setCut(false);
                        localMedia.setMimeType(0);
                        localMedia.setNum(0);
                        localMedia.setPath(files_dir);
                        localMedia.setPictureType("image/jpeg");
                        localMedia.setPosition((int)Message.getTimeStamp());
                        localMedia.setSortIndex((int)Message.getTimeStamp());
                        previewImages.add(localMedia);
                        ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
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
                        EMMessage message = EMMessage.createImageSendMessage(imagePath, true, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.currentGroupData.getGId() + "");
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        message.setMsgTime(System.currentTimeMillis());
                        currentSendMsg = message;

                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                            sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, imagePath);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.currentGroupData.getUserKey());

                            String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.currentGroupData.getUserKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
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
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin", ""));
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
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.currentGroupData.getUserKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.currentGroupData.getGId() + "");
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setPorperty("1");
                                String FriendPublicKey = UserDataManger.currentGroupData.getUserKey();
                                /*byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }*/
                                toxFileData.setSrcKey(UserDataManger.currentGroupData.getUserKey());
                                toxFileData.setDstKey(UserDataManger.currentGroupData.getUserKey());

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                   /* FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);*/
                                } else {
                                    String groupIdPre = UserDataManger.currentGroupData.getGId();
                                    groupIdPre = groupIdPre.substring(0, groupIdPre.indexOf("_"));
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInGoupChat(groupIdPre, base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64), uuid + "") + "";
                                }
                                ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().put(fileNumber, toxFileData);
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

    protected void sendVideoMessage(String videoPathStr, boolean isLocal) {
        if (friendStatus != 0) {
            Toast.makeText(getActivity(), R.string.notFreinds, Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {

                try {
                    String videoPath = videoPathStr;
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
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
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
                            String fileName = Base58.getBase58TwoName(leftName, "_" + ((int) (System.currentTimeMillis() / 1000)), typeName);
                            String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + fileName;
                            int result = FileUtil.copyAppFileToSdcard(videoPath, files_dir);
                           /* if(result == 1 )
                            {
                                DeleteUtils.deleteFile(videoPath);
                            }*/
                            videoPath = files_dir;
                        }
                        String imgeSouceName = videoPath.substring(videoPath.lastIndexOf("/") + 1,videoPath.lastIndexOf("."));
                        String typeName = videoPath.substring(videoPath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0,imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName,"_");
                            if(StringUitl.isNumeric(leftName))
                            {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        }else{
                            leftName = imgeSouceName;
                        }
//                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
//                        {
//                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
//                        }
//                        String videoFileName = leftName+ "_" +((int) (System.currentTimeMillis() / 1000)) + typeName;
                        String videoFileName = Base58.getBase58TwoName(leftName, "_" + ((int) (System.currentTimeMillis() / 1000)), typeName);

                        //String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf("."));
                        String thumbPath = PathUtils.getInstance().getImagePath() + "/" + leftName + ".png";
                        Bitmap bitmap = EaseImageUtils.getVideoPhoto(videoPath);
                        int videoLength = EaseImageUtils.getVideoDuration(videoPath);
                        FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.currentGroupData.getGId() + "");
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        message.setMsgTime(System.currentTimeMillis());
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                            message.setMsgId(uuid);
                            currentSendMsg = message;
                            //ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid, message);
                          /*  sendMsgLocalMap.put(uuid, false);
                            sendFilePathMap.put(uuid, videoPath);
                            deleteFileMap.put(uuid, false);
                            sendFileFriendKeyMap.put(uuid, UserDataManger.currentGroupData.getUserKey());*/


                            //数据库记录
                            /*MessageEntity messageEntity  = new MessageEntity();
                            messageEntity.setUserId(userId);
                            messageEntity.setFriendId(UserDataManger.currentGroupData.getGId()+"");
                            messageEntity.setSendTime(System.currentTimeMillis() / 1000 +"");
                            messageEntity.setType("3");//这里要改
                            messageEntity.setMsgId(uuid);
                            messageEntity.setComplete(false);
                            messageEntity.setFilePath(videoPath);
                            messageEntity.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                            messageEntity.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                            KLog.i("消息数据增加视频文件：userId："+userId +" friendId:"+UserDataManger.currentGroupData.getGId()+"");
                            AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);*/


                         /*   String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.currentGroupData.getUserKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
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
                            SendFileInfo.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                            SendFileInfo.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                            SendFileInfo.setVoiceTimeLen(0);
                            SendFileInfo.setType("3");
                            SendFileInfo.setSendTime(System.currentTimeMillis() / 1000 + "");
                            SendFileInfo.setPorperty("1");
                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                            //AppConfig.instance.getPNRouterServiceMessageSender().sendVideoMessage(userId,toChatUserId,videoPath,uuid,UserDataManger.currentGroupData.getUserKey(), UserDataManger.currentGroupData.getUserKey());

                            /*String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/

                        } else {
                            String strBase58 = Base58.encode(videoFileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                            int code = FileUtil.copySdcardToxFileAndEncrypt(videoPath, base58files_dir, fileKey.substring(0, 16));
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.currentGroupData.getUserKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.currentGroupData.getGId() + "");
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_MEDIA);
                                toxFileData.setFileId(uuid);
                                toxFileData.setPorperty("1");
                                String FriendPublicKey = UserDataManger.currentGroupData.getUserKey();
                                /*byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }*/
                                toxFileData.setSrcKey(UserDataManger.currentGroupData.getUserKey());
                                toxFileData.setDstKey(UserDataManger.currentGroupData.getUserKey());

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                   /* FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);*/
                                } else {
                                    String groupIdPre = UserDataManger.currentGroupData.getGId();
                                    groupIdPre = groupIdPre.substring(0, groupIdPre.indexOf("_"));
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInGoupChat(groupIdPre, base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64), uuid + "") + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().put(fileNumber, toxFileData);
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
                        Message.setChatType(ChatType.GroupChat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageData = Message;
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
                        String imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1,filePath.lastIndexOf("."));
                        String typeName = filePath.substring(filePath.lastIndexOf("."));
                        String leftName = "";
                        if (imgeSouceName.contains("_")) {
                            leftName = imgeSouceName.substring(0,imgeSouceName.lastIndexOf("_"));
                            leftName = StringUitl.replaceALL(leftName,"_");
                            if(StringUitl.isNumeric(leftName))
                            {
                                leftName = imgeSouceName.substring(imgeSouceName.lastIndexOf("_") + 1, imgeSouceName.length());
                            }
                        }else{
                            leftName = imgeSouceName;
                        }
//                        if(leftName.length() > ConstantValue.INSTANCE.getFileNameMaxLen() -12)
//                        {
//                            leftName = leftName.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen() -12);
//                        }
//                        String fileName = leftName+ "_" +((int) (System.currentTimeMillis() / 1000)) + typeName;
                        String fileName = Base58.getBase58TwoName(leftName, "_" + ((int) (System.currentTimeMillis() / 1000)), typeName);
                        String files_dir = PathUtils.getInstance().getImagePath().toString() + "/" + fileName;
                        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUserId);
                        String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                        message.setFrom(userId);
                        message.setTo(UserDataManger.currentGroupData.getGId() + "");
                        message.setDelivered(true);
                        message.setAcked(false);
                        message.setUnread(true);
                        message.setMsgTime(System.currentTimeMillis());
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {
                            int result = FileUtil.copyAppFileToSdcard(filePath, files_dir);
                            if(result == 0)
                            {
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
                            sendFileFriendKeyMap.put(uuid, UserDataManger.currentGroupData.getUserKey());*/


                            //数据库记录
                           /* MessageEntity messageEntity  = new MessageEntity();
                            messageEntity.setUserId(userId);
                            messageEntity.setFriendId(UserDataManger.currentGroupData.getGId()+"");
                            messageEntity.setSendTime(System.currentTimeMillis() / 1000 +"");
                            messageEntity.setType("4");//这里要改
                            messageEntity.setMsgId(uuid);
                            messageEntity.setComplete(false);
                            messageEntity.setFilePath(filePath);
                            messageEntity.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                            messageEntity.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                            KLog.i("消息数据增加文件文件：userId："+userId +" friendId:"+UserDataManger.currentGroupData.getGId()+"");
                            AppConfig.instance.getMDaoMaster().newSession().getMessageEntityDao().insert(messageEntity);*/


                           /* String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(UserDataManger.currentGroupData.getUserKey());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
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
                            SendFileInfo.setFriendSignPublicKey(UserDataManger.currentGroupData.getUserKey());
                            SendFileInfo.setFriendMiPublicKey(UserDataManger.currentGroupData.getUserKey());
                            SendFileInfo.setVoiceTimeLen(0);
                            SendFileInfo.setType("4");
                            SendFileInfo.setSendTime(System.currentTimeMillis() / 1000 + "");
                            SendFileInfo.setPorperty("1");
                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo);
                            //AppConfig.instance.getPNRouterServiceMessageSender().sendFileMessage(userId,toChatUserId,files_dir,uuid,UserDataManger.currentGroupData.getUserKey(), UserDataManger.currentGroupData.getUserKey());

                           /* String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"));*/
                        } else {
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString() + "/" + strBase58;
                            String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                            int code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, fileKey.substring(0, 16));
                            if (code == 1) {
                                int uuid = (int) (System.currentTimeMillis() / 1000);
                                message.setMsgId(uuid + "");
                                currentSendMsg = message;
                                ConstantValue.INSTANCE.getSendFileMsgMap().put(uuid + "", message);
                                sendMsgLocalMap.put(uuid + "", false);
                                sendFilePathMap.put(uuid + "", base58files_dir);
                                deleteFileMap.put(uuid + "", false);
                                sendFileFriendKeyMap.put(uuid + "", UserDataManger.currentGroupData.getUserKey());
                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(userId);
                                toxFileData.setToId(UserDataManger.currentGroupData.getGId() + "");
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFilePath(base58files_dir);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int) fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_FILE);
                                toxFileData.setFileId(uuid);
                                toxFileData.setPorperty("1");
                              /*  String FriendPublicKey = UserDataManger.currentGroupData.getUserKey();
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, UserDataManger.currentGroupData.getUserKey()));
                                    } else {
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                    }
                                } catch (Exception e) {

                                }*/
                                toxFileData.setSrcKey(UserDataManger.currentGroupData.getUserKey());
                                toxFileData.setDstKey(UserDataManger.currentGroupData.getUserKey());

                                String fileNumber = "";
                                if (ConstantValue.INSTANCE.isAntox()) {
                                    /*FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey);*/
                                } else {
                                    String groupIdPre = UserDataManger.currentGroupData.getGId();
                                    groupIdPre = groupIdPre.substring(0, groupIdPre.indexOf("_"));
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInGoupChat(groupIdPre, base58files_dir, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64), uuid + "") + "";
                                }

                                ConstantValue.INSTANCE.getSendToxFileInGroupChapDataMap().put(fileNumber, toxFileData);
                            }

                        }
                        //FileUtil.copySdcardFile(filePath, files_dir);
                        Gson gson = new Gson();
                        Message Message = new Message();
                        Message.setMsgType(5);
                        Message.setFileName(fileName);
                        Message.setMsg("");
                        Message.setFrom(userId);
                        Message.setTo(toChatUserId);
                        Message.setTimeStamp(System.currentTimeMillis() / 1000);
                        Message.setUnReadCount(0);
                        Message.setChatType(ChatType.GroupChat);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                        sendMessageData = Message;
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
    protected void sendFileFileForward(JPullFileListRsp.ParamsBean.PayloadBean fileData) {
        new Thread(new Runnable() {
            public void run() {

                try {
                       /*   "png", "jpg", "jpeg","webp" -> action = 1
                        "amr" -> action = 2
                        "mp4" -> action = 4
                        else -> action = 5*/
                    String ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.xml";
                    EMMessage message = EMMessage.createFileSendMessage(ease_default_image, toChatUserId);
                    String fileMiName = fileData.getFileName();
                    String msgId = fileData.getMsgId()+"";
                    String fileOrginName = new String(Base58.decode(fileMiName));
                    String filePath = PathUtils.getInstance().getFilePath().toString() + "/" + fileOrginName;
                    String fileMiPath = PathUtils.getInstance().getTempPath().toString() + "/" + fileOrginName;
                    File file = new File(filePath);

                    switch (fileData.getFileType())
                    {
                        case 1:
                            if(file.exists())
                            {
                                //String fileMD5 = FileUtil.getFileMD5(file);
                                message = EMMessage.createImageSendMessage(filePath, true, toChatUserId);
                                if (fileData.getFileInfo() != null) {
                                    message.setAttribute("wh", fileData.getFileInfo());
                                }
                               /* if(fileMD5.equals(fileData.getFileMD5()))
                                {
                                    message = EMMessage.createImageSendMessage(filePath, true, toChatUserId);
                                    if (fileData.getFileInfo() != null) {
                                        message.setAttribute("wh", fileData.getFileInfo());
                                    }
                                }else {
                                    ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_bg.xml";
                                    message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                                    if (fileData.getFileInfo() != null) {
                                        message.setAttribute("wh", fileData.getFileInfo());
                                    }
                                }*/

                            }else{
                                ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.xml";
                                message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                                if (fileData.getFileInfo() != null) {
                                    message.setAttribute("wh", fileData.getFileInfo());
                                }
                            }

                            break;
                        case 2:
                            break;
                        case 4:
                            if(file.exists())
                            {
                                //String fileMD5 = FileUtil.getFileMD5(file);
                                String videoName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".") + 1);
                                String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                Bitmap bitmap = EaseImageUtils.getVideoPhoto(filePath);
                                FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                                message = EMMessage.createVideoSendMessage(filePath, thumbPath, fileData.getFileSize(), toChatUserId);
                              /*  if(fileMD5.equals(fileData.getFileMD5()))
                                {
                                    String videoName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".") + 1);
                                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                    Bitmap bitmap = EaseImageUtils.getVideoPhoto(filePath);
                                    FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                                    message = EMMessage.createVideoSendMessage(filePath, thumbPath, fileData.getFileSize(), toChatUserId);
                                }else{
                                    String videoName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".") + 1);
                                    String thumbPath = PathUtils.getInstance().getImagePath() + "/" + videoName + ".png";
                                    Bitmap bitmap = EaseImageUtils.getVideoPhoto(filePath);
                                    FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
                                    message = EMMessage.createVideoSendMessage(filePath, thumbPath, fileData.getFileSize(), toChatUserId);
                                }*/

                            }else{
                                String thumbPath = PathUtils.getInstance().getImagePath() + "/" + "image_defalut_fileForward_bg.xml";
                                String videoPath = PathUtils.getInstance().getVideoPath() + "/" + "ease_default_fileForward_vedio.mp4";
                                message = EMMessage.createVideoSendMessage(videoPath, thumbPath, fileData.getFileSize(), toChatUserId);
                            }

                            break;
                        case 5:
                            if(file.exists())
                            {
                                if(file.length() == 0 && fileData.getFileSize() > 0)
                                {
                                    FileUtil.getKongFile(fileOrginName);
                                    //FileUtil.drawableToFile(AppConfig.instance,R.mipmap.kong,fileOrginName,5);
                                    String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + fileOrginName;
                                    message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                                    message.setAttribute("fileForward","1");
                                    message.setAttribute("fileSize",fileData.getFileSize());
                                }else{
                                    message = EMMessage.createFileSendMessage(filePath, toChatUserId);
                                }
                                /*String fileMD5 = FileUtil.getFileMD5(file);
                                if(fileMD5.equals(fileData.getFileMD5()))
                                {
                                    message = EMMessage.createFileSendMessage(filePath, toChatUserId);
                                }else{
                                    FileUtil.drawableToFile(AppConfig.instance,R.mipmap.kong,fileOrginName,5);
                                String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + fileOrginName;
                                    message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                                }*/

                            }else{
                                File resultFile =FileUtil.getKongFile(fileOrginName);
//                                FileUtil.drawableToFile(AppConfig.instance,R.mipmap.kong,fileOrginName,5);
                                String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + fileOrginName;
                                message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                                message.setAttribute("fileForward","1");
                                message.setAttribute("fileSize",fileData.getFileSize());
                            }
                            break;
                    }
                    Gson gsonData = new Gson();
                    String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
                    message.setAttribute("fileData",gsonData.toJson(fileData));
                    message.setFrom(userId);
                    message.setTo(UserDataManger.currentGroupData.getGId());
                    message.setDelivered(true);
                    message.setAcked(false);
                    message.setUnread(true);
                    message.setMsgTime(System.currentTimeMillis());
                    EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(fileData.getMsgId()+"");
                    if(forward_msg == null)
                    {
                        message.setMsgId(fileData.getMsgId()+"");
                    }else{
                        int currentTime = ((int) (System.currentTimeMillis() / 1000));
                        message.setMsgId(fileData.getMsgId() +"_"+currentTime+"");
                        forwordFileIdMap.put(fileData.getMsgId() +"_"+currentTime+"",fileData.getMsgId() +"_"+currentTime+"");
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setCompressed(false);
                        localMedia.setDuration(0);
                        localMedia.setHeight(100);
                        localMedia.setWidth(100);
                        localMedia.setChecked(false);
                        localMedia.setCut(false);
                        localMedia.setMimeType(0);
                        localMedia.setNum(0);
                        localMedia.setPath(filePath);
                        localMedia.setPictureType("image/jpeg");
                        localMedia.setPosition((int)fileData.getTimestamp());
                        localMedia.setSortIndex(fileData.getMsgId()+currentTime);
                        previewImages.add(localMedia);
                        ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
                    }

                    String fileSouceKey = LibsodiumUtil.INSTANCE.DecryptShareKey(fileData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                    String FileKeyBase64 = RxEncodeTool.base64Encode2String(AESToolsCipher.aesEncryptBytes(fileSouceKey.getBytes(), aesKey.getBytes()));
                    String fileInfo  = fileData.getFileInfo();
                    if(fileInfo == null || fileInfo.equals(""))
                    {
                        fileInfo = "";
                    }
                    String strBase58 = Base58.encode(fileOrginName.getBytes());
                    FileForwardReq fileForwardReq = new FileForwardReq(fileData.getMsgId(),userId, UserDataManger.currentGroupData.getGId(),fileData.getFilePath(), strBase58,fileInfo, FileKeyBase64,"FileForward");
                    if (ConstantValue.INSTANCE.isWebsocketConnected()) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(4,fileForwardReq));
                    } else if (ConstantValue.INSTANCE.isToxConnected()) {
                        BaseData baseData = new BaseData(4,fileForwardReq);
                        String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                        if (ConstantValue.INSTANCE.isAntox()) {
                            //FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                        }else{
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                        }
                    }

                    Gson gson = new Gson();
                    Message Message = new Message();
                    Message.setMsgType(fileData.getFileType());
                    Message.setFileName(fileOrginName);
                    Message.setMsg("");
                    Message.setFrom(userId);
                    Message.setTo(toChatUserId);
                    Message.setTimeStamp(System.currentTimeMillis() / 1000);
                    Message.setUnReadCount(0);
                    Message.setChatType(ChatType.GroupChat);
                    String baseDataJson = gson.toJson(Message);
                    SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
                    Message.setFileName(fileMiName);
                    message.setAttribute("Message",gsonData.toJson(Message));
                    sendMessageData = Message;
                    sendMessageTo(message);


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
    public void showTips(JGroupMsgPushRsp jPushMsgRsp)
    {
        if(jPushMsgRsp.getParams().getPoint() == 1 || jPushMsgRsp.getParams().getPoint() == 2 )
        {
            //insertTipMessage(jPushMsgRsp.getParams().getFrom(),name +" "+ getString(R.string.remind_you_to_check_the_message),"1");
            new Thread(new Runnable(){
                public void run(){

                    try {
                        Thread.sleep(300);
                        if(tipsparentRoot !=null)
                        {
                            tipsparentRoot.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e)
                    {

                    }
                }
            }).start();
        }
    }
    /**
     * 接受文字和表情消息V3
     *
     * @param jPushMsgRsp
     */
    public void receiveTxtMessageV3(JGroupMsgPushRsp jPushMsgRsp) {
        String aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(UserDataManger.currentGroupData.getUserKey(),ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
        byte[] base64Scoure = RxEncodeTool.base64Decode(jPushMsgRsp.getParams().getMsg());
        String msgSouce = "";
        try {
            msgSouce = new String(AESCipher.aesDecryptBytes(base64Scoure, aesKey.getBytes()));
            if (msgSouce != null && !msgSouce.equals("")) {
                jPushMsgRsp.getParams().setMsg(msgSouce);
            }
            if(jPushMsgRsp.getParams().getAssocId() != 0 && currentPage == 1)
            {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadAssocIdMessages(jPushMsgRsp.getParams().getAssocId(),jPushMsgRsp.getParams().getMsgId());
                    }
                }, 10);
            }
            EMMessage message = EMMessage.createTxtSendMessage(jPushMsgRsp.getParams().getMsg(), toChatUserId);
            message.setDirection(EMMessage.Direct.RECEIVE);
            message.setMsgId(jPushMsgRsp.getParams().getMsgId() + "");
            message.setFrom(jPushMsgRsp.getParams().getFrom());
            message.setTo(jPushMsgRsp.getParams().getGId());
            message.setMsgTime(jPushMsgRsp.getParams().getMsgId());
            Gson gson = new Gson();
            Message Message = new Message();
            Message.setMsg(jPushMsgRsp.getParams().getMsg());
            Message.setMsgId(jPushMsgRsp.getParams().getMsgId());
            Message.setFrom(jPushMsgRsp.getParams().getFrom());
            Message.setTo(jPushMsgRsp.getParams().getGId());
            Message.setTimeStamp(jPushMsgRsp.getTimestamp());
            //Message.setTimeStamp(System.currentTimeMillis() / 1000);
            Message.setUnReadCount(0);
            Message.setChatType(ChatType.GroupChat);
            String baseDataJson = gson.toJson(Message);
            String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
            SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + toChatUserId, baseDataJson);
            sendMessageTo(message);           ;
            String name = new String(RxEncodeTool.base64Decode(jPushMsgRsp.getParams().getUserName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 接受文件消息
     *
     * @param url
     * @param msgId
     * @param fromId
     * @param toId
     */
    public void receiveFileMessage(String url, String msgId, String fromId, String toId, int FileType, String fileInfo,int timeStamp) {
        String files_dir = "";
        EMMessage message = null;
        switch (FileType) {
            case 1:
                files_dir = PathUtils.getInstance().getFilePath() + "/" + url;
                message = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                message.setAttribute("wh", fileInfo);

                LocalMedia localMedia = new LocalMedia();
                localMedia.setCompressed(false);
                localMedia.setDuration(0);
                localMedia.setHeight(100);
                localMedia.setWidth(100);
                localMedia.setChecked(false);
                localMedia.setCut(false);
                localMedia.setMimeType(0);
                localMedia.setNum(0);
                localMedia.setPath(files_dir);
                localMedia.setPictureType("image/jpeg");
                localMedia.setPosition(timeStamp);
                localMedia.setSortIndex(Integer.valueOf(msgId));
                previewImages.add(localMedia);
                ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
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
                FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
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
            message.setMsgTime(Long.valueOf(msgId));
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
            //KLog.i("insertGroupMessage:" + "EaseChatFragment"+"_sendMessageTo1");
            KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData4_" + conversation.getAllMessages().size());
            conversation.insertMessage(message);
            //refresh ui
            if (isMessageListInited) {
                //KLog.i("insertGroupMessage:" + "EaseChatFragment"+"_sendMessageTo2_"+conversation.getAllMessages().size());
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
                            KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData5_" + conversation.getAllMessages().size());
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
                //KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData4_" + conversation.getAllMessages().size());
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
                                KLog.i("insertGroupMessage:" + "EaseChatFragment" + "_refreshData5_" + conversation.getAllMessages().size());
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraFile = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/PicAndVideoTemp", System.currentTimeMillis() / 1000 + ".jpg");
        }
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
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.N) {
            videoFile = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/PicAndVideoTemp", System.currentTimeMillis() / 1000 + ".mp4");
        }
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
//        Intent intent;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            //intent.setType("image/*");
//            intent.setType("*/*");
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, REQUEST_CODE_LOCAL);
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
            if (groupEntity == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
            Intent intent = new Intent(getActivity(), GroupInfoActivity.class);
            intent.putExtra(EaseConstant.EXTRA_CHAT_GROUP, groupEntity);
            startActivityForResult(intent, REQUEST_CODE_ENTER_GROUP);
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
    public void showKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                if(inputMenu != null && inputMenu.chatPrimaryMenu != null)
                {
                    //inputManager.showSoftInput(inputMenu.chatPrimaryMenu.editText, 0);

                    inputMenu.chatPrimaryMenu.editText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        }
                    }, 100);
                }

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
                    sendTextMessage(content,"","","","");
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
                                    LocalMedia localMedia = new LocalMedia();
                                    localMedia.setCompressed(false);
                                    localMedia.setDuration(0);
                                    localMedia.setHeight(100);
                                    localMedia.setWidth(100);
                                    localMedia.setChecked(false);
                                    localMedia.setCut(false);
                                    localMedia.setMimeType(0);
                                    localMedia.setNum(0);
                                    localMedia.setPath(files_dir);
                                    localMedia.setPictureType("image/jpeg");
                                    localMedia.setPosition((int)message.getTimeStamp());
                                    localMedia.setSortIndex(message.getMsgId());
                                    previewImages.add(localMedia);
                                    ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
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
                                    FileUtil.saveBitmpToFileOnThread(bitmap, thumbPath);
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

                                if (message.getFrom() != null) {

                                    if (message.getFrom().equals(userId)) {
                                        messageData.setFrom(message.getFrom());
                                        messageData.setTo(toChatUserId);
                                        messageData.setDelivered(true);
                                        messageData.setAcked(true);
                                        messageData.setUnread(true);
                                   /* if (message.getSender() == 0)
                                    {
                                        messageData.setFrom(userId);
                                        messageData.setTo(toChatUserId);
                                        switch (message.getStatus())
                                        {
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
                                        }*/
                                        messageData.setDirection(EMMessage.Direct.SEND);
                                    } else {
                                        messageData.setFrom(message.getFrom());
                                        messageData.setTo(toChatUserId);
                                        messageData.setDirection(EMMessage.Direct.RECEIVE);
                                    }
                                } else {
                                    if (message.getFrom() != null && message.getFrom().equals(userId)) {
                                        messageData.setDirection(EMMessage.Direct.SEND);
                                    } else {
                                        messageData.setDirection(EMMessage.Direct.RECEIVE);
                                    }
                                }

                                //messageData.setMsgTime(message.getTimeStamp() * 1000);
                                messageData.setMsgTime(message.getMsgId());
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
