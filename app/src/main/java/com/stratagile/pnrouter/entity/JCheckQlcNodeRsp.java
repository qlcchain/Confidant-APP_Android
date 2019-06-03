package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JCheckQlcNodeRsp extends BaseEntity {


    /**
     * timestamp : 1559548635
     * params : {"Action":"CheckQlcNode","RetCode":1,"Status":0,"Info":""}
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
         * Action : CheckQlcNode
         * RetCode : 1
         * Status : 0
         * Info :
         */

        private String Action;
        private int RetCode;
        private int Status;
        private String Info;

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

        public int getStatus() {
            return Status;
        }

        public void setStatus(int Status) {
            this.Status = Status;
        }

        public String getInfo() {
            return Info;
        }

        public void setInfo(String Info) {
            this.Info = Info;
        }
    }
}
