package com.stratagile.pnrouter.entity;

public class JReadMsgPushRsp extends BaseEntity{


    /**
     * params : {"Action":"ReadMsgPush","UserId":"C146B04A9C02558646834E3EF336C7E1EE5F062830B744A6FFD5EED51F8D524BB0072E2DDA68","FriendId":"2F3F65132D63C7AD4D792BF23A827BD7E13423EC5FBA8AD50E6E5E1D25C5D4246FE89021CED0","ReadMsgs":""}
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
         * Action : ReadMsgPush
         * UserId : C146B04A9C02558646834E3EF336C7E1EE5F062830B744A6FFD5EED51F8D524BB0072E2DDA68
         * FriendId : 2F3F65132D63C7AD4D792BF23A827BD7E13423EC5FBA8AD50E6E5E1D25C5D4246FE89021CED0
         * ReadMsgs :
         */

        private String Action;
        private String UserId;
        private String FriendId;
        private String ReadMsgs;

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

        public String getReadMsgs() {
            return ReadMsgs;
        }

        public void setReadMsgs(String ReadMsgs) {
            this.ReadMsgs = ReadMsgs;
        }
    }
}
