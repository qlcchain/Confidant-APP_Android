package com.stratagile.pnrouter.entity;

public class JLoginRsp extends BaseEntity {

    /**
     * params : {"Action":"AddFriendReq","RetCode":1,"Msg":""}
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public int getNeedSynch() {
            return NeedSynch;
        }

        public void setNeedSynch(int needSynch) {
            NeedSynch = needSynch;
        }

        /**
         * Action : AddFriendReq
         * RetCode : 1
         * Msg :
         */

        private String Action;
        private int RetCode;
        private String UserId;
        private int NeedSynch;

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

    }
}
