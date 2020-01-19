package com.stratagile.pnrouter.entity.events;

public class SendEmailSuccess {
    private int positon;
    public SendEmailSuccess(int positon) {
        this.positon = positon;
    }
    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }
}
