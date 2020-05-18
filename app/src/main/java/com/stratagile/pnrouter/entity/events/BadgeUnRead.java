package com.stratagile.pnrouter.entity.events;

public class BadgeUnRead {
    private int unReadCount;

    public BadgeUnRead(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }
}
