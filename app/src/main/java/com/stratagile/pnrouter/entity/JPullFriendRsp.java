package com.stratagile.pnrouter.entity;

import java.util.List;

public class JPullFriendRsp extends BaseEntity {

    /**
     * Action : PullFriend
     * RetCode : 0.0
     * FriendNum : 1.0
     * Payload : [{"Status":0,"Id":"637E1441368744CBFE66DBACE11A7491E1990045A1E2D3EC7223D6E35C3FFB10493E459029A1","Name":"hu zhi"}]
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : AddFriendReq
         * RetCode : 1
         * Msg :
         */

        private String Action;
        private int RetCode;

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

        private int FriendNum;
        private List<PayloadBean> Payload;


        public double getFriendNum() {
            return FriendNum;
        }

        public void setFriendNum(int FriendNum) {
            this.FriendNum = FriendNum;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean {
            /**
             * Status : 0.0
             * Id : 637E1441368744CBFE66DBACE11A7491E1990045A1E2D3EC7223D6E35C3FFB10493E459029A1
             * Name : hu zhi
             */

            private int Status;
            private String Id;
            private String Index;
            private String Name;

            private String Remarks;
            private String UserKey;

            public int getStatus() {
                return Status;
            }

            public void setStatus(int Status) {
                this.Status = Status;
            }

            public String getId() {
                return Id;
            }

            public void setId(String Id) {
                this.Id = Id;
            }

            public String getIndex() {
                return Index;
            }

            public void setIndex(String index) {
                Index = index;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }
            public String getRemarks() {
                return Remarks;
            }

            public void setRemarks(String remarks) {
                Remarks = remarks;
            }
            public String getUserKey() {
                return UserKey;
            }

            public void setUserKey(String userKey) {
                UserKey = userKey;
            }
        }

    }


}
