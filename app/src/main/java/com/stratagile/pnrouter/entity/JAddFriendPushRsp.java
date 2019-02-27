package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JAddFriendPushRsp extends BaseEntity {

    /**
     * timestamp : 1551255062
     * params : {"Action":"AddFriendPush","UserId":"7287A1CDCEE1BC65D97283096D44B6207E110ADB5D9736494B5BCE21C79223132A4EA8A2DCD2","FriendId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","NickName":"cmVkODg4","UserKey":"QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA=","Msg":"SSdtIHJlZDg4OA==","RouterId":"D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67","RouterName":"Q2hhbmdTaGE="}
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
         * Action : AddFriendPush
         * UserId : 7287A1CDCEE1BC65D97283096D44B6207E110ADB5D9736494B5BCE21C79223132A4EA8A2DCD2
         * FriendId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * NickName : cmVkODg4
         * UserKey : QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA=
         * Msg : SSdtIHJlZDg4OA==
         * RouterId : D8662FBE4E2DBC81076EACEF4976F035BEE26E29F1B90C137693AAAC8AC22B76F1F00BCBBD67
         * RouterName : Q2hhbmdTaGE=
         */

        private String Action;
        private String UserId;
        private String FriendId;
        private String NickName;
        private String UserKey;
        private String Msg;
        private String RouterId;
        private String RouterName;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getFriendId() {
            return FriendId;
        }

        public void setFriendId(String FriendId) {
            this.FriendId = FriendId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String UserKey) {
            this.UserKey = UserKey;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        public String getRouterId() {
            return RouterId;
        }

        public void setRouterId(String RouterId) {
            this.RouterId = RouterId;
        }

        public String getRouterName() {
            return RouterName;
        }

        public void setRouterName(String RouterName) {
            this.RouterName = RouterName;
        }
    }
}
