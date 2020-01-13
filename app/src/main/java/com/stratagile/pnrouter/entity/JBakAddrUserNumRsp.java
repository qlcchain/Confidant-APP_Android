package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JBakAddrUserNumRsp extends BaseEntity {


    /**
     * timestamp : 1578908380
     * params : {"Action":"BakAddrBookInfo","RetCode":0,"ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","FileId":0,"Num":0,"Fpath":"","Fkey":""}
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
         * Action : BakAddrBookInfo
         * RetCode : 0
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * FileId : 0
         * Num : 0
         * Fpath :
         * Fkey :
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int FileId;
        private int Num;
        private String Fpath;
        private String Fkey;

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

        public int getFileId() {
            return FileId;
        }

        public void setFileId(int FileId) {
            this.FileId = FileId;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public String getFpath() {
            return Fpath;
        }

        public void setFpath(String Fpath) {
            this.Fpath = Fpath;
        }

        public String getFkey() {
            return Fkey;
        }

        public void setFkey(String Fkey) {
            this.Fkey = Fkey;
        }
    }
}
