package com.stratagile.pnrouter.entity;

import java.util.List;

public class JPullMsgRsp extends BaseEntity {

    /**
     * params : {"Action":"PullMsg","RetCode":0,"MsgNum":10,"Payload":[{"MsgId":1537001926,"MsgType":1,"TimeStatmp":1537001926,"From":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","To":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE","Msg":"[发怒][调皮][调皮][调皮]"},{"MsgId":1537001367,"MsgType":1,"TimeStatmp":1537001367,"From":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE","To":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","Msg":"[调皮]"}]}
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
         * Action : PullMsg
         * RetCode : 0
         * MsgNum : 10
         * Payload : [{"MsgId":1537001926,"MsgType":1,"TimeStatmp":1537001926,"From":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","To":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE","Msg":"[发怒][调皮][调皮][调皮]"},{"MsgId":1537001367,"MsgType":1,"TimeStatmp":1537001367,"From":"8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE","To":"8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74","Msg":"[调皮]"}]
         */

        private String Action;
        private int RetCode;
        private int MsgNum;
        private List<PayloadBean> Payload;

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

        public int getMsgNum() {
            return MsgNum;
        }

        public void setMsgNum(int MsgNum) {
            this.MsgNum = MsgNum;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean {
            /**
             * MsgId : 1537001926
             * MsgType : 1
             * TimeStatmp : 1537001926
             * From : 8A9A37275400CE381F80C738235440350FB8322824988565DED2793AE83BFF377F0D95AC5A74
             * To : 8EDE2DD3C5A84F14A386155233AE44AD1DB9752DF9FE744A562548A896A30913BCB70A123ADE
             * Msg : [发怒][调皮][调皮][调皮]
             */

            private int MsgId;
            private int MsgType;
            private int TimeStatmp;
            private String From;
            private String To;
            private String Msg;

            public int getMsgId() {
                return MsgId;
            }

            public void setMsgId(int MsgId) {
                this.MsgId = MsgId;
            }

            public int getMsgType() {
                return MsgType;
            }

            public void setMsgType(int MsgType) {
                this.MsgType = MsgType;
            }

            public int getTimeStatmp() {
                return TimeStatmp;
            }

            public void setTimeStatmp(int TimeStatmp) {
                this.TimeStatmp = TimeStatmp;
            }

            public String getFrom() {
                return From;
            }

            public void setFrom(String From) {
                this.From = From;
            }

            public String getTo() {
                return To;
            }

            public void setTo(String To) {
                this.To = To;
            }

            public String getMsg() {
                return Msg;
            }

            public void setMsg(String Msg) {
                this.Msg = Msg;
            }
        }
    }
}
