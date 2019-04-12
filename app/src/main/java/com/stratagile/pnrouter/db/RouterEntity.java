package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.SpUtil;

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
    private String routerAlias;
    private Integer dataFileVersion;
    private String dataFilePay;
    private boolean lastCheck; //最后一次选中
    private String loginKey;
    //多选模式中是否选中
    private boolean isMultChecked;

    private String AdminId;
    private String AdminName;
    private String AdminKey;







    @Generated(hash = 2120409963)
    public RouterEntity(Long id, String routerId, String userSn, String username, String userId, String index,
            String routerName, String routerAlias, Integer dataFileVersion, String dataFilePay, boolean lastCheck,
            String loginKey, boolean isMultChecked, String AdminId, String AdminName, String AdminKey) {
        this.id = id;
        this.routerId = routerId;
        this.userSn = userSn;
        this.username = username;
        this.userId = userId;
        this.index = index;
        this.routerName = routerName;
        this.routerAlias = routerAlias;
        this.dataFileVersion = dataFileVersion;
        this.dataFilePay = dataFilePay;
        this.lastCheck = lastCheck;
        this.loginKey = loginKey;
        this.isMultChecked = isMultChecked;
        this.AdminId = AdminId;
        this.AdminName = AdminName;
        this.AdminKey = AdminKey;
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
        routerAlias = in.readString();
        if (in.readByte() == 0) {
            dataFileVersion = null;
        } else {
            dataFileVersion = in.readInt();
        }
        dataFilePay = in.readString();
        lastCheck = in.readByte() != 0;
        loginKey = in.readString();
        isMultChecked = in.readByte() != 0;
        AdminId = in.readString();
        AdminName = in.readString();
        AdminKey = in.readString();
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
        dest.writeString(routerAlias);
        if (dataFileVersion == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dataFileVersion);
        }
        dest.writeString(dataFilePay);
        dest.writeByte((byte) (lastCheck ? 1 : 0));
        dest.writeString(loginKey);
        dest.writeByte((byte) (isMultChecked ? 1 : 0));
        dest.writeString(AdminId);
        dest.writeString(AdminName);
        dest.writeString(AdminKey);
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
        String name = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUsername(), "");
        return name;
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
        if(dataFileVersion == null)
        {
            return 0;
        }
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

    public String getRouterAlias() {
        return this.routerAlias;
    }

    public void setRouterAlias(String routerAlias) {
        this.routerAlias = routerAlias;
    }



    public boolean getIsMultChecked() {
        return this.isMultChecked;
    }

    public void setIsMultChecked(boolean isMultChecked) {
        this.isMultChecked = isMultChecked;
    }

    public String getAdminId() {
        return AdminId;
    }

    public void setAdminId(String adminId) {
        AdminId = adminId;
    }

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    public String getAdminKey() {
        return AdminKey;
    }

    public void setAdminKey(String adminKey) {
        AdminKey = adminKey;
    }
}
