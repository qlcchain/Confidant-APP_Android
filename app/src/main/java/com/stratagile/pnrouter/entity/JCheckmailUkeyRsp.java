package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JCheckmailUkeyRsp extends BaseEntity {


    /**
     * timestamp : 1564647848
     * params : {"Action":"CheckmailUkey","RetCode":0,"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","Num":1,"Payload":[{"User":"NTU0OTMyNjI4QHFxLmNvbQ==","PubKey":"t+INmZg96TCvt7w1Rt9Q+iToWFwdT2hCq6ck1sUwxR4="}]}
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
         * Action : CheckmailUkey
         * RetCode : 0
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * Num : 1
         * Payload : [{"User":"NTU0OTMyNjI4QHFxLmNvbQ==","PubKey":"t+INmZg96TCvt7w1Rt9Q+iToWFwdT2hCq6ck1sUwxR4="}]
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int Num;
        private List<PayloadBean> Payload;

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

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean {
            /**
             * User : NTU0OTMyNjI4QHFxLmNvbQ==
             * PubKey : t+INmZg96TCvt7w1Rt9Q+iToWFwdT2hCq6ck1sUwxR4=
             */

            private String User;
            private String PubKey;

            public String getUser() {
                return User;
            }

            public void setUser(String User) {
                this.User = User;
            }

            public String getPubKey() {
                return PubKey;
            }

            public void setPubKey(String PubKey) {
                this.PubKey = PubKey;
            }
        }
    }
}
