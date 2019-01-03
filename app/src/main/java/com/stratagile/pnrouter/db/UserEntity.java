package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.stratagile.pnrouter.utils.RxEncodeTool;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String userId;

    //路由器用户id
    private String routerUserId;

    private String publicKey;

    //昵称
    private String nickName;

    //用户备注
    private String remarks;
    //昵称
    private String nickSouceName;
    //头像
    private String avatar;
    //备注名字
    private String noteName;
    //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方 7 什么都不是，等待发起加好友
    private int friendStatus;
    //是否为我加对方
    private boolean addFromMe;
    //第一次通信的时间戳
    private long timestamp;

    private String validationInfo;//添加好友附言




    @Generated(hash = 906349994)
    public UserEntity(Long id, String userId, String routerUserId, String publicKey,
            String nickName, String remarks, String nickSouceName, String avatar,
            String noteName, int friendStatus, boolean addFromMe, long timestamp,
            String validationInfo) {
        this.id = id;
        this.userId = userId;
        this.routerUserId = routerUserId;
        this.publicKey = publicKey;
        this.nickName = nickName;
        this.remarks = remarks;
        this.nickSouceName = nickSouceName;
        this.avatar = avatar;
        this.noteName = noteName;
        this.friendStatus = friendStatus;
        this.addFromMe = addFromMe;
        this.timestamp = timestamp;
        this.validationInfo = validationInfo;
    }
    @Generated(hash = 1433178141)
    public UserEntity() {
    }


    protected UserEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readString();
        routerUserId = in.readString();
        publicKey = in.readString();
        nickName = in.readString();
        remarks = in.readString();
        nickSouceName = in.readString();
        avatar = in.readString();
        noteName = in.readString();
        friendStatus = in.readInt();
        addFromMe = in.readByte() != 0;
        timestamp = in.readLong();
        validationInfo = in.readString();
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
        dest.writeString(routerUserId);
        dest.writeString(publicKey);
        dest.writeString(nickName);
        dest.writeString(remarks);
        dest.writeString(nickSouceName);
        dest.writeString(avatar);
        dest.writeString(noteName);
        dest.writeInt(friendStatus);
        dest.writeByte((byte) (addFromMe ? 1 : 0));
        dest.writeLong(timestamp);
        dest.writeString(validationInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getNoteName() {
        return this.noteName;
    }
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }
    public int getFriendStatus() {
        return this.friendStatus;
    }
    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }
    public boolean getAddFromMe() {
        return this.addFromMe;
    }
    public void setAddFromMe(boolean addFromMe) {
        this.addFromMe = addFromMe;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public String getRouterUserId() {
        return this.routerUserId;
    }
    public void setRouterUserId(String routerUserId) {
        this.routerUserId = routerUserId;
    }
    public String getNickSouceName() {
        return this.nickSouceName;
    }
    public void setNickSouceName(String nickSouceName) {
        this.nickSouceName = nickSouceName;
    }
    public String getValidationInfo() {
        return this.validationInfo;
    }
    public void setValidationInfo(String validationInfo) {
        this.validationInfo = validationInfo;
    }
}
