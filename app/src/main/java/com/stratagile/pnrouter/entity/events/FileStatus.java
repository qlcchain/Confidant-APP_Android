package com.stratagile.pnrouter.entity.events;

public class FileStatus {

    String filePath;
    int result;  // 0成功，1文件不存在
    public FileStatus()
    {

    }
    public FileStatus(String filePath,int result)
    {

    }
    public FileStatus(String path ,long fileSize, Boolean isDownLoad, Boolean isComplete, Boolean isStop, int segSeqResult, int segSeqTotal, int speed,Boolean SendGgain,int result)
    {

    }
    public String getFilePath() {
        return filePath;
    }

    public int getResult() {
        return result;
    }
}
