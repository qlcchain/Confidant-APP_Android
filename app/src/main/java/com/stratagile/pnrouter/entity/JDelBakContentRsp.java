package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JDelBakContentRsp extends BaseEntity {


    /**
     * timestamp : 1581414915
     * params : {"Action":"DelBakContent","RetCode":0,"Type":1,"ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","Num":1}
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
         * Action : DelBakContent
         * RetCode : 0
         * Type : 1
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * Num : 1
         */

        private String Action;
        private int RetCode;
        private int Type;
        private String ToId;
        private int Num;

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

        public int getType() {
            return Type;
        }

        public void setType(int Type) {
            this.Type = Type;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }
    }
}
