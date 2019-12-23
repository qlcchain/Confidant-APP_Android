package com.stratagile.pnrouter.utils;

import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.message.Message;
import com.smailnet.eamil.Utils.AESToolsCipher;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.FileUploadItem;
import com.stratagile.pnrouter.db.FileUploadItemDao;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.entity.BakFileReq;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.MyFile;
import com.stratagile.pnrouter.entity.SendFileData;
import com.stratagile.pnrouter.entity.SendToxUploadFileNotice;
import com.stratagile.pnrouter.entity.ToxFileData;
import com.stratagile.pnrouter.entity.UploadAvatarReq;
import com.stratagile.pnrouter.entity.events.FileMangerTransformEntity;
import com.stratagile.pnrouter.entity.events.FileMangerTransformMessage;
import com.stratagile.pnrouter.entity.events.FileMangerTransformReceiverMessage;
import com.stratagile.pnrouter.entity.events.FileStatus;
import com.stratagile.pnrouter.entity.file.UpLoadFile;
import com.stratagile.tox.toxcore.ToxCoreJni;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class FileMangerUtil {
    protected static final String TAG = "FileMangerUtil";
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
    protected static int friendStatus = 0;
    protected EaseChatMessageList easeChatMessageList;
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
    protected EMMessage contextMenuMessage;

    static final int ITEM_PICTURE= 1;
    static final int ITEM_TAKE_PICTURE = 2;
    static final int ITEM_SHORTVIDEO = 3;
    static final int ITEM_FILE = 4;
    static final int ITEM_LOCATION = 5;
    static final int ITEM_MEETING= 6;
    static final int ITEM_VIDEOCALL = 7;
    static final int ITEM_PRIVATEFILE = 8;




    protected int[] itemStrings = {  R.string.attach_picture,R.string.attach_take_pic, R.string.attach_Short_video, R.string.attach_file };
    protected int[] itemdrawables = {  R.drawable.ease_chat_image_selector,R.drawable.ease_chat_takepic_selector,
            R.drawable.ease_chat_shortvideo_selector, R.drawable.ease_chat_localdocument_selector};
    protected int[] itemIds = { ITEM_PICTURE,ITEM_TAKE_PICTURE,ITEM_SHORTVIDEO ,ITEM_FILE,ITEM_LOCATION,ITEM_MEETING,ITEM_VIDEOCALL,ITEM_PRIVATEFILE};
    private boolean isMessageListInited;
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

    private static String fromUserId = "";
    private UserEntity toChatUser;

    private static ConcurrentHashMap<String, Boolean> sendMsgLocalMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> sendFilePathMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ToxFileData> sendToxFileDataMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ToxFileData> sendMsgIdToxFileDataMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Boolean> deleteFileMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> receiveToxFileNameMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> receiveToxFileSizeMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> sendFileKeyByteMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, byte[]> sendFileFriendKeyByteMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, byte[]> sendFileMyKeyByteMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> sendFileNameMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> sendFileTotalSegment = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> sendFileSize = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> sendFileLastByteSizeMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, byte[]> sendFileLeftByteMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> sendMsgIdMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Message> receiveFileDataMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> sendFileWidthAndHeightMap =new  ConcurrentHashMap<>();
    private static long  faBegin;
    private static long faEnd;
    private static Integer fromPorperty;

    public static void  init()
    {
        fromPorperty = null;
        sendMsgLocalMap = new ConcurrentHashMap<>();
        sendFilePathMap = new ConcurrentHashMap<>();
        sendToxFileDataMap = new ConcurrentHashMap<>();
        sendMsgIdToxFileDataMap = new ConcurrentHashMap<>();
        deleteFileMap = new ConcurrentHashMap<>();
        receiveToxFileNameMap = new ConcurrentHashMap<>();
        receiveToxFileSizeMap = new ConcurrentHashMap<>();
        sendFileKeyByteMap = new ConcurrentHashMap<>();
        sendFileFriendKeyByteMap = new ConcurrentHashMap<>();
        sendFileMyKeyByteMap = new ConcurrentHashMap<>();
        sendFileNameMap = new ConcurrentHashMap<>();
        sendFileTotalSegment = new ConcurrentHashMap<>();
        sendFileSize = new ConcurrentHashMap<>();
        sendFileLastByteSizeMap = new ConcurrentHashMap<>();
        sendFileLeftByteMap = new ConcurrentHashMap<>();
        sendMsgIdMap = new ConcurrentHashMap<>();
        receiveFileDataMap = new ConcurrentHashMap<>();
        sendFileWidthAndHeightMap = new ConcurrentHashMap<>();
        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
        fromUserId = userId;
    }
    private CountDownTimerUtils countDownTimerUtilsOnVpnServer;

    public void setChatUserId(String id)
    {
        toChatUserId = id;
    }
    public static  void onFileMangerTransformEntity(FileMangerTransformEntity fileTransformEntity){

        if(fileTransformEntity.getMessage() == 0)
        {
            return;
        }
        switch (fileTransformEntity.getMessage())
        {
            case 1:
                new Thread(new Runnable(){
                    public void run(){
                        int fileId = (int)(System.currentTimeMillis()/1000);
                        try
                        {
                            String filePath = sendFilePathMap.get(fileTransformEntity.getToId());
                            //String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                            String fileName = sendFileNameMap.get(fileTransformEntity.getToId());
                            String fileKey = sendFileKeyByteMap.get(fileTransformEntity.getToId());
                            byte[] SrcKey = sendFileMyKeyByteMap.get(fileTransformEntity.getToId());
                            byte[] DstKey = sendFileFriendKeyByteMap.get(fileTransformEntity.getToId());
                            File file = new File(filePath);
                            if(file.exists())
                            {
                                long fileSize = file.length();
                                //String fileMD5 = FileUtil.getFileMD5(file);
                                byte[] fileBuffer= FileUtil.file2Byte(filePath);

                                byte[] fileBufferMi = fileBuffer;
                                try{
                                    long  miBegin = System.currentTimeMillis();
                                    if(!fileKey.equals("") && !fileKey.equals("zip"))//头像和附件不用加密
                                    {
                                        fileBufferMi = AESToolsCipher.aesEncryptBytes(fileBuffer,fileKey.getBytes("UTF-8"));
                                    }
                                    int totlSegment = (int)Math.ceil(fileBufferMi.length / ConstantValue.INSTANCE.getSendFileSizeMax());
                                    sendFileTotalSegment.put(filePath,totlSegment);
                                    long miend  = System.currentTimeMillis();
                                    KLog.i("jiamiTime:"+ (miend - miBegin)/1000);
                                    faBegin = System.currentTimeMillis();
                                    if(!deleteFileMap.get(fileTransformEntity.getToId()))
                                    {
                                        sendFileByteData(fileBufferMi,fileName,fromUserId,"",fileTransformEntity.getToId(),fileId,1,fileKey,SrcKey,DstKey);
                                        if(!fileKey.equals("") && !fileKey.equals("zip"))//头像不用加密
                                        {
                                            int segSeqTotal = sendFileTotalSegment.get(filePath);
                                            UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(fileTransformEntity.getToId());
                                            if(!localUpLoadFile.isStop().equals("1"))
                                            {
                                                UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, false, "0",0,segSeqTotal,10,false,"",0,0,fileTransformEntity.getToId(),false);
                                                MyFile myRouter = new MyFile();
                                                myRouter.setType(0);
                                                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                                myRouter.setUpLoadFile(uploadFile);
                                                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                                                EventBus.getDefault().post(new FileStatus(fileName+"__"+fileTransformEntity.getToId(),fileSize, false, false, false,0,segSeqTotal,10,false,0));
                                            }
                                        }
                                    }else{
                                        sendFileLeftByteMap.remove(fileTransformEntity.getToId());
                                        sendFileNameMap.remove(fileTransformEntity.getToId());
                                        sendFileLastByteSizeMap.remove(fileTransformEntity.getToId());
                                        sendFileKeyByteMap.remove(fileTransformEntity.getToId());
                                        sendFileMyKeyByteMap.remove(fileTransformEntity.getToId());
                                        sendFileFriendKeyByteMap.remove(fileTransformEntity.getToId());
                                        System.gc();
                                        KLog.i("websocket文件上传前取消！");
                                        String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                                        EventBus.getDefault().post(new FileMangerTransformEntity(fileTransformEntity.getToId(),4,"",wssUrl,"lws-pnr-bin"));
                                    }

                                }catch (Exception e)
                                {
                                    sendFileLeftByteMap.remove(fileTransformEntity.getToId());
                                    sendFileNameMap.remove(fileTransformEntity.getToId());
                                    sendFileLastByteSizeMap.remove(fileTransformEntity.getToId());
                                    sendFileKeyByteMap.remove(fileTransformEntity.getToId());
                                    sendFileMyKeyByteMap.remove(fileTransformEntity.getToId());
                                    sendFileFriendKeyByteMap.remove(fileTransformEntity.getToId());
                                    System.gc();
                                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                                    EventBus.getDefault().post(new FileMangerTransformEntity(fileTransformEntity.getToId(),4,"",wssUrl,"lws-pnr-bin"));
                                }
                            }
                        }catch (Exception e)
                        {
                            sendFileLeftByteMap.remove(fileTransformEntity.getToId());
                            sendFileNameMap.remove(fileTransformEntity.getToId());
                            sendFileLastByteSizeMap.remove(fileTransformEntity.getToId());
                            sendFileKeyByteMap.remove(fileTransformEntity.getToId());
                            sendFileMyKeyByteMap.remove(fileTransformEntity.getToId());
                            sendFileFriendKeyByteMap.remove(fileTransformEntity.getToId());
                            System.gc();
                        }
                    }
                }).start();

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
                break;
            default:
                break;
        }
    }
    public static void onFileMangerTransformReceiverMessage(FileMangerTransformReceiverMessage transformReceiverFileMessage){
        byte[] retMsg = transformReceiverFileMessage.getMessage();
        byte[] Action = new byte[4];
        byte[] FileId = new byte[4];
        byte[] LogId = new byte[4];
        byte[] SegSeq = new byte[4];
        byte[] CRC = new byte[2];
        byte[] Code = new byte[2];
        byte[] FromId = new byte[76];
        byte[] ToId = new byte[76];
        System.arraycopy(retMsg, 0, Action, 0, 4);
        System.arraycopy(retMsg, 4, FileId, 0, 4);
        System.arraycopy(retMsg, 8, LogId, 0, 4);
        System.arraycopy(retMsg, 12, SegSeq, 0, 4);
        System.arraycopy(retMsg, 16, CRC, 0, 2);
        System.arraycopy(retMsg, 18, Code, 0, 2);
        System.arraycopy(retMsg, 20, FromId, 0, 76);
        System.arraycopy(retMsg, 97, ToId, 0, 76);

        if(FromId[44]== 0 && FromId[45]== 0)
        {
            FromId = new byte[44];
            ToId = new byte[44];
            System.arraycopy(retMsg, 20, FromId, 0, 44);
            System.arraycopy(retMsg, 97, ToId, 0, 44);
        }

        int ActionResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(Action)) ;
        int FileIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(FileId));
        int LogIdIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(LogId));
        int SegSeqResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(SegSeq));
        short CRCResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(CRC));
        short CodeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(Code));
        String FromIdResult  = new String(FromId);
        String ToIdResult  = new String(ToId);
        String aa = "";
        KLog.i("CodeResult:"+ CodeResult);
        String msgId = sendMsgIdMap.get(FileIdResult+"");
        String fileKey = sendFileKeyByteMap.get(msgId+"");
        switch (CodeResult)
        {
            case 0:
                if(sendFileLastByteSizeMap.get(msgId+"") == null)
                {
                    return;
                }
                int lastSendSize = sendFileLastByteSizeMap.get(msgId+"");
                byte[] fileBuffer = sendFileLeftByteMap.get(msgId+"");
                int leftSize =fileBuffer.length - lastSendSize;

                String filePath = sendFilePathMap.get(msgId+"");
                long fileSize  = sendFileSize.get(msgId);
                if(sendFileTotalSegment.get(filePath) == null)
                {
                    return;
                }
                int fileTotalSegment = sendFileTotalSegment.get(filePath);
                if(leftSize >0)
                {
                    new Thread(new Runnable(){
                        public void run(){

                            try
                            {
                                byte[] fileLeftBuffer = new byte[leftSize];
                                System.arraycopy(fileBuffer, ConstantValue.INSTANCE.getSendFileSizeMax(), fileLeftBuffer, 0, leftSize);
                                String fileName = sendFileNameMap.get(msgId+"");

                                byte[] SrcKey = sendFileMyKeyByteMap.get(msgId);
                                byte[] DstKey = sendFileFriendKeyByteMap.get(msgId);
                                if(!deleteFileMap.get(msgId))
                                {
                                    sendFileByteData(fileLeftBuffer,fileName,FromIdResult+"","",msgId,FileIdResult,SegSeqResult +1,fileKey,SrcKey,DstKey);

                                    if(!fileKey.equals("") && !fileKey.equals("zip"))//头像不用加密
                                    {
                                        int sended = SegSeqResult - 1;
                                        if(sended < 0 )
                                            sended = 0;
                                        KLog.i("websocket文件上传进度："+sended +"_"+fileTotalSegment);
                                        UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(msgId);
                                        if(!localUpLoadFile.isStop().equals("1"))
                                        {
                                            UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, false, "0",sended, fileTotalSegment,10,false,"",0,0,msgId,false);
                                            MyFile myRouter = new MyFile();
                                            myRouter.setType(0);
                                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                            myRouter.setUpLoadFile(uploadFile);
                                            LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                                            EventBus.getDefault().post(new FileStatus(fileName+"__"+msgId,fileSize, false, false, false,sended, fileTotalSegment,10,false,0));
                                        }
                                    }
                                }else{
                                    sendFileLeftByteMap.remove(msgId);
                                    sendFileNameMap.remove(msgId);
                                    sendFileLastByteSizeMap.remove(msgId);
                                    sendFileKeyByteMap.remove(msgId);
                                    sendFileMyKeyByteMap.remove(msgId);
                                    sendFileFriendKeyByteMap.remove(msgId);
                                    System.gc();
                                    KLog.i("websocket文件上传中取消！");
                                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                                    EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
                                }
                            }catch (Exception e)
                            {
                                sendFileLeftByteMap.remove(msgId);
                                sendFileNameMap.remove(msgId);
                                sendFileLastByteSizeMap.remove(msgId);
                                sendFileKeyByteMap.remove(msgId);
                                sendFileMyKeyByteMap.remove(msgId);
                                sendFileFriendKeyByteMap.remove(msgId);
                                System.gc();
                            }
                        }
                    }).start();

                }else{

                    int segSeqTotal = sendFileTotalSegment.get(filePath);
                    String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
                    UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, true, "0",segSeqTotal,segSeqTotal,0,false,"",0,0,msgId,false);
                    MyFile myRouter = new MyFile();
                    myRouter.setType(0);
                    myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                    myRouter.setUpLoadFile(uploadFile);
                    LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                    EventBus.getDefault().post(new FileStatus(fileName+"##"+FileIdResult+"__"+msgId,fileSize, false, true, false,segSeqTotal,segSeqTotal,0,false,0));

                    //EventBus.getDefault().post(new FileStatus(filePath,fileSize,fileSize,0));
                    KLog.i("websocket文件上传成功！");
                    sendFileLeftByteMap.remove(msgId);
                    sendFileNameMap.remove(msgId);
                    sendFileLastByteSizeMap.remove(msgId);
                    sendFileKeyByteMap.remove(msgId);
                    sendFileMyKeyByteMap.remove(msgId);
                    sendFileFriendKeyByteMap.remove(msgId);
                    sendFilePathMap.remove(msgId);
                    fromPorperty = null;
                    System.gc();
                    faEnd = System.currentTimeMillis();
                    KLog.i("faTime:"+ (faEnd - faBegin)/1000);
                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
                    if(fileKey.equals(""))
                    {
                        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                        String fileBase58Name = Base58.encode(fileName.getBytes());
                        String fileMD5 = FileUtil.getFileMD5(new File(filePath));
                        UploadAvatarReq uploadAvatarReq = new UploadAvatarReq( userId, fileBase58Name,fileMD5,"UploadAvatar");
                        if(ConstantValue.INSTANCE.isWebsocketConnected())
                        {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(4,uploadAvatarReq));
                        }else if(ConstantValue.INSTANCE.isToxConnected())
                        {
                            BaseData baseData = new BaseData(4,uploadAvatarReq);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }
                        }
                    }
                    List<FileUploadItem> picItemList = AppConfig.instance.getMDaoMaster().newSession().getFileUploadItemDao().queryBuilder().where(FileUploadItemDao.Properties.FileId.eq(msgId)).list();
                    if(picItemList != null &&  picItemList.size() != 0)//加密相册上传
                    {
                        FileUploadItem fileUploadItem = picItemList.get(0);
                        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                        BakFileReq bakFileReq = new BakFileReq( fileUploadItem.getDepens(), userId,fileUploadItem.getType(),FileIdResult,fileUploadItem.getSize(),fileUploadItem.getMd5(),fileUploadItem.getFName(),fileUploadItem.getFKey(),fileUploadItem.getFInfo(),fileUploadItem.getPathId(),fileUploadItem.getPathName(),"BakFile");
                        if(ConstantValue.INSTANCE.isWebsocketConnected())
                        {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(6,bakFileReq));
                        }else if(ConstantValue.INSTANCE.isToxConnected())
                        {
                            BaseData baseData = new BaseData(6,bakFileReq);
                            String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                            if (ConstantValue.INSTANCE.isAntox()) {
                                //FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }
                        }
                    }

                }
                break;
            case 1:
                sendFileLeftByteMap.remove(msgId);
                sendFileNameMap.remove(msgId);
                sendFileLastByteSizeMap.remove(msgId);
                sendFileKeyByteMap.remove(msgId);
                sendFileMyKeyByteMap.remove(msgId);
                sendFileFriendKeyByteMap.remove(msgId);
                System.gc();
                break;
            case 2:
                sendFileLeftByteMap.remove(msgId);
                sendFileNameMap.remove(msgId);
                sendFileLastByteSizeMap.remove(msgId);
                sendFileKeyByteMap.remove(msgId);
                sendFileMyKeyByteMap.remove(msgId);
                sendFileFriendKeyByteMap.remove(msgId);
                System.gc();
                break;
            case 3:
                sendFileLeftByteMap.remove(msgId);
                sendFileNameMap.remove(msgId);
                sendFileLastByteSizeMap.remove(msgId);
                sendFileKeyByteMap.remove(msgId);
                sendFileMyKeyByteMap.remove(msgId);
                sendFileFriendKeyByteMap.remove(msgId);
                System.gc();
                break;
            case 4:
                sendFileLeftByteMap.remove(msgId);
                sendFileNameMap.remove(msgId);
                sendFileLastByteSizeMap.remove(msgId);
                sendFileKeyByteMap.remove(msgId);
                sendFileMyKeyByteMap.remove(msgId);
                sendFileFriendKeyByteMap.remove(msgId);
                System.gc();
                break;
            case 5:
                sendFileLeftByteMap.remove(msgId);
                sendFileNameMap.remove(msgId);
                sendFileLastByteSizeMap.remove(msgId);
                sendFileKeyByteMap.remove(msgId);
                sendFileMyKeyByteMap.remove(msgId);
                sendFileFriendKeyByteMap.remove(msgId);
                System.gc();
                break;
        }
    }
    private static void sendFileByteData(byte[] fileLeftBuffer,String fileName,String From,String To,String msgId,int fileId,int segSeq,String fileKey,byte[] SrcKey,byte[] DstKey)
    {
        try {
            KLog.i("发送中>>>刚调用"+"From:"+From+"  To:"+To);
            String MsgType = fileName.substring(fileName.lastIndexOf(".")+1);
            int action = 1;
            switch (MsgType)
            {
                case "png":
                case "jpg":
                case "jpeg":
                case "webp":
                    action = 1;
                    break;
                case "amr":
                    action = 2;
                    break;
                case "mp4":
                    action = 4;
                    break;
                default:
                    action = 5;
                    break;
            }
            if(fileKey.equals(""))
            {
                action = 6;
            }else if(fileKey.equals("zip"))
            {
                action = 7;
            }
            SendFileData sendFileData = new SendFileData();
            int segSize = fileLeftBuffer.length > ConstantValue.INSTANCE.getSendFileSizeMax() ? ConstantValue.INSTANCE.getSendFileSizeMax() : (int)fileLeftBuffer.length;
            sendFileData.setMagic(FormatTransfer.reverseInt(0x0dadc0de));
            sendFileData.setAction(FormatTransfer.reverseInt(action));
            sendFileData.setSegSize(FormatTransfer.reverseInt(segSize));
            int aa = FormatTransfer.reverseInt(9437440);
            sendFileData.setSegSeq(FormatTransfer.reverseInt(segSeq));
            int segMore = fileLeftBuffer.length>ConstantValue.INSTANCE.getSendFileSizeMax() ? 1: 0;
            int fileOffset = 0;
            fileOffset = (segSeq -1) * ConstantValue.INSTANCE.getSendFileSizeMax();
            sendFileData.setFileOffset(FormatTransfer.reverseInt(fileOffset));
            sendFileData.setFileId(FormatTransfer.reverseInt(fileId));
            sendFileData.setCRC(FormatTransfer.reverseShort((short)0));

            sendFileData.setSegMore((byte) segMore);
            sendFileData.setCotinue((byte) 0);
            //String strBase64 = RxEncodeTool.base64Encode2String(fileName.getBytes());
            String strBase58 = Base58.encode(fileName.getBytes());
            if (action == 1 && sendFileWidthAndHeightMap.get(msgId) != null || action == 4 && sendFileWidthAndHeightMap.get(msgId) != null) {
                String fileTempName = strBase58 + sendFileWidthAndHeightMap.get(msgId);
                sendFileData.setFileName(fileTempName.getBytes());
                KLog.i("发送文件的宽高为："  + msgId + "  " + sendFileWidthAndHeightMap.get(msgId));
                //KLog.i("发送的文件的名字为：" + String(sendFileData.fileName))
            } else {
                sendFileData.setFileName(strBase58.getBytes());
            }

            sendFileData.setFromId(From.getBytes());
            byte[] toRouter = new byte[77];
            Arrays.fill(toRouter,(byte) 0);
            sendFileData.setToId(toRouter);
            sendFileData.setSrcKey(SrcKey);
            sendFileData.setDstKey(DstKey);
            byte[] content = new byte[segSize];
            System.arraycopy(fileLeftBuffer, 0, content, 0, segSize);
            byte[] porperty = new byte[1];
            if(action == 7)
            {
                Arrays.fill(porperty,(byte) 2);
            }else{
                Arrays.fill(porperty,(byte) 0);
            }
            if(fromPorperty != null && fromPorperty != -1)
            {
                Arrays.fill(porperty,(byte) fromPorperty.byteValue());

            }
            sendFileData.setPorperty(porperty);
            byte[] ver = new byte[1];
            Arrays.fill(ver,(byte) 1);
            sendFileData.setVer(ver);
            sendFileData.setContent(content);
            byte[] sendData = sendFileData.toByteArray();
            //int newCRC = CRC16Util.getCRC(sendData,sendData.length);
            int newCRC = 1;
            sendFileData.setCRC(FormatTransfer.reverseShort((short)newCRC));
            sendData = sendFileData.toByteArray();
            sendFileLastByteSizeMap.put(msgId+"",segSize);
            sendFileLeftByteMap.put(msgId+"",fileLeftBuffer);
            sendMsgIdMap.put(fileId+"",msgId);
            /*sendFileKeyByteMap.put(msgId+"",fileKey);
            sendFileMyKeyByteMap.put(msgId+"",SrcKey);
            sendFileFriendKeyByteMap.put(msgId+"",DstKey);*/
            //KLog.i("发送中>>>内容"+"content:"+aabb);
            /*byte[] header = new byte[segSize];
            System.arraycopy(content, 0, header, 0, segSize);
            String headerStr = FileUtil.bytesToHex(header);*/
            KLog.i("发送中>>>"+"content:"+content.length+"strBase58:"+strBase58+"segMore:"+segMore+"  " +"segSize:"+ segSize  +"   " + "left:"+ (fileLeftBuffer.length -segSize) +"  segSeq:"+segSeq  +"  fileOffset:"+fileOffset +"  setSegSize:"+sendFileData.getSegSize()+" CRC:"+newCRC);
            EventBus.getDefault().post(new FileMangerTransformMessage(msgId,sendData));

        }catch (Exception e)
        {
            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
            EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
        }
    }
    protected boolean turnOnTyping() {
        return false;
    }


    public void setFriendStatus(int status)
    {
        friendStatus = status;
    }
    public static void  onToxFileSendFinished(int fileNumber,String key)
    {

        ToxFileData toxFileData = sendToxFileDataMap.get(fileNumber+"");
        if(toxFileData != null)
        {
            String filePath = toxFileData.getFilePath();
            String fileSouceName = toxFileData.getFileName();
            int fieType = toxFileData.getFileType().value();
            if(fieType == 6)
            {
                String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                String fileBase58Name = Base58.encode(fileSouceName.getBytes());
                String fileMD5 = FileUtil.getFileMD5(new File(filePath));
                UploadAvatarReq uploadAvatarReq = new UploadAvatarReq( userId, fileBase58Name,fileMD5,"UploadAvatar");
                BaseData baseData = new BaseData(4,uploadAvatarReq);
                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                if (ConstantValue.INSTANCE.isAntox()) {
                    //FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                }
            }else{
                UpLoadFile uploadFile = new UpLoadFile(fileSouceName,filePath,toxFileData.getFileSize(), false, true, "0",1,1,0,false,"",0,0,toxFileData.getFileId() +"",false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileSouceName+"__"+toxFileData.getFileId(),toxFileData.getFileSize(), false, true, false,1,1,0,false,0));

            }
            if(!deleteFileMap.get(toxFileData.getFileId() + ""))
            {
                if(fieType != 6)
                {
                    SendToxUploadFileNotice sendToxFileNotice = new SendToxUploadFileNotice( toxFileData.getFromId(),toxFileData.getFileName(),toxFileData.getWidthAndHeight(),toxFileData.getFileMD5(),toxFileData.getFileSize(),toxFileData.getFileType().value(),toxFileData.getSrcKey(),"UploadFile");
                    BaseData baseData = new BaseData(2,sendToxFileNotice);
                    String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                    if(ConstantValue.INSTANCE.isAntox())
                    {
                        //FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                    }else{
                        ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                    }
                }

            }else{

            }
        }
    }
    public static void  onToxSendFileProgressEvent(int fileNumber,String key,int position,int filesize)
    {
        ToxFileData toxFileData = sendToxFileDataMap.get(fileNumber+"");
        if(toxFileData != null) {
            int fieType = toxFileData.getFileType().value();
            if(fieType != 6)
            {
                String filePath = toxFileData.getFilePath();
                String fileMiName = toxFileData.getFileName();
                UpLoadFile uploadFile = new UpLoadFile(fileMiName,filePath,toxFileData.getFileSize(), false, false, "0",position,filesize,0,false,"",0,0,toxFileData.getFileId()+"",false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileMiName+"__"+toxFileData.getFileId(),toxFileData.getFileSize(), false, false, false,position,filesize,0,false,0));
            }

        }
    }
    public static void  onToxReceiveFileFinishedEvent(int fileNumber,String key)
    {

        String fileNameAndUserId = receiveToxFileNameMap.get(fileNumber+"");
        String fileMiName = fileNameAndUserId;
        String msgLocalId = "";
        String fileType = "";

        if(fileNameAndUserId.contains(":"))
        {
            String [] fileNameArray = fileNameAndUserId.split(":");
            fileMiName = fileNameArray[1];
            msgLocalId = fileNameArray[2];
            fileType = fileNameArray[3];
        }
        if(fileMiName != null)
        {
            String fileOrginName = new String(Base58.decode(fileMiName));

            String base58files_dir = PathUtils.getInstance().getTempPath() + "/" + fileOrginName;
            String files_dirTemp = PathUtils.getInstance().getFilePath() + "/" + fileOrginName;
            String filePath = PathUtils.getInstance().getFilePath().toString() + "/" + fileOrginName ;
            int code = 0;
            if(fileType.equals("3"))
            {
                String avatarPath = filePath.replace("__Avatar","");
                code = FileUtil.copyAppFileToSdcard(base58files_dir, avatarPath);
                if(code == 1)
                {
                    AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, avatarPath, System.currentTimeMillis());
                }
            }else{
                String  useKey = ConstantValue.INSTANCE.getReceiveToxFileGlobalDataMap().get(fileMiName);
                String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(useKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey(),ConstantValue.INSTANCE.getLibsodiumprivateMiKey());
                code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir,files_dirTemp,fileKey);
                UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(msgLocalId);
                String fileMiUrl = fileMiName;
                String userKey = "";
                int fileFrom = 0;
                String msgId = "";
                if(localUpLoadFile != null)
                {
                    fileMiUrl = localUpLoadFile.getPath();
                    userKey = localUpLoadFile.getUserKey();
                    fileFrom = localUpLoadFile.getFileFrom();
                    msgId = localUpLoadFile.getMsgId();
                }
                long fileSize = receiveToxFileSizeMap.get(fileNumber+"");
                UpLoadFile uploadFile = new UpLoadFile(fileMiName,fileMiUrl,fileSize, true, true, "0",1,1,0,false,userKey,0,0,msgId,false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileMiName+"__"+msgId,fileSize, true, true, false,1,1,0,false,0));
            }

        }
        receiveToxFileNameMap.remove(fileNumber+"");
    }
    public static void  onToxReceiveFileProgressEvent(int fileNumber,String key,int position,int filesize)
    {
        String fileNameAndUserId = receiveToxFileNameMap.get(fileNumber+"");
        String fileMiName = fileNameAndUserId;
        String msgLocalId = "";
        String fileType = "";
        if(fileNameAndUserId.contains(":"))
        {
            String [] fileNameArray = fileNameAndUserId.split(":");
            fileMiName = fileNameArray[1];
            msgLocalId = fileNameArray[2];
            fileType = fileNameArray[3];
        }
        if(fileMiName != null) {
            receiveToxFileSizeMap.put(fileNumber+"",(long)filesize);
            String fileSouceName = new String(Base58.decode(fileMiName));
            if(fileType.equals("3"))
            {

            }else{
                UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(msgLocalId);
                String fileMiUrl = fileMiName;
                String userKey = "";
                int fileFrom = 0;
                String msgId = "";
                if(localUpLoadFile != null)
                {
                    fileMiUrl = localUpLoadFile.getPath();
                    userKey = localUpLoadFile.getUserKey();
                    fileFrom = localUpLoadFile.getFileFrom();
                    msgId = localUpLoadFile.getMsgId();
                }
                UpLoadFile uploadFile = new UpLoadFile(fileMiName,fileMiUrl,filesize, true, false, "0",position,filesize,0,false,userKey,fileFrom,0,msgId,false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileMiName+"__"+msgId,filesize, true, false, false,position,filesize,0,false,0));
            }

        }
    }
    public static void  onAgreeReceivwFileStart(int fileNumber,String key,String fileName)
    {

        if(ConstantValue.INSTANCE.isAntox())
        {
            /*FriendKey friendKey = new FriendKey(key);
            if(friendKey != null)
            {
                receiveToxFileNameMap.put(fileNumber+"",fileName);

                MessageHelper.sendAgreeReceiveFileFromKotlin(AppConfig.instance,fileNumber,friendKey);
            }*/
        }else{
            receiveToxFileNameMap.put(fileNumber+"",fileName);
        }

    }
    public String getToxReceiveFileName(int fileNumber,String key)
    {
        String fileName = receiveToxFileNameMap.get(fileNumber+"");
        return fileName;
    }



    public static void sendVoiceFile(String filePath, int length) {

        if(sendFilePathMap.containsValue(filePath))
        {
            return;
        }
        try {
            File file = new File(filePath);
            boolean isHas = file.exists();
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            int uuidTox = (int)(System.currentTimeMillis()/1000);
            if(isHas)
            {

                if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                {

                    sendMsgLocalMap.put(uuid,false);
                    sendFilePathMap.put(uuid,filePath);
                    sendFileSize.put(uuid,file.length());
                    deleteFileMap.put(uuid,false);


                    String fileKey =  RxEncryptTool.generateAESKey();
                    byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());

                    byte[] SrcKey = new byte[256];
                    byte[] DstKey = new byte[256];
                    try {

                        if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                        {
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                            DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                        }else{
                            SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                            DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                        }
                        sendFileKeyByteMap.put(uuid,fileKey.substring(0,16));
                        sendFileMyKeyByteMap.put(uuid,SrcKey);
                        sendFileFriendKeyByteMap.put(uuid,DstKey);

                    }catch (Exception e){
                        //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));
                } else {
                    String strBase58 = Base58.encode(fileName.getBytes());
                    String base58files_dir = PathUtils.getInstance().getTempPath().toString()+"/" + strBase58;
                    String fileKey =  RxEncryptTool.generateAESKey();
                    int code =  FileUtil.copySdcardToxFileAndEncrypt(filePath,base58files_dir,fileKey.substring(0,16));
                    if(code == 1)
                    {
                        sendMsgLocalMap.put(uuidTox+"",false);
                        sendFilePathMap.put(uuidTox+"",base58files_dir);
                        sendFileSize.put(uuidTox+"",file.length());
                        deleteFileMap.put(uuidTox+"",false);
                        ToxFileData toxFileData = new ToxFileData();
                        toxFileData.setFromId(fromUserId);
                        toxFileData.setFilePath(filePath);
                        toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                        File fileMi = new File(base58files_dir);
                        long fileSize = fileMi.length();
                        String fileMD5 = FileUtil.getFileMD5(fileMi);
                        toxFileData.setFileName(strBase58);
                        toxFileData.setFileMD5(fileMD5);
                        toxFileData.setFileSize((int)fileSize);
                        toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_AUDIO);
                        toxFileData.setFileId(uuidTox);
                        byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                        byte[] SrcKey = new byte[256];
                        byte[] DstKey = new byte[256];
                        try {

                            if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                            {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                            }else{
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                            }
                        }catch (Exception e)
                        {

                        }
                        toxFileData.setSrcKey(new String(SrcKey));
                        toxFileData.setDstKey(new String(DstKey));
                        String fileNumber = "";
                        if(ConstantValue.INSTANCE.isAntox())
                        {
                            /*FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);*/
                        }else{
                            fileNumber = ToxCoreJni.getInstance().senToxFileInManger(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                        }
                        toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                        sendToxFileDataMap.put(fileNumber,toxFileData);
                        sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                    }
                }
            }else{
                if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                {
                    LocalFileUtils.INSTANCE.deleteLocalAssets(uuid);
                }else{
                    LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                }
                EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,1));
            }
        }catch (Exception e)
        {

        }

    }

    public static void cancelWebSocketWork(String msgId)
    {
        sendFilePathMap.remove(msgId);
        deleteFileMap.put(msgId,true);
        sendFileLeftByteMap.remove(msgId);
        sendFileNameMap.remove(msgId);
        sendFileLastByteSizeMap.remove(msgId);
        sendFileKeyByteMap.remove(msgId);
        sendFileMyKeyByteMap.remove(msgId);
        sendFileFriendKeyByteMap.remove(msgId);
        String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
        EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
        System.gc();
    }
    public static void cancelFileReceive(String msgId)
    {
        sendFilePathMap.remove(msgId);
        deleteFileMap.put(msgId,true);
        int fileNumber = ToxCoreJni.getInstance().reveiveFileNumberAndMsgIDMap.get(msgId);
        KLog.i("cancelFileReceive_fileNumber:"+fileNumber);
        ToxCoreJni.getInstance().cacelFileReceive1(fileNumber);
    }
    public static void cancelFileSend(String msgId)
    {
        sendFilePathMap.remove(msgId);
        deleteFileMap.put(msgId,true);
        ToxFileData toxFileData = sendMsgIdToxFileDataMap.get(msgId);
        if(toxFileData != null) {
            ToxCoreJni.getInstance().cancelFileSend(toxFileData.getFileNumber());
        }
    }
    public static void setPorperty(Integer porperty)
    {
        fromPorperty = porperty;
    }
    public static int sendImageFile(String imagePath,String msgId,boolean isCompress,Integer porperty) {
        if(sendFilePathMap.containsValue(imagePath))
        {
            return 0;
        }
        fromPorperty = porperty;
        new Thread(new Runnable(){
            public void run(){

                try
                {
                    File file = new File(imagePath);
                    boolean isHas = file.exists();
                    String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);
                    String fileNameEnd = fileName.substring(fileName.lastIndexOf("."),fileName.length());
                    String fileNamePre = fileName.substring(0,fileName.lastIndexOf("."));
                    if(fileNamePre.length() > ConstantValue.INSTANCE.getFileNameMaxLen())
                    {
                        fileNamePre = fileNamePre.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen());
                    }
//                    fileName = fileNamePre + fileNameEnd;
                    fileName = Base58.getBase58NameWithOrginName(fileNamePre, fileNameEnd);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    String widthAndHeight = "," + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
                    KLog.i("图片的宽高为：" + widthAndHeight);
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                    {
                        if(!msgId.equals(""))
                        {
                            uuid = msgId;
                        }
                    }else{
                        if(!msgId.equals(""))
                        {
                            uuidTox = Integer.valueOf(msgId);
                        }
                    }
                    if(isHas)
                    {

                        if(file.length() == 0)
                        {
                            EventBus.getDefault().post(new FileStatus(imagePath+"__"+uuid,3));
                            return;
                        }
                        if(file.length() > 1024 * 1024 * 100)
                        {
                            EventBus.getDefault().post(new FileStatus(imagePath+"__"+uuid,2));
                            return;
                        }
                        String files_dir = imagePath;
                        FileUtil.recordRecentFile(fileName, 0, 0, "Router");
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            sendFileWidthAndHeightMap.put(uuid, widthAndHeight);
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                            UpLoadFile uploadFile = new UpLoadFile(fileName,imagePath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));
                            sendFileNameMap.put(uuid,fileName);
                            sendMsgLocalMap.put(uuid,false);
                            sendFilePathMap.put(uuid,files_dir);
                            sendFileSize.put(uuid,file.length());
                            deleteFileMap.put(uuid,false);

                            String fileKey =  RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {
                                if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                }else{
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                }
                                sendFileKeyByteMap.put(uuid,fileKey.substring(0,16));
                                sendFileMyKeyByteMap.put(uuid,SrcKey);
                                sendFileFriendKeyByteMap.put(uuid,DstKey);
                            }catch (Exception e){
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));

                        }else{
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir =  PathUtils.getInstance().getTempPath().toString()+"/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            int code =  FileUtil.copySdcardToxPicAndEncrypt(imagePath,base58files_dir,fileKey.substring(0,16),isCompress);
                            if(code == 1)
                            {

                                File miFile = new File(base58files_dir);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                                UpLoadFile uploadFile = new UpLoadFile(fileName+"__"+uuidTox,imagePath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
                                MyFile myRouter = new MyFile();
                                myRouter.setType(0);
                                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                myRouter.setUpLoadFile(uploadFile);
                                LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                                EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));

                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",base58files_dir);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(imagePath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
                                toxFileData.setFileId(uuidTox);
                                toxFileData.setWidthAndHeight(widthAndHeight.substring(1, widthAndHeight.length()));
                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                    {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    }else{
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    }
                                }catch (Exception e)
                                {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if(ConstantValue.INSTANCE.isAntox())
                                {
                                   /* FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);*/
                                }else{
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInManger(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                                }
                                toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                                sendToxFileDataMap.put(fileNumber,toxFileData);
                                sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                            }else{
                                //Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                    }else{
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuid);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,1));
                        }else{
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,1));
                        }

                    }

                }catch (Exception e)
                {

                }
            }

        }).start();
        return 1;
    }

    /**
     * 头像不用加密
     * @param imagePath
     * @param msgId
     * @param isCompress
     * @return
     */
    public static int sendAvatarFile(String imagePath,String msgId,boolean isCompress) {
        new Thread(new Runnable(){
            public void run(){

                try
                {
                    File file = new File(imagePath);
                    boolean isHas = file.exists();
                    String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);
                    String fileNameEnd = fileName.substring(fileName.lastIndexOf("."),fileName.length());
                    String fileNamePre = fileName.substring(0,fileName.lastIndexOf("."));
                    if(fileNamePre.length() > ConstantValue.INSTANCE.getFileNameMaxLen())
                    {
                        fileNamePre = fileNamePre.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen());
                    }
//                    fileName = fileNamePre + fileNameEnd;
                    fileName = Base58.getBase58NameWithOrginName(fileNamePre, fileNameEnd);
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                    if(!msgId.equals(""))
                    {
                        uuid = msgId;
                    }
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if(isHas)
                    {
                        if(file.length()> 500  * 1024)
                        {
                            //EventBus.getDefault().post(new FileStatus(imagePath+"__"+uuid,3));
                            return;
                        }
                        String files_dir = imagePath;
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                            sendFileNameMap.put(uuid,fileName);
                            sendMsgLocalMap.put(uuid,false);
                            sendFilePathMap.put(uuid,files_dir);
                            sendFileSize.put(uuid,file.length());
                            deleteFileMap.put(uuid,false);

                            String fileKey =  RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {
                               /* if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                }else{
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                }*/
                                sendFileKeyByteMap.put(uuid,"");
                                sendFileMyKeyByteMap.put(uuid,SrcKey);
                                sendFileFriendKeyByteMap.put(uuid,DstKey);
                            }catch (Exception e){
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));

                        }else{
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getFilePath().toString() + "/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            int code =  FileUtil.copyAppFileToSdcard(imagePath,base58files_dir);
                            if(code == 1)
                            {


                                File miFile = new File(base58files_dir);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());

                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",base58files_dir);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(imagePath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_AVATAR);
                                toxFileData.setFileId(uuidTox);


                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                /*try {

                                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                    {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    }else{
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    }
                                }catch (Exception e)
                                {

                                }*/
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if(ConstantValue.INSTANCE.isAntox())
                                {
                                    /*FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,imagePath,friendKey);*/
                                }else{
                                    fileNumber = ToxCoreJni.getInstance().senToxAvatarFile(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                                }
                                toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                                sendToxFileDataMap.put(fileNumber,toxFileData);
                                sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                            }else{
                                //Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                    }else{
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuid);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,1));
                        }else{
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,1));
                        }

                    }

                }catch (Exception e)
                {

                }
            }

        }).start();
        return 1;
    }
    /**
     * 邮件zip文件不用加密，子文件已经加密
     * @param emailPath
     * @param msgId
     * @param isCompress
     * @return
     */
    public static int sendEmailFile(String emailPath,int msgId,boolean isCompress) {
        new Thread(new Runnable(){
            public void run(){

                try
                {
                    File file = new File(emailPath);
                    boolean isHas = file.exists();
                    String fileName = emailPath.substring(emailPath.lastIndexOf("/")+1);
                    String fileNameEnd = fileName.substring(fileName.lastIndexOf("."),fileName.length());
                    String fileNamePre = fileName.substring(0,fileName.lastIndexOf("."));
                    if(fileNamePre.length() > ConstantValue.INSTANCE.getFileNameMaxLen())
                    {
                        fileNamePre = fileNamePre.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen());
                    }
//                    fileName = fileNamePre + fileNameEnd;
                    //fileName = Base58.encode(fileName.getBytes());
                    int uuid = msgId;
                    int uuidTox = msgId;
                    if(isHas)
                    {
                        if(file.length()> 1024 * 1024 * 100)
                        {
                            EventBus.getDefault().post(new FileStatus(emailPath+"__"+uuid,2));
                            return;
                        }
                        if(file.length() == 0)
                        {
                            EventBus.getDefault().post(new FileStatus(emailPath+"__"+uuid,3));
                            return;
                        }
                        String files_dir = emailPath;
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                            sendFileNameMap.put(uuid+"",fileName);
                            sendMsgLocalMap.put(uuid+"",false);
                            sendFilePathMap.put(uuid+"",files_dir);
                            sendFileSize.put(uuid+"",file.length());
                            deleteFileMap.put(uuid+"",false);

                            String fileKey =  RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {
                                if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                }else{
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                }
                                sendFileKeyByteMap.put(uuid+"","zip");
                                sendFileMyKeyByteMap.put(uuid+"",SrcKey);
                                sendFileFriendKeyByteMap.put(uuid+"",DstKey);
                            }catch (Exception e){
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid+"",0,"",wssUrl,"lws-pnr-bin"));

                        }else{
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getFilePath().toString() + "/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            int code =  FileUtil.copyAppFileToSdcard(emailPath,base58files_dir);
                            if(code == 1)
                            {


                                File miFile = new File(base58files_dir);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());

                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",base58files_dir);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(emailPath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_AVATAR);
                                toxFileData.setFileId(uuidTox);


                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                /*try {

                                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                    {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    }else{
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    }
                                }catch (Exception e)
                                {

                                }*/
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if(ConstantValue.INSTANCE.isAntox())
                                {
                                   /* FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,emailPath,friendKey);*/
                                }else{
                                    fileNumber = ToxCoreJni.getInstance().senToxAvatarFile(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                                }
                                toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                                sendToxFileDataMap.put(fileNumber,toxFileData);
                                sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                            }else{
                                //Toast.makeText(getActivity(), R.string.senderror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                    }else{
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuid+"");
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,1));
                        }else{
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,1));
                        }

                    }

                }catch (Exception e)
                {

                }
            }

        }).start();
        return 1;
    }
    public static int sendVideoFile(String videoPath,String msgId,Integer porperty) {
        if(sendFilePathMap.containsValue(videoPath))
        {
            return 0;
        }
        fromPorperty = porperty;
        new Thread(new Runnable(){
            public void run() {

                try {
                    File file = new File(videoPath);
                    boolean isHas = file.exists();
                    String videoFileName = videoPath.substring(videoPath.lastIndexOf("/")+1);

                    String fileNameEnd = videoFileName.substring(videoFileName.lastIndexOf("."),videoFileName.length());
                    String fileNamePre = videoFileName.substring(0,videoFileName.lastIndexOf("."));
                    if(fileNamePre.length() > ConstantValue.INSTANCE.getFileNameMaxLen())
                    {
                        fileNamePre = fileNamePre.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen());
                    }
//                    videoFileName = fileNamePre + fileNameEnd;
                    videoFileName = Base58.getBase58NameWithOrginName(fileNamePre, fileNameEnd);

                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                    {
                        if(!msgId.equals(""))
                        {
                            uuid = msgId;
                        }
                    }else{
                        if(!msgId.equals(""))
                        {
                            uuidTox = Integer.valueOf(msgId);
                        }
                    }

                    if(isHas)
                    {

                        if(file.length() > 1024 * 1024 * 100)
                        {
                            EventBus.getDefault().post(new FileStatus(videoPath+"__"+uuid,2));
                            return;
                        }
                        if(file.length() == 0)
                        {
                            EventBus.getDefault().post(new FileStatus(videoPath+"__"+uuid,3));
                            return;
                        }
                        FileUtil.recordRecentFile(videoFileName, 0, 4, "Router");

                        Bitmap bitmap = EaseImageUtils.getVideoPhoto(videoPath);
                        int videoLength = EaseImageUtils.getVideoDuration(videoPath);

                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {

                            sendFileWidthAndHeightMap.put(uuid, ",200.0000000*200.0000000");
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                            UpLoadFile uploadFile = new UpLoadFile(videoFileName,videoPath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(videoFileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));

                            sendFileNameMap.put(uuid,videoFileName);
                            sendMsgLocalMap.put(uuid,false);
                            sendFilePathMap.put(uuid,videoPath);
                            sendFileSize.put(uuid,file.length());
                            deleteFileMap.put(uuid,false);

                            String fileKey =  RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                }else{
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                }
                                sendFileKeyByteMap.put(uuid,fileKey.substring(0,16));
                                sendFileMyKeyByteMap.put(uuid,SrcKey);
                                sendFileFriendKeyByteMap.put(uuid,DstKey);
                            }catch (Exception e){
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));

                        }else{
                            String strBase58 = Base58.encode(videoFileName.getBytes());
                            String base58files_dir = PathUtils.getInstance().getTempPath().toString()+"/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            int code =  FileUtil.copySdcardToxFileAndEncrypt(videoPath,base58files_dir,fileKey.substring(0,16));
                            if(code == 1)
                            {

                                File miFile = new File(base58files_dir);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                                UpLoadFile uploadFile = new UpLoadFile(videoFileName,videoPath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
                                MyFile myRouter = new MyFile();
                                myRouter.setType(0);
                                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                myRouter.setUpLoadFile(uploadFile);
                                LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                                EventBus.getDefault().post(new FileStatus(videoFileName+"__"+uuidTox,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));


                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",base58files_dir);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(videoPath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_MEDIA);
                                toxFileData.setFileId(uuidTox);

                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());

                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                    {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    }else{
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    }
                                }catch (Exception e)
                                {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber  = "";
                                if(ConstantValue.INSTANCE.isAntox())
                                {
                                   /* FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);*/
                                }else{
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInManger(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                                }
                                toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                                sendToxFileDataMap.put(fileNumber,toxFileData);
                                sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                            }
                        }

                    }else{
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuid);
                            EventBus.getDefault().post(new FileStatus(videoFileName +"__"+uuid,1));

                        }else{
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                            EventBus.getDefault().post(new FileStatus(videoFileName+"__"+uuidTox,1));
                        }

                    }
                }catch (Exception e)
                {

                }
            }

        }).start();
        return 1;
    }

    public static int sendOtherFile(String filePath,String msgId,Integer porperty) {
        if(sendFilePathMap.containsValue(filePath))
        {
            return 0;
        }
        new Thread(new Runnable(){
            public void run(){

                try
                {
                    File file = new File(filePath);
                    boolean isHas = file.exists();
                    String fileName = filePath.substring(filePath.lastIndexOf("/")+1);

                    String fileNameEnd = fileName.substring(fileName.lastIndexOf("."),fileName.length());
                    String fileNamePre = fileName.substring(0,fileName.lastIndexOf("."));
                    if(fileNamePre.length() > ConstantValue.INSTANCE.getFileNameMaxLen())
                    {
                        fileNamePre = fileNamePre.substring(0,ConstantValue.INSTANCE.getFileNameMaxLen());
                    }
//                    fileName = fileNamePre + fileNameEnd;
                    fileName = Base58.getBase58NameWithOrginName(fileNamePre, fileNameEnd);
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                    {
                        if(!msgId.equals(""))
                        {
                            uuid = msgId;
                        }
                    }else{
                        if(!msgId.equals(""))
                        {
                            uuidTox = Integer.valueOf(msgId);
                        }
                    }
                    if(isHas)
                    {
                        if(file.length() > 1024 * 1024 * 100)
                        {
                            EventBus.getDefault().post(new FileStatus(filePath+"__"+uuid,2));
                            return;
                        }
                        FileUtil.recordRecentFile(fileName, 0, 4, "Router");
                        if(file.length() == 0)
                        {
                            EventBus.getDefault().post(new FileStatus(filePath+"__"+uuid,3));
                            return;
                        }
                        String files_dir = filePath;
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {

                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                            UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));


                            sendFileNameMap.put(uuid,fileName);
                            sendMsgLocalMap.put(uuid,false);
                            sendFilePathMap.put(uuid,files_dir);
                            sendFileSize.put(uuid,file.length());
                            deleteFileMap.put(uuid,false);


                            String fileKey =  RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {

                                if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                }else{
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                }
                                sendFileKeyByteMap.put(uuid,fileKey.substring(0,16));
                                sendFileMyKeyByteMap.put(uuid,SrcKey);
                                sendFileFriendKeyByteMap.put(uuid,DstKey);
                            }catch (Exception e){
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));
                        }else{
                            String strBase58 = Base58.encode(fileName.getBytes());
                            String base58files_dir =  PathUtils.getInstance().getTempPath().toString()+"/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            int code =  FileUtil.copySdcardToxFileAndEncrypt(filePath,base58files_dir,fileKey.substring(0,16));
                            if(code == 1)
                            {

                                File miFile = new File(base58files_dir);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / ConstantValue.INSTANCE.getSendFileSizeMax());
                                UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSouceSize, false, false, "2",0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
                                MyFile myRouter = new MyFile();
                                myRouter.setType(0);
                                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                myRouter.setUpLoadFile(uploadFile);
                                LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                                EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));


                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",base58files_dir);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(filePath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(base58files_dir);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_FILE);
                                toxFileData.setFileId(uuidTox);

                                byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                                byte[] SrcKey = new byte[256];
                                byte[] DstKey = new byte[256];
                                try {

                                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                                    {
                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey,ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    }else{
                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    }
                                }catch (Exception e)
                                {

                                }
                                toxFileData.setSrcKey(new String(SrcKey));
                                toxFileData.setDstKey(new String(DstKey));

                                String fileNumber = "";
                                if(ConstantValue.INSTANCE.isAntox())
                                {
                                   /* FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);*/
                                }else {
                                    fileNumber = ToxCoreJni.getInstance().senToxFileInManger(base58files_dir,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
                                }
                                toxFileData.setFileNumber(Integer.valueOf(fileNumber));
                                sendToxFileDataMap.put(fileNumber,toxFileData);
                                sendMsgIdToxFileDataMap.put(uuidTox+"",toxFileData);
                            }

                        }
                        //FileUtil.copySdcardFile(filePath,files_dir);

                    }else{
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuid);
                            EventBus.getDefault().post(new FileStatus(fileName +"__"+uuid,1));

                        }else{
                            LocalFileUtils.INSTANCE.deleteLocalAssets(uuidTox +"");
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuidTox,1));
                        }

                    }

                }catch (Exception e)
                {

                }
            }

        }).start();
        return 1;
    }


    protected Handler handlerDown = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x404:

                    break;
                case 0x55:
                    if(conversation !=null && ConstantValue.INSTANCE.getUserId() != null)
                    {
                        String fromUserId =   SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                        Bundle data = msg.getData();
                        String msgId = data.getInt("msgID")+"";
                        Message message = receiveFileDataMap.get(msgId);
                        conversation.removeMessage(msgId);
                        String files_dir = "";
                        EMMessage messageData = null;
                        if(message != null)
                        {
                            switch (message.getMsgType())
                            {
                                case 1:
                                    files_dir = PathUtils.getInstance().getImagePath()+"/" +message.getFileName();
                                    messageData = EMMessage.createImageSendMessage(files_dir, true, toChatUserId);
                                    break;
                                case 2:
                                    files_dir = PathUtils.getInstance().getVoicePath()+"/" +message.getFileName();
                                    int longTime = FileUtil.getAmrDuration(new File(files_dir));
                                    messageData = EMMessage.createVoiceSendMessage(files_dir, longTime, toChatUserId);
                                    break;
                                case 4:
                                    files_dir = PathUtils.getInstance().getVideoPath()+"/" +message.getFileName();
                                    int beginIndex = files_dir.lastIndexOf("/")+1;
                                    int endIndex = files_dir.lastIndexOf(".")+1;
                                    if(endIndex < beginIndex)
                                    {
                                        return;
                                    }
                                    String videoName = files_dir.substring(beginIndex,endIndex);
                                    String thumbPath = PathUtils.getInstance().getImagePath()+"/"  + videoName +".png";
                                    Bitmap bitmap = EaseImageUtils.getVideoPhoto(files_dir);
                                    FileUtil.saveBitmpToFileOnThread(bitmap,thumbPath);
                                    messageData = EMMessage.createVideoSendMessage(files_dir, thumbPath,1000, toChatUserId);
                                    break;
                                case 5:
                                    files_dir = PathUtils.getInstance().getImagePath()+"/" +message.getFileName();
                                    messageData = EMMessage.createFileSendMessage(files_dir, toChatUserId);
                                    break;
                            }
                            if(messageData != null)
                            {
                                messageData.setFrom(message.getFrom());
                                messageData.setTo(message.getTo());
                                messageData.setUnread(false);

                                if(message.getFrom() == null)
                                {
                                    if(message.getSender() == 0)
                                    {
                                        messageData.setFrom(fromUserId);
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
                                        }
                                        messageData.setDirection(EMMessage.Direct.SEND );
                                    }else {
                                        messageData.setFrom(toChatUserId);
                                        messageData.setTo(fromUserId);
                                        messageData.setDirection(EMMessage.Direct.RECEIVE );
                                    }
                                }else{
                                    if(message.getFrom()!= null && message.getFrom().equals(fromUserId))
                                    {
                                        messageData.setDirection(EMMessage.Direct.SEND );
                                    }else {
                                        messageData.setDirection(EMMessage.Direct.RECEIVE );
                                    }
                                }

                                messageData.setMsgTime(message.getTimeStamp()* 1000);
                                messageData.setMsgId( message.getMsgId()+"");
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
