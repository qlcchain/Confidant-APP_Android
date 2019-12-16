package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JFileActionRsp extends BaseEntity {


    /**
     * timestamp : 1576488435
     * params : {"Action":"FileAction","RetCode":0,"ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","FileId":0,"PathId":1,"Name":"3x39SkyNksd7X"}
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
         * Action : FileAction
         * RetCode : 0
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * FileId : 0
         * PathId : 1
         * Name : 3x39SkyNksd7X
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int FileId;
        private int PathId;
        private String Name;

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

        public int getPathId() {
            return PathId;
        }

        public void setPathId(int PathId) {
            this.PathId = PathId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }
}
