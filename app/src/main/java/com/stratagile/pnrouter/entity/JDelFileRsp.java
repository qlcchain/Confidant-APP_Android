package com.stratagile.pnrouter.entity;

public class JDelFileRsp extends BaseEntity {
    /**
     * appid : MIFI
     * timestamp : 1536839565
     * apiversion : 1
     * params : {"Action":"DelFile","RetCode":1,"Msg":""}
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
         * Action : DelFile
         * RetCode : 1
         * Msg :
         */

        private String Action;
        private int RetCode;

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

    }
}
