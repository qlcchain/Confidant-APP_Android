package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MessageEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String userId;

    private String friendId;

    private String msgId; //消息id

    private String sendTime;//发送时间

    private String type;  // 0 文本，1文件

    private Boolean complete;//是否完成

    private String baseData; //文本数据

    private String fileLeftBuffer; //文件数据

    private String fileName; //文件数据

    private String fileId; //文件数据

    private String segSeq; //文件数据

    private String fileKey; //文件数据

    private String SrcKey; //文件数据

    private String DstKey; //文件数据

    @Generated(hash = 327849575)
    public MessageEntity(Long id, String userId, String friendId, String msgId,
            String sendTime, String type, Boolean complete, String baseData,
            String fileLeftBuffer, String fileName, String fileId, String segSeq,
            String fileKey, String SrcKey, String DstKey) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.msgId = msgId;
        this.sendTime = sendTime;
        this.type = type;
        this.complete = complete;
        this.baseData = baseData;
        this.fileLeftBuffer = fileLeftBuffer;
        this.fileName = fileName;
        this.fileId = fileId;
        this.segSeq = segSeq;
        this.fileKey = fileKey;
        this.SrcKey = SrcKey;
        this.DstKey = DstKey;
    }

    @Generated(hash = 1797882234)
    public MessageEntity() {
    }

    protected MessageEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readString();
        friendId = in.readString();
        msgId = in.readString();
        sendTime = in.readString();
        type = in.readString();
        byte tmpComplete = in.readByte();
        complete = tmpComplete == 0 ? null : tmpComplete == 1;
        baseData = in.readString();
        fileLeftBuffer = in.readString();
        fileName = in.readString();
        fileId = in.readString();
        segSeq = in.readString();
        fileKey = in.readString();
        SrcKey = in.readString();
        DstKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(userId);
        dest.writeString(friendId);
        dest.writeString(msgId);
        dest.writeString(sendTime);
        dest.writeString(type);
        dest.writeByte((byte) (complete == null ? 0 : complete ? 1 : 2));
        dest.writeString(baseData);
        dest.writeString(fileLeftBuffer);
        dest.writeString(fileName);
        dest.writeString(fileId);
        dest.writeString(segSeq);
        dest.writeString(fileKey);
        dest.writeString(SrcKey);
        dest.writeString(DstKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
        @Override
        public MessageEntity createFromParcel(Parcel in) {
            return new MessageEntity(in);
        }

        @Override
        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getBaseData() {
        return baseData;
    }

    public void setBaseData(String baseData) {
        this.baseData = baseData;
    }

    public String getFileLeftBuffer() {
        return fileLeftBuffer;
    }

    public void setFileLeftBuffer(String fileLeftBuffer) {
        this.fileLeftBuffer = fileLeftBuffer;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSegSeq() {
        return segSeq;
    }

    public void setSegSeq(String segSeq) {
        this.segSeq = segSeq;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getSrcKey() {
        return SrcKey;
    }

    public void setSrcKey(String srcKey) {
        SrcKey = srcKey;
    }

    public String getDstKey() {
        return DstKey;
    }

    public void setDstKey(String dstKey) {
        DstKey = dstKey;
    }
}
