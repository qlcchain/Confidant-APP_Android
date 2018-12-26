package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JUserInfoPushRsp extends BaseEntity{


    /**
     * timestamp : 1545817868
     * params : {"Action":"UserInfoPush","UserId":"FC76D1B93045E9C8139D21BAD2D26E1AAAAE6880131285BAFBA9B99F83EEBD5F8044292FFBC7","FriendId":"823F82523A7AEF8CF7D3935E79A0E93827872AAE74B13D9D06C668FEDF38C56796D296EE11D0","NickName":"eXl0c3gxMTEx"}
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
         * Action : UserInfoPush
         * UserId : FC76D1B93045E9C8139D21BAD2D26E1AAAAE6880131285BAFBA9B99F83EEBD5F8044292FFBC7
         * FriendId : 823F82523A7AEF8CF7D3935E79A0E93827872AAE74B13D9D06C668FEDF38C56796D296EE11D0
         * NickName : eXl0c3gxMTEx
         */

        private String Action;
        private String UserId;
        private String FriendId;
        private String NickName;

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

        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String FriendId) {
            this.FriendId = FriendId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }
    }
}
