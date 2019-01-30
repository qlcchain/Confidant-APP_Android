package com.stratagile.pnrouter.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecentFile {
    @Id(autoincrement = true)
    private Long id;
    private long timeStamp;
    private String fileName;
    // 文件类型0--所有文件
    //1--图片
    //2--语音
    //3--暂时空缺（跟上传的文件类型保持一致）
    //4--视频
    //5--文档
    //6--其他文件
    private int fileType;
    //操作类型，0 上传，1 下载，2 删除，3 分享
    private int opreateType;
    private String friendName;
    @Generated(hash = 206090348)
    public RecentFile(Long id, long timeStamp, String fileName, int fileType,
            int opreateType, String friendName) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.fileName = fileName;
        this.fileType = fileType;
        this.opreateType = opreateType;
        this.friendName = friendName;
    }
    @Generated(hash = 65986886)
    public RecentFile() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getFileType() {
        return this.fileType;
    }
    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
    public int getOpreateType() {
        return this.opreateType;
    }
    public void setOpreateType(int opreateType) {
        this.opreateType = opreateType;
    }
    public String getFriendName() {
        return this.friendName;
    }
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
