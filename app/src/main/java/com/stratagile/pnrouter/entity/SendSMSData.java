package com.stratagile.pnrouter.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class SendSMSData implements Parcelable {

    /**
     * Index : 1
     * Tel : MTgwNzUxODYyNTE=
     * Num : 1
     * Uid : 0
     * Time : 1580806685247
     * Read : 0
     * Send : 0
     * User :
     * Title :
     * Cont : ePF7KkJR1/HDK97iZqFQiA==
     * Key : ePF7KkJR1/HDK97iZqFQiA==
     */
    private Integer Index;

    private Integer Id;
    // 电话
    private String Tel;
    private Integer Num;
    // 联系人
    private String User;
    // 联系人位置
    private Integer Uid;
    // 时间
    private Long Time;
    // 是否阅读
    private Integer Read;
    // 消息类型
    private Integer Send;
    // 主题
    private String Title;
    // 内容
    private String Cont;
    // 加密
    private String Key;

    private boolean lastCheck; //最后一次选中

    //多选模式中是否选中
    private boolean isMultChecked;//是否开启编辑

    public SendSMSData() {
    }


    protected SendSMSData(Parcel in) {
        if (in.readByte() == 0) {
            Index = null;
        } else {
            Index = in.readInt();
        }
        if (in.readByte() == 0) {
            Id = null;
        } else {
            Id = in.readInt();
        }
        Tel = in.readString();
        if (in.readByte() == 0) {
            Num = null;
        } else {
            Num = in.readInt();
        }
        User = in.readString();
        if (in.readByte() == 0) {
            Uid = null;
        } else {
            Uid = in.readInt();
        }
        if (in.readByte() == 0) {
            Time = null;
        } else {
            Time = in.readLong();
        }
        if (in.readByte() == 0) {
            Read = null;
        } else {
            Read = in.readInt();
        }
        if (in.readByte() == 0) {
            Send = null;
        } else {
            Send = in.readInt();
        }
        Title = in.readString();
        Cont = in.readString();
        Key = in.readString();
        lastCheck = in.readByte() != 0;
        isMultChecked = in.readByte() != 0;
    }

    public static final Creator<SendSMSData> CREATOR = new Creator<SendSMSData>() {
        @Override
        public SendSMSData createFromParcel(Parcel in) {
            return new SendSMSData(in);
        }

        @Override
        public SendSMSData[] newArray(int size) {
            return new SendSMSData[size];
        }
    };

    public Integer getIndex() {
        return Index;
    }

    public void setIndex(Integer index) {
        Index = index;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public Integer getNum() {
        return Num;
    }

    public void setNum(Integer num) {
        Num = num;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Integer getUid() {
        return Uid;
    }

    public void setUid(Integer uid) {
        Uid = uid;
    }

    public Long getTime() {
        return Time;
    }

    public void setTime(Long time) {
        Time = time;
    }

    public Integer getRead() {
        return Read;
    }

    public void setRead(Integer read) {
        Read = read;
    }

    public Integer getSend() {
        return Send;
    }

    public void setSend(Integer send) {
        Send = send;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCont() {
        return Cont;
    }

    public void setCont(String cont) {
        Cont = cont;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public boolean isLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(boolean lastCheck) {
        this.lastCheck = lastCheck;
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
        if (Index == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Index);
        }
        if (Id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Id);
        }
        dest.writeString(Tel);
        if (Num == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Num);
        }
        dest.writeString(User);
        if (Uid == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Uid);
        }
        if (Time == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Time);
        }
        if (Read == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Read);
        }
        if (Send == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Send);
        }
        dest.writeString(Title);
        dest.writeString(Cont);
        dest.writeString(Key);
        dest.writeByte((byte) (lastCheck ? 1 : 0));
        dest.writeByte((byte) (isMultChecked ? 1 : 0));
    }
}
