package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupSendMsgRsp extends BaseEntity{


    /**
     * timestamp : 1552961387
     * params : {"Action":"GroupVerify","RetCode":0}
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
         * Action : GroupVerify
         * RetCode : 0
         */

        private String Action;
        private int RetCode;

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
    }
}
