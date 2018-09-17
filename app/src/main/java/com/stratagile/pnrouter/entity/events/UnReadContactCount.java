package com.stratagile.pnrouter.entity.events;

public class UnReadContactCount {
    private int messageCount;

    public int getMessageCount() {
        return messageCount;
    }

    public UnReadContactCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
