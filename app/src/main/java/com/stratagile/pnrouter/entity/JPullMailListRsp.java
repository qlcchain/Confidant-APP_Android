package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JPullMailListRsp extends BaseEntity {
    /**
     * timestamp : 1565857072
     * params : {"Action":"PullMailList","RetCode":0,"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","Num":6,"Payload":[{"Id":18,"Label":0,"Read":0,"Userkey":"Wz201WlTC5QO2og4GXgMZW4ofGbRw2eakG6SJ3NMdRio6F9a5ZsWUdTEEsfQM4B4GFQYN8vCX+/tfS9QdDDa69BL9JRsjm1GhZxP4tJrhDU=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565845521"},{"Id":19,"Label":0,"Read":0,"Userkey":"a8zN2BH1og6mKkbRYuA80W/0Jcs4tiw4qDP96rALfFnKFf8jur8FKIxsghD3zeruuv0dAbypl8dELtSpTqU8gf3kQVe45lJF/xLrUPpBMRI=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848264"},{"Id":20,"Label":0,"Read":0,"Userkey":"j5akd/haDbTlzu3OEK6mOQ9mKYEvfUoT6yPB6M3a7HYwYRtf3nex5YqXMQ6nqy0t/w72F77dWTmEOrUYeGvEy2XSZOMnX29KjefopK89gto=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848583"},{"Id":21,"Label":0,"Read":0,"Userkey":"fS1hoH0DE5gBHtenAmqAYZ830G0AO0zs8mTPLBTuBHbDckyTsMMP3WW9DH0gRY4JtEYbnyWmxBLOG2nYiG/TXCk7gHM+z4Y2hbQE/1lyfJs=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848605"},{"Id":22,"Label":0,"Read":0,"Userkey":"JcgNNnsm/FNCALMfhth51QKRb05t8m3FPPUHl+MG5SH1gSf1WkGONivusjE9Z4I17fxPrKKXz5ggcFcnQVyeKJ9K4ThKsu6UaXiGa7AgXSY=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848612"},{"Id":23,"Label":0,"Read":0,"Userkey":"Se+LYWluV3VmDhpcRqcvrPH5cYRiVimkuoVmqNkxa3qB9zaMOUvg3TWH0ASXBd8ggowoGfEI4Xl0ty8I++ECbwtDz9lmCuEP2WvGKxrq+B0=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848672"}]}
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
         * Action : PullMailList
         * RetCode : 0
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * Num : 6
         * Payload : [{"Id":18,"Label":0,"Read":0,"Userkey":"Wz201WlTC5QO2og4GXgMZW4ofGbRw2eakG6SJ3NMdRio6F9a5ZsWUdTEEsfQM4B4GFQYN8vCX+/tfS9QdDDa69BL9JRsjm1GhZxP4tJrhDU=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565845521"},{"Id":19,"Label":0,"Read":0,"Userkey":"a8zN2BH1og6mKkbRYuA80W/0Jcs4tiw4qDP96rALfFnKFf8jur8FKIxsghD3zeruuv0dAbypl8dELtSpTqU8gf3kQVe45lJF/xLrUPpBMRI=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848264"},{"Id":20,"Label":0,"Read":0,"Userkey":"j5akd/haDbTlzu3OEK6mOQ9mKYEvfUoT6yPB6M3a7HYwYRtf3nex5YqXMQ6nqy0t/w72F77dWTmEOrUYeGvEy2XSZOMnX29KjefopK89gto=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848583"},{"Id":21,"Label":0,"Read":0,"Userkey":"fS1hoH0DE5gBHtenAmqAYZ830G0AO0zs8mTPLBTuBHbDckyTsMMP3WW9DH0gRY4JtEYbnyWmxBLOG2nYiG/TXCk7gHM+z4Y2hbQE/1lyfJs=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848605"},{"Id":22,"Label":0,"Read":0,"Userkey":"JcgNNnsm/FNCALMfhth51QKRb05t8m3FPPUHl+MG5SH1gSf1WkGONivusjE9Z4I17fxPrKKXz5ggcFcnQVyeKJ9K4ThKsu6UaXiGa7AgXSY=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848612"},{"Id":23,"Label":0,"Read":0,"Userkey":"Se+LYWluV3VmDhpcRqcvrPH5cYRiVimkuoVmqNkxa3qB9zaMOUvg3TWH0ASXBd8ggowoGfEI4Xl0ty8I++ECbwtDz9lmCuEP2WvGKxrq+B0=","MailInfo":"","EmailPath":"/user5/mail/U005S07F1565848672"}]
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int Num;
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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean {
            /**
             * Id : 18
             * Label : 0
             * Read : 0
             * Userkey : Wz201WlTC5QO2og4GXgMZW4ofGbRw2eakG6SJ3NMdRio6F9a5ZsWUdTEEsfQM4B4GFQYN8vCX+/tfS9QdDDa69BL9JRsjm1GhZxP4tJrhDU=
             * MailInfo :
             * EmailPath : /user5/mail/U005S07F1565845521
             */

            private int Id;
            private int Label;
            private int Read;
            private String Userkey;
            private String MailInfo;
            private String EmailPath;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public int getLabel() {
                return Label;
            }

            public void setLabel(int Label) {
                this.Label = Label;
            }

            public int getRead() {
                return Read;
            }

            public void setRead(int Read) {
                this.Read = Read;
            }

            public String getUserkey() {
                return Userkey;
            }

            public void setUserkey(String Userkey) {
                this.Userkey = Userkey;
            }

            public String getMailInfo() {
                return MailInfo;
            }

            public void setMailInfo(String MailInfo) {
                this.MailInfo = MailInfo;
            }

            public String getEmailPath() {
                return EmailPath;
            }

            public void setEmailPath(String EmailPath) {
                this.EmailPath = EmailPath;
            }
        }
    }
}
