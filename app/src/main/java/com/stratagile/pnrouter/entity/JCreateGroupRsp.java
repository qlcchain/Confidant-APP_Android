package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JCreateGroupRsp extends BaseEntity{


    /**
     * timestamp : 1552896778
     * params : {"Action":"CreateGroup","RetCode":0,"ToId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","GAdmin":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","GId":1,"GName":"b25l"}
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
         * Action : CreateGroup
         * RetCode : 0
         * ToId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * GAdmin : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * GId : 1
         * GName : b25l
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private String GAdmin;
        private int GId;
        private String GName;

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

        public String getGAdmin() {
            return GAdmin;
        }

        public void setGAdmin(String GAdmin) {
            this.GAdmin = GAdmin;
        }

        public int getGId() {
            return GId;
        }

        public void setGId(int GId) {
            this.GId = GId;
        }

        public String getGName() {
            return GName;
        }

        public void setGName(String GName) {
            this.GName = GName;
        }
    }
}
