package com.stratagile.pnrouter.entity;

public class JGroupVerifyPushRsp extends BaseEntity {

    /**
     * timestamp : 1553167693
     * params : {"Action":"GroupVerifyPush","From":"8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF","To":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","Aduit":"23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989","GId":"group1_admin8_time1553167635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","UserPubKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=","UserGroupKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=","FromName":"SHpw","ToName":"aHc4ODg=","Gname":"5Y2O5Li6576k5Li7"}
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
         * Action : GroupVerifyPush
         * From : 8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF
         * To : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * Aduit : 23BD2A28A22DC5A1145014A53B64374DC08A1BA04C0C9D24D4B3B2AF2197CE3A286DF0923989
         * GId : group1_admin8_time1553167635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * UserPubKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
         * UserGroupKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
         * FromName : SHpw
         * ToName : aHc4ODg=
         * Gname : 5Y2O5Li6576k5Li7
         */

        private String Action;
        private String From;
        private String To;
        private String Aduit;
        private String GId;
        private String UserPubKey;
        private String UserGroupKey;
        private String FromName;
        private String ToName;
        private String Gname;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
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

        public String getAduit() {
            return Aduit;
        }

        public void setAduit(String Aduit) {
            this.Aduit = Aduit;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getUserPubKey() {
            return UserPubKey;
        }

        public void setUserPubKey(String UserPubKey) {
            this.UserPubKey = UserPubKey;
        }

        public String getUserGroupKey() {
            return UserGroupKey;
        }

        public void setUserGroupKey(String UserGroupKey) {
            this.UserGroupKey = UserGroupKey;
        }

        public String getFromName() {
            return FromName;
        }

        public void setFromName(String FromName) {
            this.FromName = FromName;
        }

        public String getToName() {
            return ToName;
        }

        public void setToName(String ToName) {
            this.ToName = ToName;
        }

        public String getGname() {
            return Gname;
        }

        public void setGname(String Gname) {
            this.Gname = Gname;
        }
    }
}
