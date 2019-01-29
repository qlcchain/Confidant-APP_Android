package com.stratagile.pnrouter.entity.events;

public class FileStatus {

    String filePath;
    long totalSize;
    long sendSize;
    int sendSpeed;
    public FileStatus()
    {

    }
    public FileStatus(String filePath,long totalSize,long sendSize,int sendSpeed) {
        this.filePath = filePath;
        this.totalSize = totalSize;
        this.sendSize = sendSize;
        this.sendSpeed = sendSpeed;
    }
    public String getFilePath() {
        return filePath;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getSendSize() {
        return sendSize;
    }

    public int getSendSpeed() {
        return sendSpeed;
    }
}
