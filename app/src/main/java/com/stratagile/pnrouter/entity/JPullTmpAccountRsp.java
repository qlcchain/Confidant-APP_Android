package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JPullTmpAccountRsp extends BaseEntity {


    /**
     * timestamp : 1554358266
     * params : {"Action":"PullTmpAccount","RetCode":0,"ToId":"7698F26767A81736AF416DDC27A8881775339F64A010159AF4202638314CF71EDB925B335275","UserSN":"03F00001B827EBD089CB00005C6F750E","Qrcode":"type_1,46xQvEG/5K5bKHYgCOHCL4h9qX1HI223rAK5bSIWjb/FPQGLGYauLV0UXNyoEWQxxqFAgVx3sOb77VeJaFlcwNswQ7CR+daPSOMqVgmi7c3UgNi0tWOiBegg5HR8vsJsxBZS9g/dkUvPv1U/kG+KIZ9DkfSDF0OJ1Cyhdelv0Kk="}
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
         * Action : PullTmpAccount
         * RetCode : 0
         * ToId : 7698F26767A81736AF416DDC27A8881775339F64A010159AF4202638314CF71EDB925B335275
         * UserSN : 03F00001B827EBD089CB00005C6F750E
         * Qrcode : type_1,46xQvEG/5K5bKHYgCOHCL4h9qX1HI223rAK5bSIWjb/FPQGLGYauLV0UXNyoEWQxxqFAgVx3sOb77VeJaFlcwNswQ7CR+daPSOMqVgmi7c3UgNi0tWOiBegg5HR8vsJsxBZS9g/dkUvPv1U/kG+KIZ9DkfSDF0OJ1Cyhdelv0Kk=
         */

        private String Action;
        private int RetCode;
        private String ToId;
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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
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
