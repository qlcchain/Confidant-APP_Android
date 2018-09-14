package com.stratagile.pnrouter.entity;

public class JDelFriendPushRsp extends BaseEntity {
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

        /**
         * Action : AddFriendReq
         * RetCode : 1
         * Msg :

         */

        private String Action;
        private String UserId;
        private String FriendId;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

    }
}
