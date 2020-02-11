package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JPullBakContentRsp extends BaseEntity {


    /**
     * timestamp : 1581404454
     * params : {"Action":"PullBakContent","RetCode":0,"Type":1,"ToId":"wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=","Num":1,"Payload":[{"Index":1,"Tel":"MTgwNzUxODYyNTE=","Num":1,"Uid":0,"Time":1580806685247,"Read":0,"Send":0,"User":"","Title":"","Cont":"ePF7KkJR1/HDK97iZqFQiA==","Key":"ePF7KkJR1/HDK97iZqFQiA=="}]}
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
         * Action : PullBakContent
         * RetCode : 0
         * Type : 1
         * ToId : wBJBnl9pUAhqTq9eyI0blEMDOWFIsloTsckao5OYEKA=
         * Num : 1
         * Payload : [{"Index":1,"Tel":"MTgwNzUxODYyNTE=","Num":1,"Uid":0,"Time":1580806685247,"Read":0,"Send":0,"User":"","Title":"","Cont":"ePF7KkJR1/HDK97iZqFQiA==","Key":"ePF7KkJR1/HDK97iZqFQiA=="}]
         */

        private String Action;
        private int RetCode;
        private int Type;
        private String ToId;
        private int Num;
        private List<SendSMSData> Payload;

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

        public List<SendSMSData> getPayload() {
            return Payload;
        }

        public void setPayload(List<SendSMSData> Payload) {
            this.Payload = Payload;
        }


    }
}
