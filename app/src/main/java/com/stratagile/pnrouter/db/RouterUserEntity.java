package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class RouterUserEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    private String UserSN;
    private int UserType;
    private int Active;
    private String IdentifyCode;
    private String Mnemonic;
    private String NickName;
    private String UserId;
    private int LastLoginTime;
    private String Qrcode;
    //昵称
    private String nickSouceName;



    @Generated(hash = 1938675895)
    public RouterUserEntity(Long id, String UserSN, int UserType, int Active,
            String IdentifyCode, String Mnemonic, String NickName, String UserId,
            int LastLoginTime, String Qrcode, String nickSouceName) {
        this.id = id;
        this.UserSN = UserSN;
        this.UserType = UserType;
        this.Active = Active;
        this.IdentifyCode = IdentifyCode;
        this.Mnemonic = Mnemonic;
        this.NickName = NickName;
        this.UserId = UserId;
        this.LastLoginTime = LastLoginTime;
        this.Qrcode = Qrcode;
        this.nickSouceName = nickSouceName;
    }

    @Generated(hash = 893520339)
    public RouterUserEntity() {
    }


    protected RouterUserEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        UserSN = in.readString();
        UserType = in.readInt();
        Active = in.readInt();
        IdentifyCode = in.readString();
        Mnemonic = in.readString();
        NickName = in.readString();
        UserId = in.readString();
        LastLoginTime = in.readInt();
        Qrcode = in.readString();
        nickSouceName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(UserSN);
        dest.writeInt(UserType);
        dest.writeInt(Active);
        dest.writeString(IdentifyCode);
        dest.writeString(Mnemonic);
        dest.writeString(NickName);
        dest.writeString(UserId);
        dest.writeInt(LastLoginTime);
        dest.writeString(Qrcode);
        dest.writeString(nickSouceName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RouterUserEntity> CREATOR = new Creator<RouterUserEntity>() {
        @Override
        public RouterUserEntity createFromParcel(Parcel in) {
            return new RouterUserEntity(in);
        }

        @Override
        public RouterUserEntity[] newArray(int size) {
            return new RouterUserEntity[size];
        }
    };

    public String getUserSN() {
        return UserSN;
    }

    public void setUserSN(String UserSN) {
        this.UserSN = UserSN;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int UserType) {
        this.UserType = UserType;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int Active) {
        this.Active = Active;
    }

    public String getIdentifyCode() {
        return IdentifyCode;
    }

    public void setIdentifyCode(String IdentifyCode) {
        this.IdentifyCode = IdentifyCode;
    }

    public String getMnemonic() {
        return Mnemonic;
    }

    public void setMnemonic(String Mnemonic) {
        this.Mnemonic = Mnemonic;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public int getLastLoginTime() {
        return LastLoginTime;
    }

    public void setLastLoginTime(int LastLoginTime) {
        this.LastLoginTime = LastLoginTime;
    }

    public String getQrcode() {
        return Qrcode;
    }

    public void setQrcode(String Qrcode) {
        this.Qrcode = Qrcode;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickSouceName() {
        return this.nickSouceName;
    }

    public void setNickSouceName(String nickSouceName) {
        this.nickSouceName = nickSouceName;
    }


}
