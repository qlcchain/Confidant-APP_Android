package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LocalFileMenu implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private Long creatTime;
    private String fileName;
    private String path;
    private Long fileNum;

    public LocalFileMenu() {

    }

    protected LocalFileMenu(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            creatTime = null;
        } else {
            creatTime = in.readLong();
        }
        fileName = in.readString();
        path = in.readString();
        if (in.readByte() == 0) {
            fileNum = null;
        } else {
            fileNum = in.readLong();
        }
    }

    public static final Creator<LocalFileMenu> CREATOR = new Creator<LocalFileMenu>() {
        @Override
        public LocalFileMenu createFromParcel(Parcel in) {
            return new LocalFileMenu(in);
        }

        @Override
        public LocalFileMenu[] newArray(int size) {
            return new LocalFileMenu[size];
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
        if (creatTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(creatTime);
        }
        dest.writeString(fileName);
        dest.writeString(path);
        if (fileNum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(fileNum);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getFileNum() {
        return fileNum;
    }

    public void setFileNum(Long fileNum) {
        this.fileNum = fileNum;
    }
}
