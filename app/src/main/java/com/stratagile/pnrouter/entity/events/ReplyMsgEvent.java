package com.stratagile.pnrouter.entity.events;

public class ReplyMsgEvent {
    private String msgId;
    private String content;
    private String userId;
    public ReplyMsgEvent(String msgId, String content,String userId) {
        this.msgId = msgId;
        this.content = content;
        this.userId = userId;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
