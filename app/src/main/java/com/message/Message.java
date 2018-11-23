package com.message;

import android.util.Base64;

import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.utils.SpUtil;

import java.util.Calendar;

public class Message {

    /**
     * MsgId : 1537262116
     * MsgType : 1
     * TimeStatmp : 1537262116
     * From : EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788
     * To : 14EB061F2A983B966B79030AF773AE74BE703315A4E56EA9D801DAC5DC840522C62EA32ECBC5
     * Msg : 。。。
     */

    private int MsgId;
    private int MsgType;
    private long TimeStatmp;
    private String From;
    private String To;
    private String Msg;
    private Type type;
    private Status status;
    private String FileName;
    private String  FilePath;
    private Long FileSize;

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    private String UserKey;


    public String getFileName() {
        //base64解码
        String FileNameOld = new String(RxEncodeTool.base64Decode(FileName.getBytes()));
        return FileNameOld;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
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
    @Override
    public String toString() {
        return "Message{" +
                "MsgId=" + MsgId +
                ", MsgType=" + MsgType +
                ", TimeStatmp=" + TimeStatmp +
                ", From='" + From + '\'' +
                ", To='" + To + '\'' +
                ", Msg='" + Msg + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", unRead=" + unRead +
                '}';
    }

    public boolean isUnRead() {
        return unRead;
    }

    public void setUnRead(boolean unRead) {
        this.unRead = unRead;
    }

    private boolean unRead;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public long getTimeStatmp() {
        return TimeStatmp;
    }

    public void setTimeStatmp(long TimeStatmp) {
        this.TimeStatmp = TimeStatmp;
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

    public static enum ChatType {
        Chat,
        GroupChat,
        ChatRoom;

        private ChatType() {
        }
    }

    public static enum Status {
        SUCCESS,
        FAIL,
        INPROGRESS,
        CREATE;

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
        message.status = Status.SUCCESS;
        message.TimeStatmp = Calendar.getInstance().getTimeInMillis();
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
        message.status = Status.CREATE;
        message.TimeStatmp = Calendar.getInstance().getTimeInMillis();
        message.To = toId;
        message.MsgId = msgId;
        message.unRead = false;
        message.setMsg(content);
        return message;
    }

}
