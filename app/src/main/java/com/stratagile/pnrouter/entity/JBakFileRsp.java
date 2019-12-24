package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JBakFileRsp extends BaseEntity {


    /**
     * timestamp : 1577170033
     * params : {"Action":"BakFile","RetCode":0,"ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","SrcId":1577170024,"FileId":25,"PathId":1,"FilePath":"/user5/files/A005S08F1577170024","PathName":"5QpjAN3kSUtMiEADy","Fname":"3Bqcqiy1K1oekpM5G594AJ5Jr6A3aP4gApx9v8td9zBNMnN7RPUuwpMqAeJf3Sev6pjFxCpAeo9LgSTohz7r3Ssp"}
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
         * Action : BakFile
         * RetCode : 0
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * SrcId : 1577170024
         * FileId : 25
         * PathId : 1
         * FilePath : /user5/files/A005S08F1577170024
         * PathName : 5QpjAN3kSUtMiEADy
         * Fname : 3Bqcqiy1K1oekpM5G594AJ5Jr6A3aP4gApx9v8td9zBNMnN7RPUuwpMqAeJf3Sev6pjFxCpAeo9LgSTohz7r3Ssp
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int SrcId;
        private int FileId;
        private int PathId;
        private String FilePath;
        private String PathName;
        private String Fname;

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

        public int getSrcId() {
            return SrcId;
        }

        public void setSrcId(int SrcId) {
            this.SrcId = SrcId;
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

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
        }

        public String getPathName() {
            return PathName;
        }

        public void setPathName(String PathName) {
            this.PathName = PathName;
        }

        public String getFname() {
            return Fname;
        }

        public void setFname(String Fname) {
            this.Fname = Fname;
        }
    }
}
