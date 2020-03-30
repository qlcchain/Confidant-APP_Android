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

    private String account;
    private String msgId;
    private String menu;
    private String subject;
    private String from;
    private String to;
    private String cc;//抄送
    private String bcc;//密送
    private String date;
    private Long timeStamp;//用于排序
    private boolean isSeen;
    private boolean isStar;
    private String priority;
    private boolean isReplySign;
    private long size;
    private boolean isContainerAttachment;
    private int attachmentCount;
    private String content;
    private String contentText;
    private String originalText;//如果有，说明是解密出来的，否则直接用content
    private String aesKey;
    private long messageTotalCount;
    private String emailAttachPath;
    private String userId;
    private Long sortId;//用于排序
    private String originalBody; //原始正文，用于解析不出问题
    
    public EmailMessageEntity() {

    }


    protected EmailMessageEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        account = in.readString();
        msgId = in.readString();
        menu = in.readString();
        subject = in.readString();
        from = in.readString();
        to = in.readString();
        cc = in.readString();
        bcc = in.readString();
        date = in.readString();
        if (in.readByte() == 0) {
            timeStamp = null;
        } else {
            timeStamp = in.readLong();
        }
        isSeen = in.readByte() != 0;
        isStar = in.readByte() != 0;
        priority = in.readString();
        isReplySign = in.readByte() != 0;
        size = in.readLong();
        isContainerAttachment = in.readByte() != 0;
        attachmentCount = in.readInt();
        content = in.readString();
        contentText = in.readString();
        originalText = in.readString();
        aesKey = in.readString();
        messageTotalCount = in.readLong();
        emailAttachPath = in.readString();
        userId = in.readString();
        if (in.readByte() == 0) {
            sortId = null;
        } else {
            sortId = in.readLong();
        }
        originalBody = in.readString();
    }


    @Generated(hash = 1086322148)
    public EmailMessageEntity(Long id, String account, String msgId, String menu, String subject,
            String from, String to, String cc, String bcc, String date, Long timeStamp, boolean isSeen,
            boolean isStar, String priority, boolean isReplySign, long size,
            boolean isContainerAttachment, int attachmentCount, String content, String contentText,
            String originalText, String aesKey, long messageTotalCount, String emailAttachPath,
            String userId, Long sortId, String originalBody) {
        this.id = id;
        this.account = account;
        this.msgId = msgId;
        this.menu = menu;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.date = date;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
        this.isStar = isStar;
        this.priority = priority;
        this.isReplySign = isReplySign;
        this.size = size;
        this.isContainerAttachment = isContainerAttachment;
        this.attachmentCount = attachmentCount;
        this.content = content;
        this.contentText = contentText;
        this.originalText = originalText;
        this.aesKey = aesKey;
        this.messageTotalCount = messageTotalCount;
        this.emailAttachPath = emailAttachPath;
        this.userId = userId;
        this.sortId = sortId;
        this.originalBody = originalBody;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
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

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setIsStar(boolean star) {
        isStar = star;
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

    public void setIsReplySign(boolean replySign) {
        isReplySign = replySign;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isContainerAttachment() {
        return isContainerAttachment;
    }

    public void setIsContainerAttachment(boolean containerAttachment) {
        isContainerAttachment = containerAttachment;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public long getMessageTotalCount() {
        return messageTotalCount;
    }

    public void setMessageTotalCount(long messageTotalCount) {
        this.messageTotalCount = messageTotalCount;
    }

    public String getEmailAttachPath() {
        return emailAttachPath;
    }

    public void setEmailAttachPath(String emailAttachPath) {
        this.emailAttachPath = emailAttachPath;
    }

    public boolean getIsSeen() {
        return this.isSeen;
    }

    public boolean getIsStar() {
        return this.isStar;
    }

    public boolean getIsReplySign() {
        return this.isReplySign;
    }

    public boolean getIsContainerAttachment() {
        return this.isContainerAttachment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public String getOriginalBody() {
        return originalBody;
    }

    public void setOriginalBody(String originalBody) {
        this.originalBody = originalBody;
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
        dest.writeString(msgId);
        dest.writeString(menu);
        dest.writeString(subject);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(cc);
        dest.writeString(bcc);
        dest.writeString(date);
        if (timeStamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timeStamp);
        }
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isStar ? 1 : 0));
        dest.writeString(priority);
        dest.writeByte((byte) (isReplySign ? 1 : 0));
        dest.writeLong(size);
        dest.writeByte((byte) (isContainerAttachment ? 1 : 0));
        dest.writeInt(attachmentCount);
        dest.writeString(content);
        dest.writeString(contentText);
        dest.writeString(originalText);
        dest.writeString(aesKey);
        dest.writeLong(messageTotalCount);
        dest.writeString(emailAttachPath);
        dest.writeString(userId);
        if (sortId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sortId);
        }
        dest.writeString(originalBody);
    }
}
