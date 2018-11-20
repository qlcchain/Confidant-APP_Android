package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.RxEncodeTool;

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

        private String RouteId;
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

        public String getRouteId() {
            return RouteId;
        }

        public void setRouteId(String routeId) {
            RouteId = routeId;
        }

        public String getNickName() {
            try{
                String encryptedBytes = new String(RxEncodeTool.base64Decode(NickName));
                return encryptedBytes;
            }catch (IllegalArgumentException e)
            {
                return NickName;
            }
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }
    }
}
