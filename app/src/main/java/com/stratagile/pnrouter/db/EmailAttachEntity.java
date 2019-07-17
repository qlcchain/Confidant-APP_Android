package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailAttachEntity implements Parcelable{


    @Id(autoincrement = true)
    private Long id;
    private String msgId;
    private String account;
    private String name;
    private String localPath;
    private byte[] data;

    public EmailAttachEntity() {

    }

    protected EmailAttachEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        msgId = in.readString();
        account = in.readString();
        name = in.readString();
        localPath = in.readString();
        data = in.createByteArray();
    }

    @Generated(hash = 269789248)
    public EmailAttachEntity(Long id, String msgId, String account, String name, String localPath,
            byte[] data) {
        this.id = id;
        this.msgId = msgId;
        this.account = account;
        this.name = name;
        this.localPath = localPath;
        this.data = data;
    }

    public static final Creator<EmailAttachEntity> CREATOR = new Creator<EmailAttachEntity>() {
        @Override
        public EmailAttachEntity createFromParcel(Parcel in) {
            return new EmailAttachEntity(in);
        }

        @Override
        public EmailAttachEntity[] newArray(int size) {
            return new EmailAttachEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
        dest.writeString(msgId);
        dest.writeString(account);
        dest.writeString(name);
        dest.writeString(localPath);
        dest.writeByteArray(data);
    }
}
