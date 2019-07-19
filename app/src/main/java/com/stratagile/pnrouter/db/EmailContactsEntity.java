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
    }

    @Generated(hash = 151066049)
    public EmailContactsEntity(Long id, String account, String name) {
        this.id = id;
        this.account = account;
        this.name = name;
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
    }
}
