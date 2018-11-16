package com.stratagile.pnrouter.entity;

public class JAddFriendDealRsp extends BaseEntity{

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
        private String FriendId;
        private int RetCode;

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int retCode) {
            RetCode = retCode;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        private String Msg;
        private String UserId;

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

        public int getResult() {
            return Result;
        }

        public void setResult(int result) {
            Result = result;
        }

        private String Nickname;
        private String FriendName;
        private int Result;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String friendId) {
            FriendId = friendId;
        }
    }
}
