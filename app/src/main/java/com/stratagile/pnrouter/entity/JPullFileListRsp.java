package com.stratagile.pnrouter.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JPullFileListRsp extends BaseEntity {

    /**
     * timestamp : 1548920984
     * params : {"Action":"PullFileList","RetCode":0,"FileNum":5,"Payload":[{"MsgId":82,"Timestamp":1548917023,"FileType":4,"FileName":"/user7/u/hDdeXdJfNaNjKdEFy6s","FileMD5":"b21e3561dd8455d98a994df1e7d7c558","FileSize":2868752,"Sender":"","UserKey":"LeAre9fdAcCqaEbQe9zGP4JsO5WaAnX8x2RL72XEtja1NKNMJlGzWuAF790aiFbBue+GqBbqtgQ91TB3aiu7ofwlGkkcsfpfbs86lVdC7ZU=","FileFrom":3},{"MsgId":81,"Timestamp":1548916185,"FileType":4,"FileName":"/user7/u/92MxFZ7ZW4oWjg8Q9iqcpsq5kNemp3RorvsyNed6czagbnHZ","FileMD5":"d64b2d9c61e97fdc8983ff399abeb615","FileSize":42632208,"Sender":"","UserKey":"BVDEip7NIFfMDjh18XsVwzzbr/Sv4XPKjj0EViDGZX4iphw5hQfcMkmGVdZHIpO2IS1Ay8SWN8vKX8LXlvO1Mu2vWTKYogr6L5/fDFBpfX8=","FileFrom":3},{"MsgId":80,"Timestamp":1548916155,"FileType":4,"FileName":"/user7/u/92MxFZ7ZW4oWjg8Q9iqcpsq5kNemp3RorvsvBM92pFPpV4Mq","FileMD5":"033316ad1f417b2d73dff884f095dfe8","FileSize":42540224,"Sender":"","UserKey":"JDe2ofraDKEIvGdaM06sfXI8v3WlUZULtbLU99e4hnCU/Yw4xKu4VyAYSONUML6Lz1QaJvNZFuQwXfQzL5jCIhcVSNdDixw9DxoBC9Gpmns=","FileFrom":3},{"MsgId":79,"Timestamp":1548905183,"FileType":4,"FileName":"/user7/u/3Z1UGiMAji5PcwkKjSYcTS41SrcWuhyvjyGF","FileMD5":"8cf2e737fd0f99e052f6b0423f83ea71","FileSize":1571552,"Sender":"","UserKey":"voDJaQ6YEWFVgEhjO8ZVZfYlWpkqVhNbtbHsLAIG0xbXKzDEVdoLTCcO+fzGo2P74h3cipMP+ETZCZIhdxgTSRIeTuxPCuleN8f74alnI7s=","FileFrom":3},{"MsgId":78,"Timestamp":1548904083,"FileType":4,"FileName":"/user7/u/EMd3XiSeCKb6sgTmHVBAgoWycrQQz7","FileMD5":"95d77a622eb961c4d002a1b18397d2fa","FileSize":7347536,"Sender":"","UserKey":"PQgp6o9W8JEGhQOsCAvKOb6VV6G89g/4K44NRWtl4QMJ7K7C7n62o6HjqaEuX0rk4c68JntajfeRS+BZF4WvhvlTHCv2DTfpWWIr/A/Oemo=","FileFrom":3}]}
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
         * Action : PullFileList
         * RetCode : 0
         * FileNum : 5
         * Payload : [{"MsgId":82,"Timestamp":1548917023,"FileType":4,"FileName":"/user7/u/hDdeXdJfNaNjKdEFy6s","FileMD5":"b21e3561dd8455d98a994df1e7d7c558","FileSize":2868752,"Sender":"","UserKey":"LeAre9fdAcCqaEbQe9zGP4JsO5WaAnX8x2RL72XEtja1NKNMJlGzWuAF790aiFbBue+GqBbqtgQ91TB3aiu7ofwlGkkcsfpfbs86lVdC7ZU=","FileFrom":3},{"MsgId":81,"Timestamp":1548916185,"FileType":4,"FileName":"/user7/u/92MxFZ7ZW4oWjg8Q9iqcpsq5kNemp3RorvsyNed6czagbnHZ","FileMD5":"d64b2d9c61e97fdc8983ff399abeb615","FileSize":42632208,"Sender":"","UserKey":"BVDEip7NIFfMDjh18XsVwzzbr/Sv4XPKjj0EViDGZX4iphw5hQfcMkmGVdZHIpO2IS1Ay8SWN8vKX8LXlvO1Mu2vWTKYogr6L5/fDFBpfX8=","FileFrom":3},{"MsgId":80,"Timestamp":1548916155,"FileType":4,"FileName":"/user7/u/92MxFZ7ZW4oWjg8Q9iqcpsq5kNemp3RorvsvBM92pFPpV4Mq","FileMD5":"033316ad1f417b2d73dff884f095dfe8","FileSize":42540224,"Sender":"","UserKey":"JDe2ofraDKEIvGdaM06sfXI8v3WlUZULtbLU99e4hnCU/Yw4xKu4VyAYSONUML6Lz1QaJvNZFuQwXfQzL5jCIhcVSNdDixw9DxoBC9Gpmns=","FileFrom":3},{"MsgId":79,"Timestamp":1548905183,"FileType":4,"FileName":"/user7/u/3Z1UGiMAji5PcwkKjSYcTS41SrcWuhyvjyGF","FileMD5":"8cf2e737fd0f99e052f6b0423f83ea71","FileSize":1571552,"Sender":"","UserKey":"voDJaQ6YEWFVgEhjO8ZVZfYlWpkqVhNbtbHsLAIG0xbXKzDEVdoLTCcO+fzGo2P74h3cipMP+ETZCZIhdxgTSRIeTuxPCuleN8f74alnI7s=","FileFrom":3},{"MsgId":78,"Timestamp":1548904083,"FileType":4,"FileName":"/user7/u/EMd3XiSeCKb6sgTmHVBAgoWycrQQz7","FileMD5":"95d77a622eb961c4d002a1b18397d2fa","FileSize":7347536,"Sender":"","UserKey":"PQgp6o9W8JEGhQOsCAvKOb6VV6G89g/4K44NRWtl4QMJ7K7C7n62o6HjqaEuX0rk4c68JntajfeRS+BZF4WvhvlTHCv2DTfpWWIr/A/Oemo=","FileFrom":3}]
         */

        private String Action;
        private int RetCode;
        private int FileNum;
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

        public int getFileNum() {
            return FileNum;
        }

        public void setFileNum(int FileNum) {
            this.FileNum = FileNum;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean implements Parcelable {
            /**
             * MsgId : 82
             * Timestamp : 1548917023
             * FileType : 4
             * FileName : /user7/u/hDdeXdJfNaNjKdEFy6s
             * FileMD5 : b21e3561dd8455d98a994df1e7d7c558
             * FileSize : 2868752
             * Sender :
             * UserKey : LeAre9fdAcCqaEbQe9zGP4JsO5WaAnX8x2RL72XEtja1NKNMJlGzWuAF790aiFbBue+GqBbqtgQ91TB3aiu7ofwlGkkcsfpfbs86lVdC7ZU=
             * FileFrom : 3
             */

            private int MsgId;
            private int Timestamp;
            private int FileType;
            private String FileName;
            private String FileMD5;
            private int FileSize;
            private String Sender;
            private String UserKey;
            //1 自己发的， 2 收到的， 3自己上传的
            private int FileFrom;
            public String SenderKey;

            protected PayloadBean(Parcel in) {
                MsgId = in.readInt();
                Timestamp = in.readInt();
                FileType = in.readInt();
                FileName = in.readString();
                FileMD5 = in.readString();
                FileSize = in.readInt();
                Sender = in.readString();
                UserKey = in.readString();
                SenderKey = in.readString();
                FileFrom = in.readInt();
            }

            public static final Creator<PayloadBean> CREATOR = new Creator<PayloadBean>() {
                @Override
                public PayloadBean createFromParcel(Parcel in) {
                    return new PayloadBean(in);
                }

                @Override
                public PayloadBean[] newArray(int size) {
                    return new PayloadBean[size];
                }
            };

            public int getMsgId() {
                return MsgId;
            }

            public void setMsgId(int MsgId) {
                this.MsgId = MsgId;
            }

            public int getTimestamp() {
                return Timestamp;
            }

            public void setTimestamp(int Timestamp) {
                this.Timestamp = Timestamp;
            }

            public int getFileType() {
                return FileType;
            }

            public void setFileType(int FileType) {
                this.FileType = FileType;
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

            public int getFileSize() {
                return FileSize;
            }

            public void setFileSize(int FileSize) {
                this.FileSize = FileSize;
            }

            public String getSender() {
                return Sender;
            }

            public void setSender(String Sender) {
                this.Sender = Sender;
            }

            public String getUserKey() {
                return UserKey;
            }

            public void setUserKey(String UserKey) {
                this.UserKey = UserKey;
            }

            public int getFileFrom() {
                return FileFrom;
            }

            public void setFileFrom(int FileFrom) {
                this.FileFrom = FileFrom;
            }

            public String getSenderKey() {
                return SenderKey;
            }

            public void setSenderKey(String senderKey) {
                SenderKey = senderKey;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(MsgId);
                parcel.writeInt(Timestamp);
                parcel.writeInt(FileType);
                parcel.writeString(FileName);
                parcel.writeString(FileMD5);
                parcel.writeInt(FileSize);
                parcel.writeString(Sender);
                parcel.writeString(UserKey);
                parcel.writeString(SenderKey);
                parcel.writeInt(FileFrom);
            }
        }
    }
}
