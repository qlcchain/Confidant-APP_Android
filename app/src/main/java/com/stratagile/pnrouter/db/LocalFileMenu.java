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
    private String type;//文件类型，0 本地，1节点
    private Integer nodeId;//文件目录在服务器上的id
    private Long size;
    private Long LastModify;
    private Boolean isChoose;

    public LocalFileMenu() {
    }


    @Generated(hash = 1107858049)
    public LocalFileMenu(Long id, Long creatTime, String fileName, String path,
            Long fileNum, String type, Integer nodeId, Long size, Long LastModify,
            Boolean isChoose) {
        this.id = id;
        this.creatTime = creatTime;
        this.fileName = fileName;
        this.path = path;
        this.fileNum = fileNum;
        this.type = type;
        this.nodeId = nodeId;
        this.size = size;
        this.LastModify = LastModify;
        this.isChoose = isChoose;
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
        type = in.readString();
        if (in.readByte() == 0) {
            nodeId = null;
        } else {
            nodeId = in.readInt();
        }
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readLong();
        }
        if (in.readByte() == 0) {
            LastModify = null;
        } else {
            LastModify = in.readLong();
        }
        byte tmpIsChoose = in.readByte();
        isChoose = tmpIsChoose == 0 ? null : tmpIsChoose == 1;
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
        dest.writeString(type);
        if (nodeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(nodeId);
        }
        if (size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(size);
        }
        if (LastModify == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(LastModify);
        }
        dest.writeByte((byte) (isChoose == null ? 0 : isChoose ? 1 : 2));
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getLastModify() {
        return LastModify;
    }

    public void setLastModify(Long lastModify) {
        LastModify = lastModify;
    }

    public Boolean getChoose() {
        return isChoose;
    }

    public void setChoose(Boolean choose) {
        isChoose = choose;
    }


    public Boolean getIsChoose() {
        return this.isChoose;
    }


    public void setIsChoose(Boolean isChoose) {
        this.isChoose = isChoose;
    }
}
