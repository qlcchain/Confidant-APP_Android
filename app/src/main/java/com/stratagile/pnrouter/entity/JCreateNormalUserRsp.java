package com.stratagile.pnrouter.entity;

public class JCreateNormalUserRsp extends BaseEntity {


    /**
     * params : {"Action":"CreateNormalUser","RetCode":0,"Routerid":"F1778E83EEDD84096D9C1F79E7E8AC62838E47BA66484E15467B7E74838910039533346DCAFD","UserSN":"0200000CB827EBD4703000005C0E2D73","Qrcode":"XJ2fmq2IG+jAs+GP/3DlfxZ+mOzhe+y1P60w7zl3JIshycPojKGg+6OL1DSAD1ouWALnCHirXVTwKxcSLgw3voesDTSJLwvqHlmdrnqkn+DlABygoEVnw6jIy4/Daj+HnUAj7KfwQTG7YkuOLZFj9VmNy4eKL3FNtVtMV7AU6i8="}
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
         * Action : CreateNormalUser
         * RetCode : 0
         * Routerid : F1778E83EEDD84096D9C1F79E7E8AC62838E47BA66484E15467B7E74838910039533346DCAFD
         * UserSN : 0200000CB827EBD4703000005C0E2D73
         * Qrcode : XJ2fmq2IG+jAs+GP/3DlfxZ+mOzhe+y1P60w7zl3JIshycPojKGg+6OL1DSAD1ouWALnCHirXVTwKxcSLgw3voesDTSJLwvqHlmdrnqkn+DlABygoEVnw6jIy4/Daj+HnUAj7KfwQTG7YkuOLZFj9VmNy4eKL3FNtVtMV7AU6i8=
         */

        private String Action;
        private int RetCode;
        private String Routerid;
        private String UserSN;
        private String Qrcode;

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

        public String getUserSN() {
            return UserSN;
        }

        public void setUserSN(String UserSN) {
            this.UserSN = UserSN;
        }

        public String getQrcode() {
            return Qrcode;
        }

        public void setQrcode(String Qrcode) {
            this.Qrcode = Qrcode;
        }
    }
}
