package com.stratagile.pnrouter.entity;

public class JPushLogoutRsp extends BaseEntity{



    private ParamsBean params;

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {

        private String Action;
        private String UserId;
        private String RouterId;
        private Integer Reason;
        private String Info;

        public String getAction() {
            return Action;
        }

        public void setAction(String action) {
            Action = action;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getRouterId() {
            return RouterId;
        }

        public void setRouterId(String routerId) {
            RouterId = routerId;
        }

        public Integer getReason() {
            return Reason;
        }

        public void setReason(Integer reason) {
            Reason = reason;
        }

        public String getInfo() {
            return Info;
        }

        public void setInfo(String info) {
            Info = info;
        }
    }
}
