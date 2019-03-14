package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JUpdateAvatarRsp extends BaseEntity{


    /**
     * timestamp : 1552553791
     * params : {"Action":"UpdateAvatar","RetCode":2,"ToId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","FileSize":40977,"FileName":"/avatar/2rECyJDDVzzn1QQRGAnrqE6aiihUMRPaemKMsYnkoKSFYYssEuXycCVr5cEEkdRpeCgLoC2Hn9fw4","FileMD5":"c9411461cd3ea280da81c0fb0616ef74","TargetKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=","TargetId":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E"}
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
         * Action : UpdateAvatar
         * RetCode : 2
         * ToId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * FileSize : 40977
         * FileName : /avatar/2rECyJDDVzzn1QQRGAnrqE6aiihUMRPaemKMsYnkoKSFYYssEuXycCVr5cEEkdRpeCgLoC2Hn9fw4
         * FileMD5 : c9411461cd3ea280da81c0fb0616ef74
         * TargetKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
         * TargetId : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int FileSize;
        private String FileName;
        private String FileMD5;
        private String TargetKey;
        private String TargetId;

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

        public int getFileSize() {
            return FileSize;
        }

        public void setFileSize(int FileSize) {
            this.FileSize = FileSize;
        }

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }

        public String getFileMD5() {
            return FileMD5;
        }

        public void setFileMD5(String FileMD5) {
            this.FileMD5 = FileMD5;
        }

        public String getTargetKey() {
            return TargetKey;
        }

        public void setTargetKey(String TargetKey) {
            this.TargetKey = TargetKey;
        }

        public String getTargetId() {
            return TargetId;
        }

        public void setTargetId(String TargetId) {
            this.TargetId = TargetId;
        }
    }
}
