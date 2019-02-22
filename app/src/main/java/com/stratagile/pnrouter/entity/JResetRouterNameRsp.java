package com.stratagile.pnrouter.entity;

public class JResetRouterNameRsp extends BaseEntity {


    /**
     * Action : ResetRouterName
     * RetCode : 0
     */

    private String Action;
    private int RetCode;

    public String getAction() {
        return Action;
    }

    public void setAction(String Action) {
        this.Action = Action;
    }

    public int getRetCode() {
        return RetCode;
    }

    public void setRetCode(int RetCode) {
        this.RetCode = RetCode;
    }
}
