package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JLoginRsp extends BaseEntity {


    /**
     * timestamp : 1555049147
     * params : {"Action":"Login","RetCode":0,"Routerid":"3089876DD8A1A76274A3150FB87F9B24EF0C4C9AF16FAA37BAFE99C955DA2538155D6D0E5998","RouterName":"cG93IG5vZGU=","UserSn":"0100000200163E04B79700005C6FE08D","UserId":"5C783F904E7DDF5943E2818B3FFCFBBC8D6DB1B3788A8AEBA053507003234051C330AF9FE5A4","NeedAsysn":0,"NickName":"aHpwNjY2","AdminId":"B46DB9F7758AAD04364837FCCD103AF2DE85FFD2C260A4E8DEDFE88F6BC276658FD49825589B","AdminName":"Y2FvNA==","AdminKey":"eK659D5T5AJcyfRI2vV1R5SEm9c9IWtgNJyiBX7kNLI="}
     */

    private int timestampX;
    private ParamsBean params;

    @Override
    public String toString() {
        return "JLoginRsp{" +
                "params=" + params.toString() +
                '}';
    }

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
         * Action : Login
         * RetCode : 0
         * Routerid : 3089876DD8A1A76274A3150FB87F9B24EF0C4C9AF16FAA37BAFE99C955DA2538155D6D0E5998
         * RouterName : cG93IG5vZGU=
         * UserSn : 0100000200163E04B79700005C6FE08D
         * UserId : 5C783F904E7DDF5943E2818B3FFCFBBC8D6DB1B3788A8AEBA053507003234051C330AF9FE5A4
         * NeedAsysn : 0
         * NickName : aHpwNjY2
         * AdminId : B46DB9F7758AAD04364837FCCD103AF2DE85FFD2C260A4E8DEDFE88F6BC276658FD49825589B
         * AdminName : Y2FvNA==
         * AdminKey : eK659D5T5AJcyfRI2vV1R5SEm9c9IWtgNJyiBX7kNLI=
         */

        private String Action;
        private int RetCode;
        private String Routerid;
        private String RouterName;
        private String UserSn;
        private String UserId;
        private int NeedAsysn;
        private String NickName;
        private String AdminId;
        private String AdminName;
        private String AdminKey;

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

        public String getRouterid() {
            return Routerid;
        }

        public void setRouterid(String Routerid) {
            this.Routerid = Routerid;
        }

        public String getRouterName() {
            return RouterName;
        }

        public void setRouterName(String RouterName) {
            this.RouterName = RouterName;
        }

        public String getUserSn() {
            return UserSn;
        }

        public void setUserSn(String UserSn) {
            this.UserSn = UserSn;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public int getNeedAsysn() {
            return NeedAsysn;
        }

        public void setNeedAsysn(int NeedAsysn) {
            this.NeedAsysn = NeedAsysn;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getAdminId() {
            return AdminId;
        }

        public void setAdminId(String AdminId) {
            this.AdminId = AdminId;
        }

        public String getAdminName() {
            return AdminName;
        }

        public void setAdminName(String AdminName) {
            this.AdminName = AdminName;
        }

        public String getAdminKey() {
            return AdminKey;
        }

        public void setAdminKey(String AdminKey) {
            this.AdminKey = AdminKey;
        }
    }
}
