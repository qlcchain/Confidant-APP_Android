package com.stratagile.pnrouter.entity;

public class JGroupVerifyRsp extends BaseEntity{

    /**
     * timestamp : 1553227374
     * params : {"Action":"GroupVerify","RetCode":0,"ToId":"23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989","Msg":"","GAdmin":"23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989","GId":"group1_admin8_time1553167635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"}
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
         * Action : GroupVerify
         * RetCode : 0
         * ToId : 23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989
         * Msg :
         * GAdmin : 23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989
         * GId : group1_admin8_time1553167635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private String Msg;
        private String GAdmin;
        private String GId;

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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        public String getGAdmin() {
            return GAdmin;
        }

        public void setGAdmin(String GAdmin) {
            this.GAdmin = GAdmin;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }
    }
}
