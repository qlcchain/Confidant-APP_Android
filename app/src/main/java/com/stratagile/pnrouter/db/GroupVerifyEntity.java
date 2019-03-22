package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


/**
 * 群验证实体类。
 */
@Entity
public class GroupVerifyEntity implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private String From;
    private String To;
    private String Aduit;
    private String GId;
    private String UserPubKey;
    //自己的userId
    private String userId;

    private String UserGroupKey;
    private String FromName;
    private String ToName;
    private String Gname;
    //审核状态
    //状态， 0 通过， 1 等待我同意， 2 我拒绝 3, 被移除群聊， 4 群解散
    private int verifyType;


    @Generated(hash = 957715906)
    public GroupVerifyEntity(Long id, String From, String To, String Aduit, String GId,
            String UserPubKey, String userId, String UserGroupKey, String FromName, String ToName,
            String Gname, int verifyType) {
        this.id = id;
        this.From = From;
        this.To = To;
        this.Aduit = Aduit;
        this.GId = GId;
        this.UserPubKey = UserPubKey;
        this.userId = userId;
        this.UserGroupKey = UserGroupKey;
        this.FromName = FromName;
        this.ToName = ToName;
        this.Gname = Gname;
        this.verifyType = verifyType;
    }

    @Generated(hash = 1232350434)
    public GroupVerifyEntity() {
    }


    protected GroupVerifyEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        From = in.readString();
        To = in.readString();
        Aduit = in.readString();
        GId = in.readString();
        UserPubKey = in.readString();
        userId = in.readString();
        UserGroupKey = in.readString();
        FromName = in.readString();
        ToName = in.readString();
        Gname = in.readString();
        verifyType = in.readInt();
    }

    public static final Creator<GroupVerifyEntity> CREATOR = new Creator<GroupVerifyEntity>() {
        @Override
        public GroupVerifyEntity createFromParcel(Parcel in) {
            return new GroupVerifyEntity(in);
        }

        @Override
        public GroupVerifyEntity[] newArray(int size) {
            return new GroupVerifyEntity[size];
        }
    };

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return this.From;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public String getTo() {
        return this.To;
    }

    public void setTo(String To) {
        this.To = To;
    }

    public String getAduit() {
        return this.Aduit;
    }

    public void setAduit(String Aduit) {
        this.Aduit = Aduit;
    }

    public String getGId() {
        return this.GId;
    }

    public void setGId(String GId) {
        this.GId = GId;
    }

    public String getUserPubKey() {
        return this.UserPubKey;
    }

    public void setUserPubKey(String UserPubKey) {
        this.UserPubKey = UserPubKey;
    }

    public String getUserGroupKey() {
        return this.UserGroupKey;
    }

    public void setUserGroupKey(String UserGroupKey) {
        this.UserGroupKey = UserGroupKey;
    }

    public String getFromName() {
        return this.FromName;
    }

    public void setFromName(String FromName) {
        this.FromName = FromName;
    }

    public String getToName() {
        return this.ToName;
    }

    public void setToName(String ToName) {
        this.ToName = ToName;
    }

    public String getGname() {
        return this.Gname;
    }

    public void setGname(String Gname) {
        this.Gname = Gname;
    }


    public int getVerifyType() {
        return this.verifyType;
    }

    public void setVerifyType(int verifyType) {
        this.verifyType = verifyType;
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
        parcel.writeString(From);
        parcel.writeString(To);
        parcel.writeString(Aduit);
        parcel.writeString(GId);
        parcel.writeString(UserPubKey);
        parcel.writeString(userId);
        parcel.writeString(UserGroupKey);
        parcel.writeString(FromName);
        parcel.writeString(ToName);
        parcel.writeString(Gname);
        parcel.writeInt(verifyType);
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
