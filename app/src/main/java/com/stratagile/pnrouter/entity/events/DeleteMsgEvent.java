package com.stratagile.pnrouter.entity.events;

public class DeleteMsgEvent {
    private String msgId;

    public DeleteMsgEvent(String msgId) {
        this.msgId = msgId;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
