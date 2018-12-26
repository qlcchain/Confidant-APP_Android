package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JUserInfoUpdateRsp extends BaseEntity{


    /**
     * timestamp : 1545816807
     * params : {"Action":"UserInfoUpdate","RetCode":0,"UserId":"6C1530C9B4EE4FAB8F9E0342CB095A01BD0DD6CC9A2FBE0D124B6E5C01253457AB2FF8E3EF07"}
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
         * Action : UserInfoUpdate
         * RetCode : 0
         * UserId : 6C1530C9B4EE4FAB8F9E0342CB095A01BD0DD6CC9A2FBE0D124B6E5C01253457AB2FF8E3EF07
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
