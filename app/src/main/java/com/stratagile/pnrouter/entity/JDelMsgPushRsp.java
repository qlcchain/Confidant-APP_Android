package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JDelMsgPushRsp extends BaseEntity {

    /**
     * params : {"Action":"PushDelMsg","UserId":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE","FriendId":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","MsgId":1537172161}
     * timestamp : 1537172164
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
         * Action : PushDelMsg
         * UserId : 8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE
         * FriendId : 8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74
         * MsgId : 1537172161
         */

        private String Action;
        private String UserId;
        private String FriendId;
        private int MsgId;

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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }
    }
}
