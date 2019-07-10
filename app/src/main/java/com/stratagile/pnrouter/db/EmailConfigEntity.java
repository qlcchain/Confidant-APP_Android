package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.SpUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailConfigEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;

    private int smtpPort;           //SMTP端口
    private int popPort;            //POP端口
    private int imapPort;           //IMAP端口
    private String smtpHost;        //SMTP的Host
    private String popHost;         //POP的Host
    private String imapHost;        //IMAP的Host
    private String account;         //邮箱帐号
    private String password;        //邮箱密码


    @Generated(hash = 219903674)
    public EmailConfigEntity(Long id, int smtpPort, int popPort, int imapPort,
            String smtpHost, String popHost, String imapHost, String account,
            String password) {
        this.id = id;
        this.smtpPort = smtpPort;
        this.popPort = popPort;
        this.imapPort = imapPort;
        this.smtpHost = smtpHost;
        this.popHost = popHost;
        this.imapHost = imapHost;
        this.account = account;
        this.password = password;
    }

    @Generated(hash = 819609725)
    public EmailConfigEntity() {
    }


    protected EmailConfigEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        smtpPort = in.readInt();
        popPort = in.readInt();
        imapPort = in.readInt();
        smtpHost = in.readString();
        popHost = in.readString();
        imapHost = in.readString();
        account = in.readString();
        password = in.readString();
    }

    public static final Creator<EmailConfigEntity> CREATOR = new Creator<EmailConfigEntity>() {
        @Override
        public EmailConfigEntity createFromParcel(Parcel in) {
            return new EmailConfigEntity(in);
        }

        @Override
        public EmailConfigEntity[] newArray(int size) {
            return new EmailConfigEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public int getPopPort() {
        return popPort;
    }

    public void setPopPort(int popPort) {
        this.popPort = popPort;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getPopHost() {
        return popHost;
    }

    public void setPopHost(String popHost) {
        this.popHost = popHost;
    }

    public String getImapHost() {
        return imapHost;
    }

    public void setImapHost(String imapHost) {
        this.imapHost = imapHost;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        dest.writeInt(smtpPort);
        dest.writeInt(popPort);
        dest.writeInt(imapPort);
        dest.writeString(smtpHost);
        dest.writeString(popHost);
        dest.writeString(imapHost);
        dest.writeString(account);
        dest.writeString(password);
    }
}
