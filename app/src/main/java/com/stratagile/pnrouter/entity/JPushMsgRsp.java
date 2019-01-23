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

        private String From;//发送消息用户hashid，14位字符串,不可为空
        private String To;//接收消息用户hashid，14位字符串,不可为空

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

        public String getFrom() {
            return From;
        }

        public void setFrom(String from) {
            From = from;
        }

        public String getTo() {
            return To;
        }

        public void setTo(String to) {
            To = to;
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

        private String Sign;

        private String Nonce;

        private String PriKey;

        public String getSign() {
            return Sign;
        }

        public void setSign(String sign) {
            Sign = sign;
        }

        public String getNonce() {
            return Nonce;
        }

        public void setNonce(String nonce) {
            Nonce = nonce;
        }

        public String getPriKey() {
            return PriKey;
        }

        public void setPriKey(String priKey) {
            PriKey = priKey;
        }
    }
}
