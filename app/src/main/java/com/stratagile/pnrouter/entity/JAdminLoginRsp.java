package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JAdminLoginRsp extends BaseEntity {


    /**
     * timestamp : 1550800639
     * params : {"Action":"RouterLogin","RetCode":0,"UserSn":"01000001B827EBD089CB00005C6D3835","IdentifyCode":"","RouterId":"D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67","Qrcode":"type_1,46xQvEG/5K5bKHYgCOHCL4h9qX1HI223rAK5bSIWjb/FPQGLGYauLV0UXNyoEWQxxqFAgVx3sOb77VeJaFlcwNswQ7CR+daPSOMqVgmi7c0yS5WeuRsU2LYu66kCAXYD6kpX9cQYdPI0tfmyRvi6kCTOEqyDGrWBkRAV+cvqFMU=","RouterName":"YXBwIHdhcyA="}
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
         * Action : RouterLogin
         * RetCode : 0
         * UserSn : 01000001B827EBD089CB00005C6D3835
         * IdentifyCode :
         * RouterId : D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67
         * Qrcode : type_1,46xQvEG/5K5bKHYgCOHCL4h9qX1HI223rAK5bSIWjb/FPQGLGYauLV0UXNyoEWQxxqFAgVx3sOb77VeJaFlcwNswQ7CR+daPSOMqVgmi7c0yS5WeuRsU2LYu66kCAXYD6kpX9cQYdPI0tfmyRvi6kCTOEqyDGrWBkRAV+cvqFMU=
         * RouterName : YXBwIHdhcyA=
         */

        private String Action;
        private int RetCode;
        private String UserSn;
        private String IdentifyCode;
        private String RouterId;
        private String Qrcode;
        private String RouterName;

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

        public String getIdentifyCode() {
            return IdentifyCode;
        }

        public void setIdentifyCode(String IdentifyCode) {
            this.IdentifyCode = IdentifyCode;
        }

        public String getRouterId() {
            return RouterId;
        }

        public void setRouterId(String RouterId) {
            this.RouterId = RouterId;
        }

        public String getQrcode() {
            return Qrcode;
        }

        public void setQrcode(String Qrcode) {
            this.Qrcode = Qrcode;
        }

        public String getRouterName() {
            return RouterName;
        }

        public void setRouterName(String RouterName) {
            this.RouterName = RouterName;
        }
    }
}
