package com.stratagile.pnrouter.entity;

import java.util.SimpleTimeZone;

public class JPushMsgRsp extends BaseEntity {
    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * Action : PushMsg
         * RetCode : 1

         * Msg :
         */

        private String Action;
        private String FromId;
        private String ToId;

        public String getMsgId() {
            return MsgId;
        }

        public void setMsgId(String msgId) {
            MsgId = msgId;
        }

        private String MsgId;
        private String Msg;

        public String getFromId() {
            return FromId;
        }

        public void setFromId(String fromId) {
            FromId = fromId;
        }

        public String getToId() {
            return ToId;
        }

        public void setToId(String toId) {
            ToId = toId;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }



        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }
    }
}
