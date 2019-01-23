package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RouterEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;

    private String routerId;
    private String userSn;
    private String username;
    private String userId;
    private String index;
    private String routerName;
    private Integer dataFileVersion;
    private String dataFilePay;
    private boolean lastCheck; //最后一次选中
    private String loginKey;



    @Generated(hash = 517680001)
    public RouterEntity(Long id, String routerId, String userSn, String username,
            String userId, String index, String routerName, Integer dataFileVersion,
            String dataFilePay, boolean lastCheck, String loginKey) {
        this.id = id;
        this.routerId = routerId;
        this.userSn = userSn;
        this.username = username;
        this.userId = userId;
        this.index = index;
        this.routerName = routerName;
        this.dataFileVersion = dataFileVersion;
        this.dataFilePay = dataFilePay;
        this.lastCheck = lastCheck;
        this.loginKey = loginKey;
    }

    @Generated(hash = 997370902)
    public RouterEntity() {
    }


    protected RouterEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        routerId = in.readString();
        userSn = in.readString();
        username = in.readString();
        userId = in.readString();
        index = in.readString();
        routerName = in.readString();
        if (in.readByte() == 0) {
            dataFileVersion = null;
        } else {
            dataFileVersion = in.readInt();
        }
        dataFilePay = in.readString();
        lastCheck = in.readByte() != 0;
        loginKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(routerId);
        dest.writeString(userSn);
        dest.writeString(username);
        dest.writeString(userId);
        dest.writeString(index);
        dest.writeString(routerName);
        if (dataFileVersion == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dataFileVersion);
        }
        dest.writeString(dataFilePay);
        dest.writeByte((byte) (lastCheck ? 1 : 0));
        dest.writeString(loginKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RouterEntity> CREATOR = new Creator<RouterEntity>() {
        @Override
        public RouterEntity createFromParcel(Parcel in) {
            return new RouterEntity(in);
        }

        @Override
        public RouterEntity[] newArray(int size) {
            return new RouterEntity[size];
        }
    };

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouterId() {
        return this.routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouterName() {
        return this.routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public boolean getLastCheck() {
        return this.lastCheck;
    }

    public void setLastCheck(boolean lastCheck) {
        this.lastCheck = lastCheck;
    }
    public String getUserSn() {
        return userSn;
    }

    public void setUserSn(String userSn) {
        this.userSn = userSn;
    }

    public Integer getDataFileVersion() {
        return dataFileVersion;
    }

    public void setDataFileVersion(Integer dataFileVersion) {
        this.dataFileVersion = dataFileVersion;
    }

    public String getDataFilePay() {
        return dataFilePay;
    }

    public void setDataFilePay(String dataFilePay) {
        this.dataFilePay = dataFilePay;
    }
    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

}
