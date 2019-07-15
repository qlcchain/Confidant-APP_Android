package com.stratagile.pnrouter.entity;

/**
 * Created by zl on 2019/07/15.
 */

public class EmailInfoData {
    private String type;
    private String From;
    private String Adress;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getAdress() {
        return Adress;
    }

    public void setAdress(String adress) {
        Adress = adress;
    }
}


