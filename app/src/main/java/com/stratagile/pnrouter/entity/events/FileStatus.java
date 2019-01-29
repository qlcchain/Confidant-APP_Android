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

    public String getFilePath() {
        return filePath;
    }

    public int getResult() {
        return result;
    }
}
