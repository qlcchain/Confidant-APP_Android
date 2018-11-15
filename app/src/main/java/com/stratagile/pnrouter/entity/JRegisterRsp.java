package com.stratagile.pnrouter.entity;

public class JRegisterRsp extends BaseEntity {


    /**
     * params : {"Action":"Register","RetCode":0,"RouteId":"D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472","UserSn":"02000001B827EBD089CB00005BEBF3BF","UserId":"7132FDA46AE177488108B82C0BEEE62CFA42E4E5ADCD087462DCE2CD865CF44CA46BDBD97374","dataFileVersion":0,"DataFilePay":"7132FDA46AE177488108B82C0BEEE62CFA42E4E5ADCD087462DCE2CD865CF44CA46BDBD97374"}
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
         * Action : Register
         * RetCode : 0
         * RouteId : D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472
         * UserSn : 02000001B827EBD089CB00005BEBF3BF
         * UserId : 7132FDA46AE177488108B82C0BEEE62CFA42E4E5ADCD087462DCE2CD865CF44CA46BDBD97374
         * dataFileVersion : 0
         * DataFilePay : 7132FDA46AE177488108B82C0BEEE62CFA42E4E5ADCD087462DCE2CD865CF44CA46BDBD97374
         */

        private String Action;
        private int RetCode;
        private String RouteId;
        private String UserSn;
        private String UserId;
        private int DataFileVersion;
        private String DataFilePay;

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

        public String getRouteId() {
            return RouteId;
        }

        public void setRouteId(String RouteId) {
            this.RouteId = RouteId;
        }

        public String getUserSn() {
            return UserSn;
        }

        public void setUserSn(String UserSn) {
            this.UserSn = UserSn;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public int getDataFileVersion() {
            return DataFileVersion;
        }

        public void setDataFileVersion(int DataFileVersion) {
            this.DataFileVersion = DataFileVersion;
        }

        public String getDataFilePay() {
            return DataFilePay;
        }

        public void setDataFilePay(String DataFilePay) {
            this.DataFilePay = DataFilePay;
        }
    }
}
