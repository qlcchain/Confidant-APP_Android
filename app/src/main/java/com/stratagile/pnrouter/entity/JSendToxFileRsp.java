package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hjk on 2018/9/30.
 */

public class JSendToxFileRsp extends BaseEntity {


    /**
     * timestamp : 1544796609
     * params : {"Action":"SendFile","RetCode":1,"FromId":"915A381EF2241D3E988FF16D6403E2561A5DCF750A5867C655527D220B2A1233F85A818359EF","ToId":"50E0463B764ABEAA9DB988B10B1D763E53DA0AA05B60304B359CC11712BBBE3FC05EF948E558","FileId":1544796608,"FileType":1,"MsgId":0}
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
         * Action : SendFile
         * RetCode : 1
         * FromId : 915A381EF2241D3E988FF16D6403E2561A5DCF750A5867C655527D220B2A1233F85A818359EF
         * ToId : 50E0463B764ABEAA9DB988B10B1D763E53DA0AA05B60304B359CC11712BBBE3FC05EF948E558
         * FileId : 1544796608
         * FileType : 1
         * MsgId : 0
         */

        private String Action;
        private int RetCode;
        private String FromId;
        private String ToId;
        private int FileId;
        private int FileType;
        private int MsgId;

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

        public int getFileId() {
            return FileId;
        }

        public void setFileId(int FileId) {
            this.FileId = FileId;
        }

        public int getFileType() {
            return FileType;
        }

        public void setFileType(int FileType) {
            this.FileType = FileType;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }
    }
}
