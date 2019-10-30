package com.stratagile.pnrouter.entity.events;

public class ToxReceiveFileProgressEvent {

    private String key;
    private Integer fileNumber;
    private int position;
    private int filesize;
    public ToxReceiveFileProgressEvent(String key, Integer fileNumber,int position,int filesize) {
        this.fileNumber = fileNumber;
        this.key = key;
        this.position = position;
        this.filesize = filesize;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }
    public int getPosition() {
        return position;
    }

    public int getFilesize() {
        return filesize;
    }
}
