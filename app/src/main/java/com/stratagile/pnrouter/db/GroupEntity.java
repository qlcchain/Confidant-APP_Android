package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;

@Entity
public class GroupEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    private String GId;
    private String GName;
    private String GAdmin;
    private String Remark;
    private String UserKey;
    private int Verify;



    @Generated(hash = 983277825)
    public GroupEntity(Long id, String GId, String GName, String GAdmin,
            String Remark, String UserKey, int Verify) {
        this.id = id;
        this.GId = GId;
        this.GName = GName;
        this.GAdmin = GAdmin;
        this.Remark = Remark;
        this.UserKey = UserKey;
        this.Verify = Verify;
    }

    @Generated(hash = 954040478)
    public GroupEntity() {
    }


    protected GroupEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        GId = in.readString();
        GName = in.readString();
        GAdmin = in.readString();
        Remark = in.readString();
        UserKey = in.readString();
        Verify = in.readInt();
    }

    public static final Creator<GroupEntity> CREATOR = new Creator<GroupEntity>() {
        @Override
        public GroupEntity createFromParcel(Parcel in) {
            return new GroupEntity(in);
        }

        @Override
        public GroupEntity[] newArray(int size) {
            return new GroupEntity[size];
        }
    };

    public String getGId() {
        return GId;
    }

    public void setGId(String GId) {
        this.GId = GId;
    }

    public String getGName() {
        return GName;
    }

    public void setGName(String GName) {
        this.GName = GName;
    }

    public String getGAdmin() {
        return GAdmin;
    }

    public void setGAdmin(String GAdmin) {
        this.GAdmin = GAdmin;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String UserKey) {
        this.UserKey = UserKey;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
        parcel.writeString(GId);
        parcel.writeString(GName);
        parcel.writeString(GAdmin);
        parcel.writeString(Remark);
        parcel.writeString(UserKey);
        parcel.writeInt(Verify);
    }

    public int getVerify() {
        return this.Verify;
    }

    public void setVerify(int Verify) {
        this.Verify = Verify;
    }
}

