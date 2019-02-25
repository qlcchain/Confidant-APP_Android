package com.stratagile.pnrouter.entity.events;


public class ForegroundCallBack {
    private boolean foreground;//true 前台  false后台
    private boolean showfinger;//是否显示指纹
    public ForegroundCallBack(boolean foreground,boolean showfinger) {
        this.foreground = foreground;
    }

    public boolean isForeground() {

        return foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    public boolean isShowfinger() {
        return showfinger;
    }

    public void setShowfinger(boolean showfinger) {
        this.showfinger = showfinger;
    }
}
