package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailContactsEntity implements Parcelable{


    @Id(autoincrement = true)
    private Long id;

    private String account;
    private String name;
    private boolean choose;
    private long createTime;
    private boolean recentFlag = false;


    public EmailContactsEntity() {

    }

    protected EmailContactsEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        account = in.readString();
        name = in.readString();
        choose = in.readByte() != 0;
        createTime = in.readLong();
        recentFlag = in.readByte() != 0;
    }

    @Generated(hash = 1093262090)
    public EmailContactsEntity(Long id, String account, String name, boolean choose, long createTime,
            boolean recentFlag) {
        this.id = id;
        this.account = account;
        this.name = name;
        this.choose = choose;
        this.createTime = createTime;
        this.recentFlag = recentFlag;
    }

    public static final Creator<EmailContactsEntity> CREATOR = new Creator<EmailContactsEntity>() {
        @Override
        public EmailContactsEntity createFromParcel(Parcel in) {
            return new EmailContactsEntity(in);
        }

        @Override
        public EmailContactsEntity[] newArray(int size) {
            return new EmailContactsEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isRecentFlag() {
        return recentFlag;
    }

    public void setRecentFlag(boolean recentFlag) {
        this.recentFlag = recentFlag;
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
        dest.writeString(account);
        dest.writeString(name);
        dest.writeByte((byte) (choose ? 1 : 0));
        dest.writeLong(createTime);
        dest.writeByte((byte) (recentFlag ? 1 : 0));
    }

    public boolean getChoose() {
        return this.choose;
    }

    public boolean getRecentFlag() {
        return this.recentFlag;
    }
}
