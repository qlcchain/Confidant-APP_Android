package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class EmailMessageEntity implements Parcelable{


    @Id(autoincrement = true)
    private Long id;

    private String subject;
    private String from;
    private String to;
    private String date;
    private boolean isSeen;
    private String priority;
    private boolean isReplySign;
    private long size;
    private boolean isContainerAttachment;
    private int attachmentCount;
    private String content;
    private String contentText;

    public EmailMessageEntity() {

    }


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
        isSeen = in.readByte() != 0;
        priority = in.readString();
        isReplySign = in.readByte() != 0;
        size = in.readLong();
        isContainerAttachment = in.readByte() != 0;
        attachmentCount = in.readInt();
        content = in.readString();
        contentText = in.readString();
    }


    @Generated(hash = 1401177952)
    public EmailMessageEntity(Long id, String subject, String from, String to, String date,
            boolean isSeen, String priority, boolean isReplySign, long size,
            boolean isContainerAttachment, int attachmentCount, String content, String contentText) {
        this.id = id;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.date = date;
        this.isSeen = isSeen;
        this.priority = priority;
        this.isReplySign = isReplySign;
        this.size = size;
        this.isContainerAttachment = isContainerAttachment;
        this.attachmentCount = attachmentCount;
        this.content = content;
        this.contentText = contentText;
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

    public boolean isContainerAttachment() {
        return isContainerAttachment;
    }

    public void setContainerAttachment(boolean containerAttachment) {
        isContainerAttachment = containerAttachment;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isReplySign() {
        return isReplySign;
    }

    public void setReplySign(boolean replySign) {
        isReplySign = replySign;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }



    public boolean getIsSeen() {
        return this.isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public boolean getIsReplySign() {
        return this.isReplySign;
    }

    public void setIsReplySign(boolean isReplySign) {
        this.isReplySign = isReplySign;
    }

    public boolean getIsContainerAttachment() {
        return this.isContainerAttachment;
    }

    public void setIsContainerAttachment(boolean isContainerAttachment) {
        this.isContainerAttachment = isContainerAttachment;
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
        dest.writeString(subject);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(date);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeString(priority);
        dest.writeByte((byte) (isReplySign ? 1 : 0));
        dest.writeLong(size);
        dest.writeByte((byte) (isContainerAttachment ? 1 : 0));
        dest.writeInt(attachmentCount);
        dest.writeString(content);
        dest.writeString(contentText);
    }
}
