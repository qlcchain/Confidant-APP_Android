package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JToxPullFileRsp extends BaseEntity {


    /**
     * timestamp : 1545035363
     * params : {"Action":"PullFile","RetCode":0,"MsgId":669,"FromId":"50E0463B764ABEAA9DB988B10B1D763E53DA0AA05B60304B359CC11712BBBE3FC05EF948E558","ToId":"915A381EF2241D3E988FF16D6403E2561A5DCF750A5867C655527D220B2A1233F85A818359EF","FileMD5":"d41d8cd98f00b204e9800998ecf8427e","FileSize":4096,"FileName":""}
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
         * Action : PullFile
         * RetCode : 0
         * MsgId : 669
         * FromId : 50E0463B764ABEAA9DB988B10B1D763E53DA0AA05B60304B359CC11712BBBE3FC05EF948E558
         * ToId : 915A381EF2241D3E988FF16D6403E2561A5DCF750A5867C655527D220B2A1233F85A818359EF
         * FileMD5 : d41d8cd98f00b204e9800998ecf8427e
         * FileSize : 4096
         * FileName :
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String FromId;
        private String ToId;
        private String FileMD5;
        private int FileSize;
        private String FileName;

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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getFromId() {
            return FromId;
        }

        public void setFromId(String FromId) {
            this.FromId = FromId;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public String getFileMD5() {
            return FileMD5;
        }

        public void setFileMD5(String FileMD5) {
            this.FileMD5 = FileMD5;
        }

        public int getFileSize() {
            return FileSize;
        }

        public void setFileSize(int FileSize) {
            this.FileSize = FileSize;
        }

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }
    }
}
