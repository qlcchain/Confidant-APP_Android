package com.stratagile.pnrouter.entity;

import java.util.List;

public class JFilesListPullRsp extends BaseEntity {


    /**
     * timestamp : 1577090945
     * params : {"Action":"FilesListPull","ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","RetCode":0,"PathId":1,"PathName":1,"Payload":[{"Depens":1,"Id":8,"Type":1,"Size":237019,"LastModify":1577088261,"Fname":"3Bqcqiy1K1oekpM5G594AJ5Jq8Ewace4qxtvd6YP21b1D6qyfsxj2BoEkwvMGrR9UBEoY5ud8qmfxp2ohKo71eer","Md5":"f908a0e774c41e2b21e5015368177944","Paths":"/user5/files/A005S08F1577088253","Finfo":",1440.0000000*2560.0000000","FKey":"2ez2bu7iPN0Dc0W/VoQrlJF8hrx1xFPv/uqUJ/MSpEjfmjmWVW6GQt67XbHbi5Je3nAttql1gE//4iw0v5qqRA56rIXi3btSKi+yMuiuRMI="}],"Num":1}
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
         * Action : FilesListPull
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * RetCode : 0
         * PathId : 1
         * PathName : 1
         * Payload : [{"Depens":1,"Id":8,"Type":1,"Size":237019,"LastModify":1577088261,"Fname":"3Bqcqiy1K1oekpM5G594AJ5Jq8Ewace4qxtvd6YP21b1D6qyfsxj2BoEkwvMGrR9UBEoY5ud8qmfxp2ohKo71eer","Md5":"f908a0e774c41e2b21e5015368177944","Paths":"/user5/files/A005S08F1577088253","Finfo":",1440.0000000*2560.0000000","FKey":"2ez2bu7iPN0Dc0W/VoQrlJF8hrx1xFPv/uqUJ/MSpEjfmjmWVW6GQt67XbHbi5Je3nAttql1gE//4iw0v5qqRA56rIXi3btSKi+yMuiuRMI="}]
         * Num : 1
         */

        private String Action;
        private String ToId;
        private int RetCode;
        private int PathId;
        private int PathName;
        private int Num;
        private List<NodeFileItem> Payload;

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

        public int getPathId() {
            return PathId;
        }

        public void setPathId(int PathId) {
            this.PathId = PathId;
        }

        public int getPathName() {
            return PathName;
        }

        public void setPathName(int PathName) {
            this.PathName = PathName;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public List<NodeFileItem> getPayload() {
            return Payload;
        }

        public void setPayload(List<NodeFileItem> Payload) {
            this.Payload = Payload;
        }

        public static class NodeFileItem {
            /**
             * Depens : 1
             * Id : 8
             * Type : 1
             * Size : 237019
             * LastModify : 1577088261
             * Fname : 3Bqcqiy1K1oekpM5G594AJ5Jq8Ewace4qxtvd6YP21b1D6qyfsxj2BoEkwvMGrR9UBEoY5ud8qmfxp2ohKo71eer
             * Md5 : f908a0e774c41e2b21e5015368177944
             * Paths : /user5/files/A005S08F1577088253
             * Finfo : ,1440.0000000*2560.0000000
             * FKey : 2ez2bu7iPN0Dc0W/VoQrlJF8hrx1xFPv/uqUJ/MSpEjfmjmWVW6GQt67XbHbi5Je3nAttql1gE//4iw0v5qqRA56rIXi3btSKi+yMuiuRMI=
             */

            private int Depens;
            private int Id;
            private int Type;
            private int Size;
            private int LastModify;
            private String Fname;
            private String Md5;
            private String Paths;
            private String Finfo;
            private String FKey;

            public int getDepens() {
                return Depens;
            }

            public void setDepens(int Depens) {
                this.Depens = Depens;
            }

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public int getType() {
                return Type;
            }

            public void setType(int Type) {
                this.Type = Type;
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

            public String getFname() {
                return Fname;
            }

            public void setFname(String Fname) {
                this.Fname = Fname;
            }

            public String getMd5() {
                return Md5;
            }

            public void setMd5(String Md5) {
                this.Md5 = Md5;
            }

            public String getPaths() {
                return Paths;
            }

            public void setPaths(String Paths) {
                this.Paths = Paths;
            }

            public String getFinfo() {
                return Finfo;
            }

            public void setFinfo(String Finfo) {
                this.Finfo = Finfo;
            }

            public String getFKey() {
                return FKey;
            }

            public void setFKey(String FKey) {
                this.FKey = FKey;
            }
        }
    }
}
