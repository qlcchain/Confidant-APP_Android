package com.stratagile.pnrouter.entity;

public class JDelFriendCmdRsp extends BaseEntity {
    /**
     * appid : MIFI
     * timestamp : 1536839565
     * apiversion : 1
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
        /**
         * Action : AddFriendReq
         * RetCode : 1
         * Msg :
         */

        private String Action;
        private int RetCode;
        private String Msg;

        private String UserId;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String friendId) {
            FriendId = friendId;
        }

        private String FriendId;
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

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
    }
}
