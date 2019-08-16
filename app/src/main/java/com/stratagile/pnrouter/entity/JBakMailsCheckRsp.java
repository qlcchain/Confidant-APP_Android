package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JBakMailsCheckRsp extends BaseEntity {


    /**
     * timestamp : 1565938471
     * params : {"Action":"BakMailsCheck","RetCode":0,"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","Result":1}
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
         * Action : BakMailsCheck
         * RetCode : 0
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * Result : 1
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int Result;

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

        public int getResult() {
            return Result;
        }

        public void setResult(int Result) {
            this.Result = Result;
        }
    }
}
