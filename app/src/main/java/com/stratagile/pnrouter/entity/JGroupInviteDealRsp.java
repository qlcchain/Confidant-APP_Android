package com.stratagile.pnrouter.entity;

public class JGroupInviteDealRsp extends BaseEntity{


    /**
     * timestamp : 1553162778
     * params : {"Action":"InviteGroup","RetCode":0,"ToId":"8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF","GId":"group1_admin36_time1553134930AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","GroupName":"54mb6YC8576k6YeM"}
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
         * Action : InviteGroup
         * RetCode : 0
         * ToId : 8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF
         * GId : group1_admin36_time1553134930AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * GroupName : 54mb6YC8576k6YeM
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private String GId;
        private String GroupName;

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

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getGroupName() {
            return GroupName;
        }

        public void setGroupName(String GroupName) {
            this.GroupName = GroupName;
        }
    }
}
