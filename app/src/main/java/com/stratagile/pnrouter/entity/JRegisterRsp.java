package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JRegisterRsp extends BaseEntity {

    @Override
    public String toString() {
        return "JRegisterRsp{" +
                "params=" + params.toString() +
                '}';
    }

    /**
     * timestamp : 1550817467
     * params : {"Action":"Register","RetCode":0,"RouteId":"D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67","RouterName":"Q2hhbmdzaGE=","UserSn":"03F00001B827EBD089CB00005C6F750E","UserId":"9A7435C4569CAD9D9BF918E1CAA584059D4FAFE11AAC39BEAE03683C74E8DF6D88F4C486A97D","DataFileVersion":0,"DataFilePay":"9A7435C4569CAD9D9BF918E1CAA584059D4FAFE11AAC39BEAE03683C74E8DF6D88F4C486A97D"}
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        @Override
        public String toString() {
            return "ParamsBean{" +
                    "Action='" + Action + '\'' +
                    ", RetCode=" + RetCode +
                    ", RouteId='" + RouteId + '\'' +
                    ", RouterName='" + RouterName + '\'' +
                    ", UserSn='" + UserSn + '\'' +
                    ", UserId='" + UserId + '\'' +
                    ", DataFileVersion=" + DataFileVersion +
                    ", DataFilePay='" + DataFilePay + '\'' +
                    ", AdminId='" + AdminId + '\'' +
                    ", AdminName=" + AdminName +
                    ", AdminKey='" + AdminKey + '\'' +
                    '}';
        }

        /**
         * Action : Register
         * RetCode : 0
         * RouteId : D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67
         * RouterName : Q2hhbmdzaGE=
         * UserSn : 03F00001B827EBD089CB00005C6F750E
         * UserId : 9A7435C4569CAD9D9BF918E1CAA584059D4FAFE11AAC39BEAE03683C74E8DF6D88F4C486A97D
         * DataFileVersion : 0
         * DataFilePay : 9A7435C4569CAD9D9BF918E1CAA584059D4FAFE11AAC39BEAE03683C74E8DF6D88F4C486A97D
         */

        private String Action;
        private int RetCode;
        private String RouteId;
        private String RouterName;
        private String UserSn;
        private String UserId;
        private int DataFileVersion;
        private String DataFilePay;
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

        public String getRouteId() {
            return RouteId;
        }

        public void setRouteId(String RouteId) {
            this.RouteId = RouteId;
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

        public int getDataFileVersion() {
            return DataFileVersion;
        }

        public void setDataFileVersion(int DataFileVersion) {
            this.DataFileVersion = DataFileVersion;
        }

        public String getDataFilePay() {
            return DataFilePay;
        }

        public void setDataFilePay(String DataFilePay) {
            this.DataFilePay = DataFilePay;
        }

        public String getAdminId() {
            return AdminId;
        }

        public void setAdminId(String adminId) {
            AdminId = adminId;
        }

        public String getAdminName() {
            return AdminName;
        }

        public void setAdminName(String adminName) {
            AdminName = adminName;
        }

        public String getAdminKey() {
            return AdminKey;
        }

        public void setAdminKey(String adminKey) {
            AdminKey = adminKey;
        }
    }
}
