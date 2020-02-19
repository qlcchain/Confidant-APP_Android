package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class QLCAccount implements Parcelable {
    @Id(autoincrement = true)

    private Long id;

    private String seed;
    private String password;
    private String pubKey;
    private String privKey;
    private String address;
    private Boolean isCurrent;
    private String accountName;
    //是否为主账号的种子
    private Boolean isAccountSeed;
    private Integer walletIndex;
    private String mnemonic;
    private Boolean isBackUp;

    public QLCAccount() {
    }

    protected QLCAccount(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        seed = in.readString();
        password = in.readString();
        pubKey = in.readString();
        privKey = in.readString();
        address = in.readString();
        byte tmpIsCurrent = in.readByte();
        isCurrent = tmpIsCurrent == 0 ? null : tmpIsCurrent == 1;
        accountName = in.readString();
        byte tmpIsAccountSeed = in.readByte();
        isAccountSeed = tmpIsAccountSeed == 0 ? null : tmpIsAccountSeed == 1;
        if (in.readByte() == 0) {
            walletIndex = null;
        } else {
            walletIndex = in.readInt();
        }
        mnemonic = in.readString();
        byte tmpIsBackUp = in.readByte();
        isBackUp = tmpIsBackUp == 0 ? null : tmpIsBackUp == 1;
    }

    @Generated(hash = 659211814)
    public QLCAccount(Long id, String seed, String password, String pubKey,
            String privKey, String address, Boolean isCurrent, String accountName,
            Boolean isAccountSeed, Integer walletIndex, String mnemonic,
            Boolean isBackUp) {
        this.id = id;
        this.seed = seed;
        this.password = password;
        this.pubKey = pubKey;
        this.privKey = privKey;
        this.address = address;
        this.isCurrent = isCurrent;
        this.accountName = accountName;
        this.isAccountSeed = isAccountSeed;
        this.walletIndex = walletIndex;
        this.mnemonic = mnemonic;
        this.isBackUp = isBackUp;
    }

    public static final Creator<QLCAccount> CREATOR = new Creator<QLCAccount>() {
        @Override
        public QLCAccount createFromParcel(Parcel in) {
            return new QLCAccount(in);
        }

        @Override
        public QLCAccount[] newArray(int size) {
            return new QLCAccount[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Boolean getAccountSeed() {
        return isAccountSeed;
    }

    public void setAccountSeed(Boolean accountSeed) {
        isAccountSeed = accountSeed;
    }

    public Integer getWalletIndex() {
        return walletIndex;
    }

    public void setWalletIndex(Integer walletIndex) {
        this.walletIndex = walletIndex;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public Boolean getBackUp() {
        return isBackUp;
    }

    public void setBackUp(Boolean backUp) {
        isBackUp = backUp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(seed);
        dest.writeString(password);
        dest.writeString(pubKey);
        dest.writeString(privKey);
        dest.writeString(address);
        dest.writeByte((byte) (isCurrent == null ? 0 : isCurrent ? 1 : 2));
        dest.writeString(accountName);
        dest.writeByte((byte) (isAccountSeed == null ? 0 : isAccountSeed ? 1 : 2));
        if (walletIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(walletIndex);
        }
        dest.writeString(mnemonic);
        dest.writeByte((byte) (isBackUp == null ? 0 : isBackUp ? 1 : 2));
    }

    public Boolean getIsCurrent() {
        return this.isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Boolean getIsAccountSeed() {
        return this.isAccountSeed;
    }

    public void setIsAccountSeed(Boolean isAccountSeed) {
        this.isAccountSeed = isAccountSeed;
    }

    public Boolean getIsBackUp() {
        return this.isBackUp;
    }

    public void setIsBackUp(Boolean isBackUp) {
        this.isBackUp = isBackUp;
    }
}
