package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.RxEncodeTool;

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

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }

        private int MsgId;
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

        public String getSrcKey() {
            return SrcKey;
        }

        public void setSrcKey(String srcKey) {
            SrcKey = srcKey;
        }

        public String getDstKey() {
            return DstKey;
        }

        public void setDstKey(String dstKey) {
            DstKey = dstKey;
        }

        private String SrcKey;

        private String DstKey;
    }
}
