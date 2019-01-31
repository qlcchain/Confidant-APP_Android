package com.stratagile.pnrouter.entity.events;

public class FileStatus {

    String fileKey;
    long fileSize;
    Boolean isDownLoad;
    Boolean isComplete;
    Boolean isStop;
    int segSeqResult;
    int segSeqTotal;
    int speed;
    Boolean SendGgain;
    int result;  // 0成功，1文件不存在
    public FileStatus(String fileKey, int result)
    {
          this.fileKey = fileKey;
          this.result = result;
    }
    public FileStatus(String fileKey, long fileSize, Boolean isDownLoad, Boolean isComplete, Boolean isStop, int segSeqResult, int segSeqTotal, int speed, Boolean SendGgain, int result)
    {
        this.fileKey = fileKey;
        this.fileSize = fileSize;
        this.isDownLoad = isDownLoad;
        this.isComplete = isComplete;
        this.isStop = isStop;
        this.segSeqResult = segSeqResult;
        this.segSeqTotal = segSeqTotal;
        this.speed = speed;
        this.SendGgain = SendGgain;
        this.result = result;
    }
    public String getFileKey() {
        return fileKey;
    }

    public int getResult() {
        return result;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Boolean getDownLoad() {
        return isDownLoad;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public Boolean getStop() {
        return isStop;
    }

    public int getSegSeqResult() {
        return segSeqResult;
    }

    public int getSegSeqTotal() {
        return segSeqTotal;
    }

    public int getSpeed() {
        return speed;
    }

    public Boolean getSendGgain() {
        return SendGgain;
    }
}
