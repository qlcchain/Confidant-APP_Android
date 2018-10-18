package com.stratagile.pnrouter.entity;

public class JSendMsgRsp extends BaseEntity{


    /**
     * params : {"Action":"SendMsg","RetCode":0,"MsgId":1539583840,"FromId":"C86008352D125AAB086FC268B288F0E57363C6217A5C936D8FA2BFFB4C06B2577A466264B5DF","ToId":"EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788","Msg":"丁总"}
     */

    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        @Override
        public String toString() {
            return "ParamsBean{" +
                    "Action='" + Action + '\'' +
                    ", RetCode=" + RetCode +
                    ", MsgId=" + MsgId +
                    ", FromId='" + FromId + '\'' +
                    ", ToId='" + ToId + '\'' +
                    ", Msg='" + Msg + '\'' +
                    '}';
        }

        /**
         * Action : SendMsg
         * RetCode : 0
         * MsgId : 1539583840
         * FromId : C86008352D125AAB086FC268B288F0E57363C6217A5C936D8FA2BFFB4C06B2577A466264B5DF
         * ToId : EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788
         * Msg : 丁总
         */

        private String Action;
        private int RetCode;
        private int MsgId;
        private String FromId;
        private String ToId;
        private String Msg;

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

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }
    }
}
