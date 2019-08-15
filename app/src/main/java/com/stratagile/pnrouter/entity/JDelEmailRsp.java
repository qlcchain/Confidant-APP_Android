package com.stratagile.pnrouter.entity;

public class JDelEmailRsp extends BaseEntity {

    /**
     * appid : MIFI
     * msgid : 1565882031
     * apiversion : 6
     * params : {"ToId":"EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6","Action":"DelEmailConf","RetCode":0}
     * timestamp : 1565882004
     */
    private ParamsEntity params;


    public void setParams(ParamsEntity params) {
        this.params = params;
    }

    public ParamsEntity getParams() {
        return params;
    }


    public class ParamsEntity {
        /**
         * ToId : EC650274E91737D8DECACBA619E8AF83008F738390DE66B9270FB609E76D0F6C5F3E2B3D1BB6
         * Action : DelEmailConf
         * RetCode : 0
         */
        private String ToId;
        private String Action;
        private int RetCode;

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public void setRetCode(int RetCode) {
            this.RetCode = RetCode;
        }

        public String getToId() {
            return ToId;
        }

        public String getAction() {
            return Action;
        }

        public int getRetCode() {
            return RetCode;
        }
    }
}
