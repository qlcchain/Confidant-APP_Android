package com.stratagile.pnrouter.entity.events;

public class SaveMsgEvent {
    private String msgId;

    private int result;
    public SaveMsgEvent(String msgId,int result) {
        this.msgId = msgId;
        this.result = result;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
