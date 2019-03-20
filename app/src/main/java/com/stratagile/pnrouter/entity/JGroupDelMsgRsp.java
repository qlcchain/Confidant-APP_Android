package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupDelMsgRsp extends BaseEntity{


    /**
     * timestamp : 1553073801
     * params : {"Action":"GroupDelMsg","RetCode":0,"MsgId":29,"ToId":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","GId":"group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","Msg":""}
     */

    private int timestampX;
    private ParamsBean params;

    public int getTimestampX() {
        return timestampX;
    }

    public void setTimestampX(int timestampX) {
        this.timestampX = timestampX;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : GroupDelMsg
         * RetCode : 0
         * MsgId : 29
         * ToId : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * GId : group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * Msg :
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String ToId;
        private String GId;
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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
    }
}
