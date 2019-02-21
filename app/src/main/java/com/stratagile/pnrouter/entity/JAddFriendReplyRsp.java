package com.stratagile.pnrouter.entity;

public class JAddFriendReplyRsp extends BaseEntity {
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

        public String getNickname() {
            return Nickname;
        }

        public void setNickname(String nickname) {
            Nickname = nickname;
        }

        public String getFriendName() {
            return FriendName;
        }

        public void setFriendName(String friendName) {
            FriendName = friendName;
        }

        /**
         * Action : AddFriendReq
         * RetCode : 1
         * Result 0 同意添加， 1 拒绝添加
         * Msg :
         */

        private String Action;

        public int getResult() {
            return Result;
        }

        public void setResult(int result) {
            Result = result;
        }

        private String UserId;
        private String FriendId;
        private String Nickname;
        private String FriendName;
        private String Sign;
        private String RouteId;
        private String RouteName;
        private int Result;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        private String UserKey;

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String userKey) {
            UserKey = userKey;
        }

        public String getSign() {
            return Sign;
        }

        public void setSign(String sign) {
            Sign = sign;
        }

        public String getRouteId() {
            return RouteId;
        }

        public void setRouteId(String routeId) {
            RouteId = routeId;
        }

        public String getRouteName() {
            return RouteName;
        }

        public void setRouteName(String routeName) {
            RouteName = routeName;
        }
    }
}
