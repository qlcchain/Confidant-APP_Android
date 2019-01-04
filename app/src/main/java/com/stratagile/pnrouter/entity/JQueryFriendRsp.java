package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JQueryFriendRsp extends BaseEntity{


    /**
     * timestamp : 1546122957
     * params : {"Action":"QueryFriend","RetCode":0,"FriendId":"CA3AD1E9888359FBCE481DE0D316B2212ED93D7FC06255EA6D78965C75F78C755AF43B799D31"}
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
         * Action : QueryFriend
         * RetCode : 0
         * FriendId : CA3AD1E9888359FBCE481DE0D316B2212ED93D7FC06255EA6D78965C75F78C755AF43B799D31
         */

        private String Action;
        private int RetCode;
        private String FriendId;

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

        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String FriendId) {
            this.FriendId = FriendId;
        }
    }
}
