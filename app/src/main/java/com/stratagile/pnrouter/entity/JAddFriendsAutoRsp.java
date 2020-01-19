package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JAddFriendsAutoRsp extends BaseEntity {


    /**
     * timestamp : 1572503963
     * params : {"Action":"AddFriendsAuto","RetCode":0,"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","Friends":"36C6B7A37844EC79C62CCC8D4D265C41712BB2F21A1370897B9C5D35AE3D7B4E35609D93FCF4"}
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
         * Action : AddFriendsAuto
         * RetCode : 0
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * Friends : 36C6B7A37844EC79C62CCC8D4D265C41712BB2F21A1370897B9C5D35AE3D7B4E35609D93FCF4
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private String Friends;

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

        public String getFriends() {
            return Friends;
        }

        public void setFriends(String Friends) {
            this.Friends = Friends;
        }
    }
}
