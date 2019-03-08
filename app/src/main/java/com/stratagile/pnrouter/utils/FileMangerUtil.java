package com.stratagile.pnrouter.utils;

import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.UserEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;


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




    protected static final int sendFileSizeMax = 1024 * 1024 * 2;
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

    private static HashMap<String, Boolean> sendMsgLocalMap = new HashMap<>();
    private static HashMap<String, String> sendFilePathMap = new HashMap<>();
    private static HashMap<String, ToxFileData> sendToxFileDataMap = new HashMap<>();
    private static HashMap<String, ToxFileData> sendMsgIdToxFileDataMap = new HashMap<>();
    private static HashMap<String, Boolean> deleteFileMap = new HashMap<>();
    private static HashMap<String, String> receiveToxFileNameMap = new HashMap<>();
    private static HashMap<String, Long> receiveToxFileSizeMap = new HashMap<>();
    private static HashMap<String, String> sendFileKeyByteMap = new HashMap<>();
    private static HashMap<String, byte[]> sendFileFriendKeyByteMap = new HashMap<>();
    private static HashMap<String, byte[]> sendFileMyKeyByteMap = new HashMap<>();
    private static HashMap<String, String> sendFileNameMap = new HashMap<>();
    private static HashMap<String, Integer> sendFileTotalSegment = new HashMap<>();
    private static HashMap<String, Long> sendFileSize = new HashMap<>();
    private static HashMap<String, Integer> sendFileLastByteSizeMap = new HashMap<>();
    private static HashMap<String, byte[]> sendFileLeftByteMap = new HashMap<>();
    private static HashMap<String, String> sendMsgIdMap = new HashMap<>();
    private static HashMap<String, Message> receiveFileDataMap = new HashMap<>();
    private static long  faBegin;
    private static long faEnd;

    public static void  init()
    {
        sendMsgLocalMap = new HashMap<>();
        sendFilePathMap = new HashMap<>();
        sendToxFileDataMap = new HashMap<>();
        sendMsgIdToxFileDataMap = new HashMap<>();
        deleteFileMap = new HashMap<>();
        receiveToxFileNameMap = new HashMap<>();
        receiveToxFileSizeMap = new HashMap<>();
        sendFileKeyByteMap = new HashMap<>();
        sendFileFriendKeyByteMap = new HashMap<>();
        sendFileMyKeyByteMap = new HashMap<>();
        sendFileNameMap = new HashMap<>();
        sendFileTotalSegment = new HashMap<>();
        sendFileSize = new HashMap<>();
        sendFileLastByteSizeMap = new HashMap<>();
        sendFileLeftByteMap = new HashMap<>();
        sendMsgIdMap = new HashMap<>();
        receiveFileDataMap = new HashMap<>();
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

                        try
                        {
                            String filePath = sendFilePathMap.get(fileTransformEntity.getToId());
                            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                            String fileKey = sendFileKeyByteMap.get(fileTransformEntity.getToId());
                            byte[] SrcKey = sendFileMyKeyByteMap.get(fileTransformEntity.getToId());
                            byte[] DstKey = sendFileFriendKeyByteMap.get(fileTransformEntity.getToId());
                            File file = new File(filePath);
                            if(file.exists())
                            {
                                long fileSize = file.length();
                                String fileMD5 = FileUtil.getFileMD5(file);
                                byte[] fileBuffer= FileUtil.file2Byte(filePath);
                                int fileId = (int)(System.currentTimeMillis()/1000);
                                byte[] fileBufferMi = new byte[0];
                                try{
                                    long  miBegin = System.currentTimeMillis();
                                    if(!fileName.contains("__Avatar.jpg"))//头像不用加密
                                    {
                                        fileBufferMi = AESCipher.aesEncryptBytes(fileBuffer,fileKey.getBytes("UTF-8"));
                                    }
                                    int totlSegment = (int)Math.ceil(fileBufferMi.length / sendFileSizeMax);
                                    sendFileTotalSegment.put(filePath,totlSegment);
                                    long miend  = System.currentTimeMillis();
                                    KLog.i("jiamiTime:"+ (miend - miBegin)/1000);
                                    faBegin = System.currentTimeMillis();
                                    if(!deleteFileMap.get(fileTransformEntity.getToId()))
                                    {
                                        sendFileByteData(fileBuffer,fileName,fromUserId,"",fileTransformEntity.getToId(),fileId,1,fileKey,SrcKey,DstKey);
                                        int segSeqTotal = sendFileTotalSegment.get(filePath);
                                        UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, false, false,0,segSeqTotal,10,false,"",0,0,fileTransformEntity.getToId(),false);
                                        MyFile myRouter = new MyFile();
                                        myRouter.setType(0);
                                        myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                        myRouter.setUpLoadFile(uploadFile);
                                        LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                                        EventBus.getDefault().post(new FileStatus(fileName+"__"+fileTransformEntity.getToId(),fileSize, false, false, false,0,segSeqTotal,10,false,0));
                                    }else{
                                        KLog.i("websocket文件上传前取消！");
                                        String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
                                        EventBus.getDefault().post(new FileMangerTransformEntity(fileTransformEntity.getToId(),4,"",wssUrl,"lws-pnr-bin"));
                                    }

                                }catch (Exception e)
                                {
                                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
                                    EventBus.getDefault().post(new FileMangerTransformEntity(fileTransformEntity.getToId(),4,"",wssUrl,"lws-pnr-bin"));
                                }
                            }
                        }catch (Exception e)
                        {

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
        switch (CodeResult)
        {
            case 0:
                int lastSendSize = sendFileLastByteSizeMap.get(FileIdResult+"");
                byte[] fileBuffer = sendFileLeftByteMap.get(FileIdResult+"");
                int leftSize =fileBuffer.length - lastSendSize;
                String msgId = sendMsgIdMap.get(FileIdResult+"");
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
                                System.arraycopy(fileBuffer, sendFileSizeMax, fileLeftBuffer, 0, leftSize);
                                String fileName = sendFileNameMap.get(FileIdResult+"");
                                String fileKey = sendFileKeyByteMap.get(FileIdResult+"");
                                byte[] SrcKey = sendFileMyKeyByteMap.get(FileIdResult+"");
                                byte[] DstKey = sendFileFriendKeyByteMap.get(FileIdResult+"");
                                if(!deleteFileMap.get(msgId))
                                {
                                    sendFileByteData(fileLeftBuffer,fileName,FromIdResult+"","",msgId,FileIdResult,SegSeqResult +1,fileKey,SrcKey,DstKey);

                                    int sended = SegSeqResult - 1;
                                    if(sended < 0 )
                                        sended = 0;
                                    KLog.i("websocket文件上传进度："+sended +"_"+fileTotalSegment);
                                    UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, false, false,sended, fileTotalSegment,10,false,"",0,0,msgId,false);
                                    MyFile myRouter = new MyFile();
                                    myRouter.setType(0);
                                    myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                                    myRouter.setUpLoadFile(uploadFile);
                                    LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);

                                    EventBus.getDefault().post(new FileStatus(fileName+"__"+msgId,fileSize, false, false, false,sended, fileTotalSegment,10,false,0));
                                }else{
                                    KLog.i("websocket文件上传中取消！");
                                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
                                    EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
                                }
                            }catch (Exception e)
                            {

                            }
                        }
                    }).start();

                }else{

                    int segSeqTotal = sendFileTotalSegment.get(filePath);
                    String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
                    UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSize, false, true, false,segSeqTotal,segSeqTotal,0,false,"",0,0,msgId,false);
                    MyFile myRouter = new MyFile();
                    myRouter.setType(0);
                    myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                    myRouter.setUpLoadFile(uploadFile);
                    LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                    EventBus.getDefault().post(new FileStatus(fileName+"__"+msgId,fileSize, false, true, false,segSeqTotal,segSeqTotal,0,false,0));

                    //EventBus.getDefault().post(new FileStatus(filePath,fileSize,fileSize,0));
                    KLog.i("websocket文件上传成功！");
                    sendFilePathMap.remove(msgId);
                    faEnd = System.currentTimeMillis();
                    KLog.i("faTime:"+ (faEnd - faBegin)/1000);
                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
                    EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
                    if(fileName.contains("__Avatar.jpg"))
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
                                FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            }
                        }
                    }

                }
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:
                break;
            case 5:
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
            if(fileName.contains("__Avatar.jpg"))
            {
                action = 6;
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
            //String strBase64 = RxEncodeTool.base64Encode2String(fileName.getBytes());
            String strBase58 = Base58.encode(fileName.getBytes());
            sendFileData.setFileName(strBase58.getBytes());
            sendFileData.setFromId(From.getBytes());
            byte[] toRouter = new byte[77];
            Arrays.fill(toRouter,(byte) 0);
            sendFileData.setToId(toRouter);
            sendFileData.setSrcKey(SrcKey);
            sendFileData.setDstKey(DstKey);
            byte[] content = new byte[sendFileSizeMax];
            System.arraycopy(fileLeftBuffer, 0, content, 0, segSize);
            sendFileData.setContent(content);
            byte[] sendData = sendFileData.toByteArray();
            //int newCRC = CRC16Util.getCRC(sendData,sendData.length);
            int newCRC = 1;
            sendFileData.setCRC(FormatTransfer.reverseShort((short)newCRC));
            sendData = sendFileData.toByteArray();
            sendFileNameMap.put(fileId+"",fileName);
            sendFileLastByteSizeMap.put(fileId+"",segSize);
            sendFileLeftByteMap.put(fileId+"",fileLeftBuffer);
            sendMsgIdMap.put(fileId+"",msgId);
            sendFileKeyByteMap.put(fileId+"",fileKey);
            sendFileMyKeyByteMap.put(fileId+"",SrcKey);
            sendFileFriendKeyByteMap.put(fileId+"",DstKey);
            //KLog.i("发送中>>>内容"+"content:"+aabb);
            KLog.i("发送中>>>"+"strBase58:"+strBase58+"segMore:"+segMore+"  " +"segSize:"+ segSize  +"   " + "left:"+ (fileLeftBuffer.length -segSize) +"  segSeq:"+segSeq  +"  fileOffset:"+fileOffset +"  setSegSize:"+sendFileData.getSegSize()+" CRC:"+newCRC);
            EventBus.getDefault().post(new FileMangerTransformMessage(msgId,sendData));

        }catch (Exception e)
        {
            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
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
            String fileMiName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());

            if(fileMiName.contains("__Avatar.jpg"))
            {
                String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                String fileBase58Name = Base58.encode(fileMiName.getBytes());
                String fileMD5 = FileUtil.getFileMD5(new File(filePath));
                UploadAvatarReq uploadAvatarReq = new UploadAvatarReq( userId, fileBase58Name,fileMD5,"UploadAvatar");
                BaseData baseData = new BaseData(4,uploadAvatarReq);
                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                if (ConstantValue.INSTANCE.isAntox()) {
                    FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                }
            }else{
                UpLoadFile uploadFile = new UpLoadFile(fileMiName,filePath,toxFileData.getFileSize(), false, true, false,1,1,0,false,"",0,0,toxFileData.getFileId() +"",false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileMiName+"__"+toxFileData.getFileId(),toxFileData.getFileSize(), false, true, false,1,1,0,false,0));

            }
            if(!deleteFileMap.get(toxFileData.getFileId() + ""))
            {
                if(!fileMiName.contains("__Avatar.jpg"))
                {
                    SendToxUploadFileNotice sendToxFileNotice = new SendToxUploadFileNotice( toxFileData.getFromId(),toxFileData.getFileName(),toxFileData.getFileMD5(),toxFileData.getFileSize(),toxFileData.getFileType().value(),toxFileData.getSrcKey(),"UploadFile");
                    BaseData baseData = new BaseData(2,sendToxFileNotice);
                    String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                    if(ConstantValue.INSTANCE.isAntox())
                    {
                        FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
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
            String filePath = toxFileData.getFilePath();
            String fileMiName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
            UpLoadFile uploadFile = new UpLoadFile(fileMiName,filePath,toxFileData.getFileSize(), false, false, false,position,filesize,0,false,"",0,0,toxFileData.getFileId()+"",false);
            MyFile myRouter = new MyFile();
            myRouter.setType(0);
            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
            myRouter.setUpLoadFile(uploadFile);
            LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
            EventBus.getDefault().post(new FileStatus(fileMiName+"__"+toxFileData.getFileId(),toxFileData.getFileSize(), false, false, false,position,filesize,0,false,0));
        }
    }
    public static void  onToxReceiveFileFinishedEvent(int fileNumber,String key)
    {



        String fileNameAndUserId = receiveToxFileNameMap.get(fileNumber+"");
        String fileMiName = fileNameAndUserId;
        long fileSize = receiveToxFileSizeMap.get(fileNumber+"");
        if(fileNameAndUserId.contains(":"))
        {
            String [] fileNameArray = fileNameAndUserId.split(":");
            fileMiName = fileNameArray[1];
        }
        if(fileMiName != null)
        {
            String fileOrginName = new String(Base58.decode(fileMiName));

            String base58files_dir = PathUtils.getInstance().getTempPath() + "/" + fileOrginName;
            String files_dirTemp = PathUtils.getInstance().getFilePath() + "/" + fileOrginName;
            String  useKey = ConstantValue.INSTANCE.getReceiveToxFileGlobalDataMap().get(fileMiName);
            String fileKey = LibsodiumUtil.INSTANCE.DecryptShareKey(useKey);
            String filePath = Environment.getExternalStorageDirectory().toString() + ConstantValue.INSTANCE.getLocalPath()+"/Avatar/" + fileOrginName ;
            int code = 0;
            if(fileOrginName.contains("__Avatar.jpg"))
            {
                code = FileUtil.copyAppFileToSdcard(base58files_dir, filePath);
            }else{
                code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir,files_dirTemp,fileKey);
            }
            UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(fileMiName);
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
            if(fileOrginName.contains("__Avatar.jpg"))
            {

            }else{
                UpLoadFile uploadFile = new UpLoadFile(fileMiName,fileMiUrl,fileSize, true, true, false,1,1,0,false,userKey,0,0,msgId,false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileMiName+"__"+msgId,fileSize, true, true, false,1,1,0,false,0));
            }

        }
    }
    public static void  onToxReceiveFileProgressEvent(int fileNumber,String key,int position,int filesize)
    {
        String fileNameAndUserId = receiveToxFileNameMap.get(fileNumber+"");
        String fileMiName = fileNameAndUserId;
        if(fileNameAndUserId.contains(":"))
        {
            String [] fileNameArray = fileNameAndUserId.split(":");
            fileMiName = fileNameArray[1];
        }
        if(fileMiName != null) {
            receiveToxFileSizeMap.put(fileNumber+"",(long)filesize);
            UpLoadFile localUpLoadFile =  LocalFileUtils.INSTANCE.getLocalAssets(fileMiName);
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
            UpLoadFile uploadFile = new UpLoadFile(fileMiName,fileMiUrl,filesize, true, false, false,position,filesize,0,false,userKey,fileFrom,0,msgId,false);
            MyFile myRouter = new MyFile();
            myRouter.setType(0);
            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
            myRouter.setUpLoadFile(uploadFile);
            LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
            EventBus.getDefault().post(new FileStatus(fileMiName+"__"+msgId,filesize, true, false, false,position,filesize,0,false,0));
        }
    }
    public static void  onAgreeReceivwFileStart(int fileNumber,String key,String fileName)
    {

        if(ConstantValue.INSTANCE.isAntox())
        {
            FriendKey friendKey = new FriendKey(key);
            if(friendKey != null)
            {
                receiveToxFileNameMap.put(fileNumber+"",fileName);

                MessageHelper.sendAgreeReceiveFileFromKotlin(AppConfig.instance,fileNumber,friendKey);
            }
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


                    String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
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
                            FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                            fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);
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
        String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
        EventBus.getDefault().post(new FileMangerTransformEntity(msgId,4,"",wssUrl,"lws-pnr-bin"));
    }
    public static void cancelToxWork(String msgId)
    {
        sendFilePathMap.remove(msgId);
        ToxFileData toxFileData = sendMsgIdToxFileDataMap.get(msgId);
        if(toxFileData != null) {
            ToxCoreJni.getInstance().cancelFileSend(toxFileData.getFileNumber());
        }
    }
    public static int sendImageFile(String imagePath,String msgId,boolean isCompress) {
        if(sendFilePathMap.containsValue(imagePath))
        {
            return 0;
        }
        new Thread(new Runnable(){
            public void run(){

                try
                {
                    File file = new File(imagePath);
                    boolean isHas = file.exists();
                    String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);

                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    if(!msgId.equals(""))
                    {
                        uuid = msgId;
                    }
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if(isHas)
                    {
                        if(file.length() == 0)
                        {
                            EventBus.getDefault().post(new FileStatus(imagePath+"__"+uuid,3));
                            return;
                        }
                        String files_dir = imagePath;
                        FileUtil.recordRecentFile(fileName, 0, 0, "Router");
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                            UpLoadFile uploadFile = new UpLoadFile(fileName,imagePath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));
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
                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
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
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                                UpLoadFile uploadFile = new UpLoadFile(fileName,imagePath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
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
                                    FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);
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

                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    if(!msgId.equals(""))
                    {
                        uuid = msgId;
                    }
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
                    if(isHas)
                    {
                        String fileMD566 = FileUtil.getFileMD5(new File(imagePath));
                        if(file.length()> 500  * 1024)
                        {
                            //EventBus.getDefault().post(new FileStatus(imagePath+"__"+uuid,3));
                            return;
                        }
                        String files_dir = imagePath;
                        if( ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI"))
                        {
                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
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
                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileMangerTransformEntity(uuid,0,"",wssUrl,"lws-pnr-bin"));

                        }else{
                            String strBase58 = Base58.encode(fileName.getBytes());
                            //String base58files_dir =  PathUtils.getInstance().getTempPath().toString()+"/" + strBase58;
                            String fileKey =  RxEncryptTool.generateAESKey();
                            //int code =  FileUtil.copySdcardToxPicAndEncrypt(imagePath,base58files_dir,fileKey.substring(0,16),isCompress);
                            if(true)
                            {

                                File miFile = new File(imagePath);
                                long fileSouceSize = miFile.length();
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);

                                sendMsgLocalMap.put(uuidTox+"",false);
                                sendFilePathMap.put(uuidTox+"",imagePath);
                                sendFileSize.put(uuidTox+"",file.length());
                                deleteFileMap.put(uuidTox+"",false);

                                ToxFileData toxFileData = new ToxFileData();
                                toxFileData.setFromId(fromUserId);
                                toxFileData.setFilePath(imagePath);
                                toxFileData.setToId(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                File fileMi = new File(imagePath);
                                long fileSize = fileMi.length();
                                String fileMD5 = FileUtil.getFileMD5(fileMi);
                                toxFileData.setFileName(strBase58);
                                toxFileData.setFileMD5(fileMD5);
                                toxFileData.setFileSize((int)fileSize);
                                toxFileData.setFileType(ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE);
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
                                    FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,imagePath,friendKey);
                                }else{
                                    fileNumber = ToxCoreJni.getInstance().senToxAvatarFile(imagePath,  ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64),uuidTox+"") +"";
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
    public static int sendVideoFile(String videoPath,String msgId) {
        if(sendFilePathMap.containsValue(videoPath))
        {
            return 0;
        }
        new Thread(new Runnable(){
            public void run() {

                try {
                    File file = new File(videoPath);
                    boolean isHas = file.exists();
                    String videoFileName = videoPath.substring(videoPath.lastIndexOf("/")+1);
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    if(!msgId.equals(""))
                    {
                        uuid = msgId;
                    }
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
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


                            long fileSouceSize = file.length();
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                            UpLoadFile uploadFile = new UpLoadFile(videoFileName,videoPath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(videoFileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));


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

                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
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
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                                UpLoadFile uploadFile = new UpLoadFile(videoFileName,videoPath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
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
                                    FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);
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

    public static int sendOtherFile(String filePath,String msgId) {
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
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    if(!msgId.equals(""))
                    {
                        uuid = msgId;
                    }
                    int uuidTox = (int)(System.currentTimeMillis()/1000);
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
                            int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                            UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuid,false);
                            MyFile myRouter = new MyFile();
                            myRouter.setType(0);
                            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                            myRouter.setUpLoadFile(uploadFile);
                            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                            EventBus.getDefault().post(new FileStatus(fileName+"__"+uuid,fileSouceSize, false, false, false,0,segSeqTotal,0,false,0));



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

                            String wssUrl = "https://"+ConstantValue.INSTANCE.getCurrentIp() + ConstantValue.INSTANCE.getFilePort();
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
                                int segSeqTotal = (int)Math.ceil(fileSouceSize / sendFileSizeMax);
                                UpLoadFile uploadFile = new UpLoadFile(fileName,filePath,fileSouceSize, false, false, false,0,segSeqTotal,0,false,"",0,0,uuidTox+"",false);
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
                                    FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance,base58files_dir,friendKey);
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
                                    FileUtil.saveBitmpToFile(bitmap,thumbPath);
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

                                messageData.setMsgTime(message.getTimeStatmp()* 1000);
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
