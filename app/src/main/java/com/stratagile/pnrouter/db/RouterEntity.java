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
    private String username;
    private String userId;
    private String routerName;

    private boolean lastCheck;

    @Generated(hash = 1871779795)
    public RouterEntity(Long id, String routerId, String username, String userId,
            String routerName, boolean lastCheck) {
        this.id = id;
        this.routerId = routerId;
        this.username = username;
        this.userId = userId;
        this.routerName = routerName;
        this.lastCheck = lastCheck;
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
        username = in.readString();
        userId = in.readString();
        routerName = in.readString();
        lastCheck = in.readByte() != 0;
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
        parcel.writeString(routerId);
        parcel.writeString(username);
        parcel.writeString(userId);
        parcel.writeString(routerName);
        parcel.writeByte((byte) (lastCheck ? 1 : 0));
    }
}
