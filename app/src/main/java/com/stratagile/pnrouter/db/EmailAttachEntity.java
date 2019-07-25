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
    private boolean canDelete;
    private boolean hasData;
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
        canDelete = in.readByte() != 0;
        hasData = in.readByte() != 0;
        data = in.createByteArray();
    }

    @Generated(hash = 448571763)
    public EmailAttachEntity(Long id, String msgId, String account, String name, String localPath,
            boolean canDelete, boolean hasData, byte[] data) {
        this.id = id;
        this.msgId = msgId;
        this.account = account;
        this.name = name;
        this.localPath = localPath;
        this.canDelete = canDelete;
        this.hasData = hasData;
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

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
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
        dest.writeByte((byte) (canDelete ? 1 : 0));
        dest.writeByte((byte) (hasData ? 1 : 0));
        dest.writeByteArray(data);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getCanDelete() {
        return this.canDelete;
    }

    public boolean getHasData() {
        return this.hasData;
    }
}
