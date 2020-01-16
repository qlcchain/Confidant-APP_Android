package com.stratagile.pnrouter.entity.events;


public class ForegroundCallBack {
    private boolean foreground;//true 前台  false后台
    public ForegroundCallBack(boolean foreground) {
        this.foreground = foreground;
    }

    public boolean isForeground() {

        return foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

}
