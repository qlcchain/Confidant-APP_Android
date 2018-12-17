package events;

import chat.tox.antox.wrapper.ContactKey;

public class ToxSendFileFinishedEvent {

    private String key;
    private Integer fileNumber;
    public ToxSendFileFinishedEvent(String key, Integer fileNumber) {
        this.fileNumber = fileNumber;
        this.key = key;
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

}
