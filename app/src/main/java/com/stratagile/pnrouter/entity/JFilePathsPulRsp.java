package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JFilePathsPulRsp extends BaseEntity {


    /**
     * timestamp : 1576488798
     * params : {"Action":"FilePathsPull","ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","RetCode":0,"Payload":[{"Id":1,"PathName":"3x39SkyNksd7X","FilesNum":0,"Size":0,"LastModify":1576488435}],"Num":1}
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
         * Action : FilePathsPull
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * RetCode : 0
         * Payload : [{"Id":1,"PathName":"3x39SkyNksd7X","FilesNum":0,"Size":0,"LastModify":1576488435}]
         * Num : 1
         */

        private String Action;
        private String ToId;
        private int RetCode;
        private int Num;
        private List<NodeFileMenu> Payload;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int RetCode) {
            this.RetCode = RetCode;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public List<NodeFileMenu> getPayload() {
            return Payload;
        }

        public void setPayload(List<NodeFileMenu> Payload) {
            this.Payload = Payload;
        }

        public static class NodeFileMenu {
            /**
             * Id : 1
             * PathName : 3x39SkyNksd7X
             * FilesNum : 0
             * Size : 0
             * LastModify : 1576488435
             */

            private int Id;
            private String PathName;
            private int FilesNum;
            private int Size;
            private int LastModify;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getPathName() {
                return PathName;
            }

            public void setPathName(String PathName) {
                this.PathName = PathName;
            }

            public int getFilesNum() {
                return FilesNum;
            }

            public void setFilesNum(int FilesNum) {
                this.FilesNum = FilesNum;
            }

            public int getSize() {
                return Size;
            }

            public void setSize(int Size) {
                this.Size = Size;
            }

            public int getLastModify() {
                return LastModify;
            }

            public void setLastModify(int LastModify) {
                this.LastModify = LastModify;
            }
        }
    }
}
