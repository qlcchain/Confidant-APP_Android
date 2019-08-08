package com.stratagile.pnrouter.entity.events;

public class ChangEmailStar {
    private int positon;
    private int type; //0取消 ，1 标星
    public ChangEmailStar(int positon, int type) {
        this.positon = positon;
        this.type = type;
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
