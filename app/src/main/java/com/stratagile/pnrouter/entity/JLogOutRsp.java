package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JLogOutRsp extends BaseEntity{


    /**
     * timestamp : 1544683806
     * params : {"Action":"LogOut","RetCode":0,"UserId":"32D594C79CAF3ACBB28AB98E2FAE7D73A2A8673DDB801E03093EDA564162883B712F2B241983"}
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
         * Action : LogOut
         * RetCode : 0
         * UserId : 32D594C79CAF3ACBB28AB98E2FAE7D73A2A8673DDB801E03093EDA564162883B712F2B241983
         */

        private String Action;
        private int RetCode;
        private String UserId;

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

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }
    }
}
