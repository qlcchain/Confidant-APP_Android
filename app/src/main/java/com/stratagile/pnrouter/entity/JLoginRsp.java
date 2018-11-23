package com.stratagile.pnrouter.entity;

public class JLoginRsp extends BaseEntity {


    /**
     * params : {"Action":"Login","RetCode":0,"RouterId":"D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472","UserSn":"02000008B827EBD089CB00005BF37E7F","UserId":"5AA2F93E498E65CC2C7BEBE78F113777F9225A15A01E001F079415522A5BEF19B92DCF4552FE","NeedAsysn":0}
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
         * Action : Login
         * RetCode : 0
         * RouterId : D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472
         * UserSn : 02000008B827EBD089CB00005BF37E7F
         * UserId : 5AA2F93E498E65CC2C7BEBE78F113777F9225A15A01E001F079415522A5BEF19B92DCF4552FE
         * NeedAsysn : 0
         */

        private String Action;
        private int RetCode;
        private String RouterId;
        private String UserSn;
        private String UserId;
        private int NeedAsysn;

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

        public String getRouterId() {
            return RouterId;
        }

        public void setRouterId(String Routerid) {
            this.RouterId = Routerid;
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
    }
}
