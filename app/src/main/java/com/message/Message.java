package com.message;

import com.hyphenate.chat.EMMessage;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.SpUtil;

import java.util.Calendar;

public class Message {

    /**
     * MsgId : 1537262116
     * MsgType : 1
     * TimeStamp : 1537262116
     * From : EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788
     * To : 14EB061F2A983B966B79030AF773AE74BE703315A4E56EA9D801DAC5DC840522C62EA32ECBC5
     * Msg : 。。。
     */

    private int MsgId;//消息id
    private int MsgType;//消息内容类型
    private int DbId;//服务器上消息数据库id
    private long TimeStamp;//时间戳
    private String From;//谁发的
    private String To;//发给谁
    private String Msg;//文字内容
    private int Point;//群聊中@
    private Type type;//消息内容类型
    private int Status;//消息状态
    private int Sender;//发送还是接收
    private String FileName;//文件名
    private String FileMd5;//文件md5
    private String  FilePath;//文件消息路径
    private String FileInfo;//文件附加信息
    private Long FileSize;//文件大小
    private String UserKey;//发送消息者的签名公钥
    private String Nonce;
    private String Sign;
    private String UserName;
    private String PriKey;//文件加密秘钥
    private int unReadCount;
    private  EMMessage.ChatType chatType;

    public String getFileInfo() {
        return FileInfo;
    }

    public void setFileInfo(String fileInfo) {
        this.FileInfo = fileInfo;
    }

    public String getFileName() {
        //bas58解码
        //String FileNameOld = new String(RxEncodeTool.base64Decode(FileName.getBytes()));
        String FileNameOld = new String(Base58.decode(FileName.replace("null", "")));
        return FileNameOld;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileMd5() {
        return FileMd5;
    }

    public void setFileMd5(String fileMd5) {
        FileMd5 = fileMd5;
    }

    @Override
    public String toString() {
        return "Message{" +
                "MsgId=" + MsgId +
                "DbId=" + DbId +
                ", MsgType=" + MsgType +
                ", TimeStamp=" + TimeStamp +
                ", From='" + From + '\'' +
                ", To='" + To + '\'' +
                ", Msg='" + Msg + '\'' +
                ", type=" + type +
                ", Status=" + Status +
                ", Nonce=" + Nonce +
                ", Sign=" + Sign +
                ", PriKey=" + PriKey +
                ", unReadCount=" + unReadCount +
                '}';
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }
    public Long getFileSize() {
        return FileSize;
    }

    public void setFileSize(Long fileSize) {
        FileSize = fileSize;
    }


    public boolean isUnRead() {
        return unRead;
    }

    public void setUnRead(boolean unRead) {
        this.unRead = unRead;
    }

    public int getPoint() {
        return Point;
    }

    public void setPoint(int point) {
        Point = point;
    }

    private boolean unRead;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }
    public int getSender() {
        return Sender;
    }

    public void setSender(int sender) {
        Sender = sender;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMsgId() {
        return MsgId;
    }

    public void setMsgId(int MsgId) {
        this.MsgId = MsgId;
    }

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int MsgType) {
        this.MsgType = MsgType;
    }
    public int getDbId() {
        return DbId;
    }

    public void setDbId(int dbId) {
        DbId = dbId;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long TimeStatmp) {
        this.TimeStamp = TimeStatmp;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String To) {
        this.To = To;
    }

    public String getMsg() {
       /* try{
            String encryptedBytes = new String(RxEncodeTool.base64Decode(Msg));
            return encryptedBytes;
        }catch (IllegalArgumentException e)
        {
            return Msg;
        }*/
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    //1为发送，0为接受
    public int getItemType() {
        if (From.equals(SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), ""))) {
            return 1;
        } else {
            return 0;
        }
    }
    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }
    public static enum ChatType {
        Chat,
        GroupChat,
        ChatRoom;

        private ChatType() {
        }
    }

    public static enum Status {
        SUCCESS,//发送成功
        SENDED,//表示消息已推送到对端
        LOOKED,//表示消息对端已阅
        FAIL,//发送失败
        CREATE;//本地创建成功，为了显示

        private Status() {
        }
    }

    public static enum Direct {
        SEND,
        RECEIVE;

        private Direct() {
        }
    }

    public static enum Type {
        TXT,
        IMAGE,
        VIDEO,
        LOCATION,
        VOICE,
        FILE,
        CMD;

        private Type() {
        }
    }

    public void setType() {
        switch (MsgType) {
            case 0:
                type = Type.TXT;
                break;
            default:
                break;
        }
    }

    /**
     * 接收到消息，生成一个消息
     * @param content
     * @param fromId
     * @return
     */
    public static Message createReceivedMessage(String content, String fromId, int msgId, String toId) {
        Message message = new Message();
        message.setType(Type.TXT);
        message.MsgType = 0;
        message.From = fromId;
        message.Status = 1;
        message.TimeStamp = Calendar.getInstance().getTimeInMillis();
        message.To = toId;
        message.MsgId = msgId;
        message.unRead = true;
        message.setMsg(content);
        return message;
    }
    /**
     * 接收到消息，生成一个消息
     * @param content
     * @param fromId
     * @return
     */
    public static Message createSendMessage(String content, String fromId, int msgId, String toId) {
        Message message = new Message();
        message.setType(Type.TXT);
        message.MsgType = 0;
        message.From = fromId;
        message.Status = 0;
        message.TimeStamp = Calendar.getInstance().getTimeInMillis();
        message.To = toId;
        message.MsgId = msgId;
        message.unRead = false;
        message.setMsg(content);
        return message;
    }

    public String getNonce() {
        return Nonce;
    }

    public void setNonce(String nonce) {
        Nonce = nonce;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPriKey() {
        return PriKey;
    }

    public void setPriKey(String priKey) {
        PriKey = priKey;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public EMMessage.ChatType getChatType() {
        return chatType;
    }

    public void setChatType(EMMessage.ChatType chatType) {
        this.chatType = chatType;
    }
}
