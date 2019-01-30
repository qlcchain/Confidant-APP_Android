package events;

public class ToxSendFileProgressEvent {

    private String key;
    private Integer fileNumber;
    private int position;
    private int filesize;
    public ToxSendFileProgressEvent(String key, Integer fileNumber,int position,int filesize) {
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
