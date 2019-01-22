package com.stratagile.tox.events;

public class ToxFriendStatusEvent {
    //连接状态，0未上线，1上线
    private int status;
    public ToxFriendStatusEvent(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
