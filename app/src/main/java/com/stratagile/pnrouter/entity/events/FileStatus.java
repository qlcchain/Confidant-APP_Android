package com.stratagile.pnrouter.entity.events;

public class FileStatus {

    String filePath;
    long fileSize;
    Boolean isDownLoad;
    Boolean isComplete;
    Boolean isStop;
    int segSeqResult;
    int segSeqTotal;
    int speed;
    Boolean SendGgain;
    int result;  // 0成功，1文件不存在
    public FileStatus(String filePath,int result)
    {
          this.filePath = filePath;
          this.result = result;
    }
    public FileStatus(String filePath ,long fileSize, Boolean isDownLoad, Boolean isComplete, Boolean isStop, int segSeqResult, int segSeqTotal, int speed,Boolean SendGgain,int result)
    {
        this.filePath = filePath;
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
    public String getFilePath() {
        return filePath;
    }

    public int getResult() {
        return result;
    }
}
