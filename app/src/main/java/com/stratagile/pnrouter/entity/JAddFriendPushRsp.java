package com.stratagile.pnrouter.entity;

public class JAddFriendPushRsp extends BaseEntity {
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

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        /**
         * Action : AddFriendReq
         * RetCode : 1

         * Msg :
         */

        private String Action;
        private String UserId;
        private String FriendId;
        private String NickName;
        private String UserKey;

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String userKey) {
            UserKey = userKey;
        }

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }
    }
}
