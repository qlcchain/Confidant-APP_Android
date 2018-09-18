package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.message.Message;

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
        private List<Message> Payload;

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

        public List<Message> getPayload() {
            return Payload;
        }

        public void setPayload(List<Message> Payload) {
            this.Payload = Payload;
        }
    }
}
