package com.stratagile.pnrouter.entity;

public class JRecoveryRsp extends BaseEntity {


    /**
     * params : {"Action":"Recovery","RetCode":0,"RouteId":"D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472","UserSn":"02000009B827EBD089CB00005BF760D1","UserId":"EF88AE41E7DC8BBD18FB280CC3DC4F3CE2ABAE3AE38B0CE744D0DDEB9447A42822CB7CFBC526","NickName":"cmVkMg==","DataFileVersion":0}
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : Recovery
         * RetCode : 0
         * RouteId : D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472
         * UserSn : 02000009B827EBD089CB00005BF760D1
         * UserId : EF88AE41E7DC8BBD18FB280CC3DC4F3CE2ABAE3AE38B0CE744D0DDEB9447A42822CB7CFBC526
         * NickName : cmVkMg==
         * DataFileVersion : 0
         */

        private String Action;
        private int RetCode;
        private String RouteId;
        private String UserSn;
        private String UserId;
        private String NickName;
        private int DataFileVersion;

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

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public int getDataFileVersion() {
            return DataFileVersion;
        }

        public void setDataFileVersion(int DataFileVersion) {
            this.DataFileVersion = DataFileVersion;
        }
    }
}
