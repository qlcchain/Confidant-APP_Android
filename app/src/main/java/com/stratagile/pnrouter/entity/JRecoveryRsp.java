package com.stratagile.pnrouter.entity;

public class JRecoveryRsp extends BaseEntity {


    /**
     * params : {"Action":"Recovery","RetCode":1,"UserSn":"01000001B827EBD089CB00005BE14F55","UserId":"","NickName":""}
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
         * RetCode : 1
         * UserSn : 01000001B827EBD089CB00005BE14F55
         * UserId :
         * NickName :
         */

        private String Action;
        private int RetCode;
        private String UserSn;
        private String UserId;
        private String NickName;

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
    }
}
