package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JOnlineStatusPushRsp extends BaseEntity{


    /**
     * timestamp : 1550732255
     * params : {"Action":"OnlineStatusPush","UserId":"530C017201763B59E82B875954C39E115E274442BF91403AC5E28EA56C9CF9535BDCB24D1F26","RouterId":"D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67","RouterName":"cm91dGVycw==","OnlineStatus":1}
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
         * Action : OnlineStatusPush
         * UserId : 530C017201763B59E82B875954C39E115E274442BF91403AC5E28EA56C9CF9535BDCB24D1F26
         * RouterId : D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67
         * RouterName : cm91dGVycw==
         * OnlineStatus : 1
         */

        private String Action;
        private String UserId;
        private String RouterId;
        private String RouterName;
        private int OnlineStatus;

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

        public String getRouterId() {
            return RouterId;
        }

        public void setRouterId(String RouterId) {
            this.RouterId = RouterId;
        }

        public String getRouterName() {
            return RouterName;
        }

        public void setRouterName(String RouterName) {
            this.RouterName = RouterName;
        }

        public int getOnlineStatus() {
            return OnlineStatus;
        }

        public void setOnlineStatus(int OnlineStatus) {
            this.OnlineStatus = OnlineStatus;
        }
    }
}
