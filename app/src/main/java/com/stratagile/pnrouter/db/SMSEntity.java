package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SMSEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;

    private Integer smsId;
    // 电话
    private String address;
    // 联系人位置
    private Integer person;
    // 时间
    private Long date;
    // 是否阅读
    private Integer read;
    // 消息类型
    private Integer type;
    // 主题
    private String subject;
    // 内容
    private String body;
    // 短信服务号
    private String service_center;
    private boolean lastCheck; //最后一次选中
    private boolean isUpload; //是否上传到节点
    //多选模式中是否选中
    private boolean isMultChecked;

    public SMSEntity() {
    }

    protected SMSEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            smsId = null;
        } else {
            smsId = in.readInt();
        }
        address = in.readString();
        if (in.readByte() == 0) {
            person = null;
        } else {
            person = in.readInt();
        }
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        if (in.readByte() == 0) {
            read = null;
        } else {
            read = in.readInt();
        }
        if (in.readByte() == 0) {
            type = null;
        } else {
            type = in.readInt();
        }
        subject = in.readString();
        body = in.readString();
        service_center = in.readString();
        lastCheck = in.readByte() != 0;
        isUpload = in.readByte() != 0;
        isMultChecked = in.readByte() != 0;
    }

    @Generated(hash = 1441031214)
    public SMSEntity(Long id, Integer smsId, String address, Integer person,
            Long date, Integer read, Integer type, String subject, String body,
            String service_center, boolean lastCheck, boolean isUpload,
            boolean isMultChecked) {
        this.id = id;
        this.smsId = smsId;
        this.address = address;
        this.person = person;
        this.date = date;
        this.read = read;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.service_center = service_center;
        this.lastCheck = lastCheck;
        this.isUpload = isUpload;
        this.isMultChecked = isMultChecked;
    }

    public static final Creator<SMSEntity> CREATOR = new Creator<SMSEntity>() {
        @Override
        public SMSEntity createFromParcel(Parcel in) {
            return new SMSEntity(in);
        }

        @Override
        public SMSEntity[] newArray(int size) {
            return new SMSEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSmsId() {
        return smsId;
    }

    public void setSmsId(Integer smsId) {
        this.smsId = smsId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPerson() {
        return person;
    }

    public void setPerson(Integer person) {
        this.person = person;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getService_center() {
        return service_center;
    }

    public void setService_center(String service_center) {
        this.service_center = service_center;
    }

    public boolean isLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(boolean lastCheck) {
        this.lastCheck = lastCheck;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public boolean isMultChecked() {
        return isMultChecked;
    }

    public void setMultChecked(boolean multChecked) {
        isMultChecked = multChecked;
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
        if (smsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(smsId);
        }
        dest.writeString(address);
        if (person == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(person);
        }
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        if (read == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(read);
        }
        if (type == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(type);
        }
        dest.writeString(subject);
        dest.writeString(body);
        dest.writeString(service_center);
        dest.writeByte((byte) (lastCheck ? 1 : 0));
        dest.writeByte((byte) (isUpload ? 1 : 0));
        dest.writeByte((byte) (isMultChecked ? 1 : 0));
    }

    public boolean getLastCheck() {
        return this.lastCheck;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public boolean getIsMultChecked() {
        return this.isMultChecked;
    }

    public void setIsMultChecked(boolean isMultChecked) {
        this.isMultChecked = isMultChecked;
    }
}
