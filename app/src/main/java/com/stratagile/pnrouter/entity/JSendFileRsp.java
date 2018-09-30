package com.stratagile.pnrouter.entity;

/**
 * Created by hjk on 2018/9/30.
 */

public class JSendFileRsp extends BaseEntity {


    /**
     * params : {"Action":"PullMsg","RetCode":1,"MsgNum":0}
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : PullMsg
         * RetCode : 1
         * MsgNum : 0
         */

        private String Action;
        private int RetCode;
        private int MsgNum;

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

        public int getMsgNum() {
            return MsgNum;
        }

        public void setMsgNum(int MsgNum) {
            this.MsgNum = MsgNum;
        }
    }
}
