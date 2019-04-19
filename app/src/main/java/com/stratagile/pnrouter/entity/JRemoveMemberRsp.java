package com.stratagile.pnrouter.entity;

public class JRemoveMemberRsp extends BaseEntity {

    private int timestampX;
    private JRemoveMemberRsp.ParamsBean params;

    public int getTimestampX() {
        return timestampX;
    }

    public void setTimestampX(int timestampX) {
        this.timestampX = timestampX;
    }

    public JRemoveMemberRsp.ParamsBean getParams() {
        return params;
    }

    public void setParams(JRemoveMemberRsp.ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {

        private String Action;
        private int RetCode;
        private String From;
        private String To;

        public String getAction() {
            return Action;
        }

        public void setAction(String action) {
            Action = action;
        }

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int retCode) {
            RetCode = retCode;
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
    }
}
