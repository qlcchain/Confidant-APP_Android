package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupSendMsgRsp extends BaseEntity{


    /**
     * timestamp : 1553074343
     * params : {"Action":"GroupSendMsg","RetCode":0,"ToId":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","MsgId":32,"GId":"group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","Gname":"dGhyZWU=","UserKey":"xuWnPebXEkp0Va1Wwbwe468QuI1WEJcxqZZ3mjRTJn0bOJYZGriITZgU/rR/3qlnS/0XQJulQ1FbR9OzMSR9FQeP1Fit1PrLn3vFPk7H3WE=","Msg":"O57PAZOrg/QJRszH9jbyVw=="}
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
         * Action : GroupSendMsg
         * RetCode : 0
         * ToId : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * MsgId : 32
         * GId : group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * Gname : dGhyZWU=
         * UserKey : xuWnPebXEkp0Va1Wwbwe468QuI1WEJcxqZZ3mjRTJn0bOJYZGriITZgU/rR/3qlnS/0XQJulQ1FbR9OzMSR9FQeP1Fit1PrLn3vFPk7H3WE=
         * Msg : O57PAZOrg/QJRszH9jbyVw==
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int MsgId;
        private String GId;
        private String Gname;
        private String UserKey;
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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getGname() {
            return Gname;
        }

        public void setGname(String Gname) {
            this.Gname = Gname;
        }

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String UserKey) {
            this.UserKey = UserKey;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
    }
}
