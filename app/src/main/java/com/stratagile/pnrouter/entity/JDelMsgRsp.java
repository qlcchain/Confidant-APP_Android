package com.stratagile.pnrouter.entity;

public class JDelMsgRsp extends BaseEntity {


    /**
     * params : {"Action":"DelMsg","RetCode":0,"Msg":""}
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
         * Action : DelMsg
         * RetCode : 0
         * Msg :
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String Msg;

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

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }
    }
}
