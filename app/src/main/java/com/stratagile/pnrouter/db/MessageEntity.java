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

    private String type;  // 0 文本，1图片，2语音，3视频，4其他文件

    private Boolean complete;//是否完成

    private String baseData; //文本数据

    private String filePath; //文件路径

    private String friendSignPublicKey; //文件数据

    private String friendMiPublicKey; //文件数据

    private int voiceTimeLen;//语音长度

    private String widthAndHeight;  //图片的宽和高   ",30*40"
    private String porperty;//0点对点 ，1 群聊


    @Generated(hash = 1514710577)
    public MessageEntity(Long id, String userId, String friendId, String msgId,
            String sendTime, String type, Boolean complete, String baseData,
            String filePath, String friendSignPublicKey, String friendMiPublicKey,
            int voiceTimeLen, String widthAndHeight, String porperty) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.msgId = msgId;
        this.sendTime = sendTime;
        this.type = type;
        this.complete = complete;
        this.baseData = baseData;
        this.filePath = filePath;
        this.friendSignPublicKey = friendSignPublicKey;
        this.friendMiPublicKey = friendMiPublicKey;
        this.voiceTimeLen = voiceTimeLen;
        this.widthAndHeight = widthAndHeight;
        this.porperty = porperty;
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
        filePath = in.readString();
        friendSignPublicKey = in.readString();
        friendMiPublicKey = in.readString();
        voiceTimeLen = in.readInt();
        widthAndHeight = in.readString();
        porperty = in.readString();
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
        dest.writeString(filePath);
        dest.writeString(friendSignPublicKey);
        dest.writeString(friendMiPublicKey);
        dest.writeInt(voiceTimeLen);
        dest.writeString(widthAndHeight);
        dest.writeString(porperty);
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFriendSignPublicKey() {
        return friendSignPublicKey;
    }

    public void setFriendSignPublicKey(String friendSignPublicKey) {
        this.friendSignPublicKey = friendSignPublicKey;
    }

    public String getFriendMiPublicKey() {
        return friendMiPublicKey;
    }

    public void setFriendMiPublicKey(String friendMiPublicKey) {
        this.friendMiPublicKey = friendMiPublicKey;
    }

    public int getVoiceTimeLen() {
        return voiceTimeLen;
    }

    public void setVoiceTimeLen(int voiceTimeLen) {
        this.voiceTimeLen = voiceTimeLen;
    }

    public String getWidthAndHeight() {
        return widthAndHeight;
    }

    public void setWidthAndHeight(String widthAndHeight) {
        this.widthAndHeight = widthAndHeight;
    }

    public String getPorperty() {
        return porperty;
    }

    public void setPorperty(String porperty) {
        this.porperty = porperty;
    }
}
