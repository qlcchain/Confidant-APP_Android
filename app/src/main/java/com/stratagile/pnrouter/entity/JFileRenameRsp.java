package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JFileRenameRsp extends BaseEntity{


    /**
     * timestamp : 1551863135
     * params : {"Action":"FileRename","RetCode":0,"MsgId":582,"ToId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Filename":"NEDHC77xRezdV9n1enhnbvSPj8fKzDW7F7DpERyWdm9HRSZLr"}
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
         * Action : FileRename
         * RetCode : 0
         * MsgId : 582
         * ToId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * Filename : NEDHC77xRezdV9n1enhnbvSPj8fKzDW7F7DpERyWdm9HRSZLr
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String ToId;
        private String Filename;

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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public String getFilename() {
            return Filename;
        }

        public void setFilename(String Filename) {
            this.Filename = Filename;
        }
    }
}
