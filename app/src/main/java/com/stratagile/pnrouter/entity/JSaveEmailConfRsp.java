package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JSaveEmailConfRsp extends BaseEntity {


    /**
     * timestamp : 1564645732
     * params : {"Action":"SaveEmailConf","RetCode":0,"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","User":"emaildev@qlink.mobi","ContactsFile":"","ContactsMd5":""}
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
         * Action : SaveEmailConf
         * RetCode : 0
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * User : emaildev@qlink.mobi
         * ContactsFile :
         * ContactsMd5 :
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private String User;
        private String ContactsFile;
        private String ContactsMd5;

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

        public String getUser() {
            return User;
        }

        public void setUser(String User) {
            this.User = User;
        }

        public String getContactsFile() {
            return ContactsFile;
        }

        public void setContactsFile(String ContactsFile) {
            this.ContactsFile = ContactsFile;
        }

        public String getContactsMd5() {
            return ContactsMd5;
        }

        public void setContactsMd5(String ContactsMd5) {
            this.ContactsMd5 = ContactsMd5;
        }
    }
}
