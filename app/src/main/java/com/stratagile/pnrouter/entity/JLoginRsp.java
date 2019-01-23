package com.stratagile.pnrouter.entity;

public class JLoginRsp extends BaseEntity {


    /**
     * params : {"Action":"Login","RetCode":0,"Routerid":"D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472","UserSn":"02000010B827EBD089CB00005BF7CD9A","UserId":"50D065FC0DEF163E03B2D7100D8A2244F7A6B374E85B42D99DA7D0DB793B740ADAFF9CA03DD3","NeedAsysn":0,"NickName":"cmVkOQ=="}
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
         * Routerid : D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472
         * UserSn : 02000010B827EBD089CB00005BF7CD9A
         * UserId : 50D065FC0DEF163E03B2D7100D8A2244F7A6B374E85B42D99DA7D0DB793B740ADAFF9CA03DD3
         * NeedAsysn : 0
         * NickName : cmVkOQ==
         */

        private String Action;
        private int RetCode;
        private String Index;
        private String Routerid;
        private String UserSn;
        private String UserId;
        private int NeedAsysn;
        private String NickName;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getIndex() {
            return Index;
        }

        public void setIndex(String index) {
            Index = index;
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
    }
}
