package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupSysPushRsp extends BaseEntity{


    /**
     * timestamp : 1553076417
     * params : {"Action":"GroupSysPush","UserId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","GId":"group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","Type":3,"MsgId":32,"From":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","FromUserName":"aHc4ODg="}
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
         * Action : GroupSysPush
         * UserId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * GId : group0_admin13_time1553059435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * Type : 3
         * MsgId : 32
         * From : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * FromUserName : aHc4ODg=
         */

        private String Action;
        private String UserId;
        private String GId;
        private int Type;
        private int MsgId;
        private String From;
        private String FromUserName;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public int getType() {
            return Type;
        }

        public void setType(int Type) {
            this.Type = Type;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getFrom() {
            return From;
        }

        public void setFrom(String From) {
            this.From = From;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String FromUserName) {
            this.FromUserName = FromUserName;
        }
    }
}
