package com.stratagile.pnrouter.entity;

/**
 * Created by hjk on 2018/9/30.
 */

public class JSendFileEndRsp extends BaseEntity {


    /**
     * params : {"Action":"SendFileEnd","RetCode":2,"MsgId":1538274371,"FromId":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","ToId":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE"}
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
         * Action : SendFileEnd
         * RetCode : 2
         * MsgId : 1538274371
         * FromId : 8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74
         * ToId : 8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String FromId;
        private String ToId;

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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public String getFromId() {
            return FromId;
        }

        public void setFromId(String FromId) {
            this.FromId = FromId;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }
    }
}
