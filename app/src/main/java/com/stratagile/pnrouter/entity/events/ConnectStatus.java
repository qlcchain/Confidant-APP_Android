package com.stratagile.pnrouter.entity.events;

public class ConnectStatus {
    //连接状态，0已经连接，1正在连接，2未连接
    private int status;

    public ConnectStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
