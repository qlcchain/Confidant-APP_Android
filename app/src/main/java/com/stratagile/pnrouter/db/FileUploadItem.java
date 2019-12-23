package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class FileUploadItem implements Parcelable {
    @Id(autoincrement = true)
    private Long id;

    private Integer depens;
    private String UserId;
    private Integer type;
    private String fileId;
    private Long size;
    private String md5;

    private String fName;
    private String fKey;
    private String fInfo;

    private Integer pathId;
    private String pathName;

    public FileUploadItem() {
    }


    protected FileUploadItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            depens = null;
        } else {
            depens = in.readInt();
        }
        UserId = in.readString();
        if (in.readByte() == 0) {
            type = null;
        } else {
            type = in.readInt();
        }
        fileId = in.readString();
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readLong();
        }
        md5 = in.readString();
        fName = in.readString();
        fKey = in.readString();
        fInfo = in.readString();
        if (in.readByte() == 0) {
            pathId = null;
        } else {
            pathId = in.readInt();
        }
        pathName = in.readString();
    }


    @Generated(hash = 1369581562)
    public FileUploadItem(Long id, Integer depens, String UserId, Integer type, String fileId,
            Long size, String md5, String fName, String fKey, String fInfo, Integer pathId,
            String pathName) {
        this.id = id;
        this.depens = depens;
        this.UserId = UserId;
        this.type = type;
        this.fileId = fileId;
        this.size = size;
        this.md5 = md5;
        this.fName = fName;
        this.fKey = fKey;
        this.fInfo = fInfo;
        this.pathId = pathId;
        this.pathName = pathName;
    }

    public static final Creator<FileUploadItem> CREATOR = new Creator<FileUploadItem>() {
        @Override
        public FileUploadItem createFromParcel(Parcel in) {
            return new FileUploadItem(in);
        }

        @Override
        public FileUploadItem[] newArray(int size) {
            return new FileUploadItem[size];
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
        if (depens == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(depens);
        }
        dest.writeString(UserId);
        if (type == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(type);
        }
        dest.writeString(fileId);
        if (size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(size);
        }
        dest.writeString(md5);
        dest.writeString(fName);
        dest.writeString(fKey);
        dest.writeString(fInfo);
        if (pathId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pathId);
        }
        dest.writeString(pathName);
    }

    public Integer getDepens() {
        return depens;
    }

    public void setDepens(Integer depens) {
        this.depens = depens;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfKey() {
        return fKey;
    }

    public void setfKey(String fKey) {
        this.fKey = fKey;
    }

    public String getfInfo() {
        return fInfo;
    }

    public void setfInfo(String fInfo) {
        this.fInfo = fInfo;
    }

    public Integer getPathId() {
        return pathId;
    }

    public void setPathId(Integer pathId) {
        this.pathId = pathId;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getFName() {
        return this.fName;
    }


    public void setFName(String fName) {
        this.fName = fName;
    }


    public String getFKey() {
        return this.fKey;
    }


    public void setFKey(String fKey) {
        this.fKey = fKey;
    }


    public String getFInfo() {
        return this.fInfo;
    }


    public void setFInfo(String fInfo) {
        this.fInfo = fInfo;
    }
}
