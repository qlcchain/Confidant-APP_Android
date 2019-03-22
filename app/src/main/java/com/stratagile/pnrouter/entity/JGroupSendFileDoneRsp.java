package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupSendFileDoneRsp extends BaseEntity{


    /**
     * timestamp : 1553255269
     * params : {"Action":"GroupSendFileDone","RetCode":0,"ToId":"DF7A116D4B26A5C0836DB8172C935EC28DD042708E49326FFF97845993AF6B444390E50B3FE2","MsgId":7,"GId":"group1_admin4_time1553236916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","Gname":"dG94MzIy","FileName":"5kfNrqdJ3qRxpgLuUTRM5JremRNkDZYHogc4fjhfMv6LEk3Mqr8qTUXvbkRE2i5GeuxKjVziTCPX2hAT59pWwnU2wSUDK2co9zzSB1sL","FileId":"1553255266","FileType":1,"UserKey":"O7NBZl3pcGHbtpFngQePx8WjT+jHjOVN34iAaK6IcTDOnbAHp36zJlbkcPS37exRaRI4mWQi2kXls3Vh47NlLD17x9G4scgkiVu59XA2sho="}
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
         * Action : GroupSendFileDone
         * RetCode : 0
         * ToId : DF7A116D4B26A5C0836DB8172C935EC28DD042708E49326FFF97845993AF6B444390E50B3FE2
         * MsgId : 7
         * GId : group1_admin4_time1553236916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * Gname : dG94MzIy
         * FileName : 5kfNrqdJ3qRxpgLuUTRM5JremRNkDZYHogc4fjhfMv6LEk3Mqr8qTUXvbkRE2i5GeuxKjVziTCPX2hAT59pWwnU2wSUDK2co9zzSB1sL
         * FileId : 1553255266
         * FileType : 1
         * UserKey : O7NBZl3pcGHbtpFngQePx8WjT+jHjOVN34iAaK6IcTDOnbAHp36zJlbkcPS37exRaRI4mWQi2kXls3Vh47NlLD17x9G4scgkiVu59XA2sho=
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int MsgId;
        private String GId;
        private String Gname;
        private String FileName;
        private String FileId;
        private int FileType;
        private String UserKey;

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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getGname() {
            return Gname;
        }

        public void setGname(String Gname) {
            this.Gname = Gname;
        }

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }

        public String getFileId() {
            return FileId;
        }

        public void setFileId(String FileId) {
            this.FileId = FileId;
        }

        public int getFileType() {
            return FileType;
        }

        public void setFileType(int FileType) {
            this.FileType = FileType;
        }

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String UserKey) {
            this.UserKey = UserKey;
        }
    }
}
