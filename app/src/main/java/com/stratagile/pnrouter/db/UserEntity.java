package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String userId;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", noteName='" + noteName + '\'' +
                ", friendStatus=" + friendStatus +
                ", addFromMe=" + addFromMe +
                ", timestamp=" + timestamp +
                '}';
    }

    //昵称
    private String nickName;
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
    @Generated(hash = 456718967)
    public UserEntity(Long id, String userId, String nickName, String avatar,
            String noteName, int friendStatus, boolean addFromMe, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.nickName = nickName;
        this.avatar = avatar;
        this.noteName = noteName;
        this.friendStatus = friendStatus;
        this.addFromMe = addFromMe;
        this.timestamp = timestamp;
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
        nickName = in.readString();
        avatar = in.readString();
        noteName = in.readString();
        friendStatus = in.readInt();
        addFromMe = in.readByte() != 0;
        timestamp = in.readLong();
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
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(userId);
        parcel.writeString(nickName);
        parcel.writeString(avatar);
        parcel.writeString(noteName);
        parcel.writeInt(friendStatus);
        parcel.writeByte((byte) (addFromMe ? 1 : 0));
        parcel.writeLong(timestamp);
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
}
