package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FriendEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    //用户id
    private String userId;
    //用户hashid，14位字符串,不可为空
    private String index;
    //好友id
    private String friendId;

    //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方 7 什么都不是，等待发起加好友
    private int friendLocalStatus = 7;

    //是否为我加对方
    private boolean addFromMe;

    //第一次通信的时间戳
    private long timestamp;

    @Generated(hash = 834006476)
    public FriendEntity() {
    }
    protected FriendEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        userId = in.readString();
        index = in.readString();
        friendId = in.readString();
        friendLocalStatus = in.readInt();
        addFromMe = in.readByte() != 0;
        timestamp = in.readLong();
    }
    @Generated(hash = 1914995724)
    public FriendEntity(Long id, String userId, String index, String friendId,
            int friendLocalStatus, boolean addFromMe, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.index = index;
        this.friendId = friendId;
        this.friendLocalStatus = friendLocalStatus;
        this.addFromMe = addFromMe;
        this.timestamp = timestamp;
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
        dest.writeString(index);
        dest.writeString(friendId);
        dest.writeInt(friendLocalStatus);
        dest.writeByte((byte) (addFromMe ? 1 : 0));
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FriendEntity> CREATOR = new Creator<FriendEntity>() {
        @Override
        public FriendEntity createFromParcel(Parcel in) {
            return new FriendEntity(in);
        }

        @Override
        public FriendEntity[] newArray(int size) {
            return new FriendEntity[size];
        }
    };

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean isAddFromMe() {
        return addFromMe;
    }

    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getFriendLocalStatus() {
        return this.friendLocalStatus;
    }
    public void setFriendLocalStatus(int friendLocalStatus) {
        this.friendLocalStatus = friendLocalStatus;
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

    public String getFriendId() {
        return this.friendId;
    }
    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
