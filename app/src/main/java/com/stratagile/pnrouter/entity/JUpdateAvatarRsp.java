package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JUpdateAvatarRsp extends BaseEntity{


    /**
     * timestamp : 1552025064
     * params : {"Action":"UpdateAvatar","RetCode":0,"ToId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","FileSize":40977,"FileName":"/avatar/2rECyJDDVzzn1QQRGAnrqE6aiihUMRPaemKMsYnkoKSFYYssEuXycCVr5cEEkdRpeCgLoC2Hn9fw4","FileMD5":"c9411461cd3ea280da81c0fb0616ef74"}
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
         * RetCode : 0
         * ToId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * FileSize : 40977
         * FileName : /avatar/2rECyJDDVzzn1QQRGAnrqE6aiihUMRPaemKMsYnkoKSFYYssEuXycCVr5cEEkdRpeCgLoC2Hn9fw4
         * FileMD5 : c9411461cd3ea280da81c0fb0616ef74
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int FileSize;
        private String FileName;
        private String FileMD5;

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
    }
}
