package com.stratagile.pnrouter.entity.events;

public class UnReadMessageCount {
    private int messageCount;

    public int getMessageCount() {
        return messageCount;
    }

    public UnReadMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
