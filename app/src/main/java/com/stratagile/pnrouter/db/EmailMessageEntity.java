package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailMessageEntity implements Parcelable{
    protected EmailMessageEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        subject = in.readString();
        from = in.readString();
        to = in.readString();
        date = in.readString();
        content = in.readString();
    }

    public static final Creator<EmailMessageEntity> CREATOR = new Creator<EmailMessageEntity>() {
        @Override
        public EmailMessageEntity createFromParcel(Parcel in) {
            return new EmailMessageEntity(in);
        }

        @Override
        public EmailMessageEntity[] newArray(int size) {
            return new EmailMessageEntity[size];
        }
    };

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
        dest.writeString(subject);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(date);
        dest.writeString(content);
    }

    @Id(autoincrement = true)
    private Long id;

    private String subject;
    private String from;
    private String to;
    private String date;
    private String content;

    @Generated(hash = 1558263807)
    public EmailMessageEntity(Long id, String subject, String from, String to,
            String date, String content) {
        this.id = id;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.date = date;
        this.content = content;
    }

    @Generated(hash = 1190827118)
    public EmailMessageEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
