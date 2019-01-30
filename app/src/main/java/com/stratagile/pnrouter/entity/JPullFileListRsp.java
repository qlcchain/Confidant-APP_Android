package com.stratagile.pnrouter.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JPullFileListRsp extends BaseEntity {

    /**
     * timestamp : 1548732819
     * params : {"Action":"PullFileList","RetCode":0,"FileNum":2,"Payload":[{"MsgId":43,"Timestamp":1548732669,"FileType":1,"FileName":"/user7/u/AeqbC4ECNWYuePyboXGYx2N1dAA9SQDz2sJzjjChK2g1K4FZs5jy8RJVchTCYDDev2ZvQcg75K1PHka6Nr4HAjZj4","FileMD5":"48e5f1a0bba1ab7084cc5278889db08a","FileSize":481456,"UserKey":"xTHLF5LEx62zeyr+cAvzYblqDeCQIcfONXyzb8xgnV+4UQDbuOlVSn/skaARNuJX5wu5ugwH3058u0oUwSleSCRMie6CjrTCc2ZBFNlmL9Y="},{"MsgId":42,"Timestamp":1548732533,"FileType":1,"FileName":"/user7/u/AeqbC4ECNWYuePyboXGYx2N1dAA9SQDz2sJzjjChHhSjJdkbA5Zt6USoRdpGXahLNB6H4nPSdTtuahoA2vPq5m3ux","FileMD5":"ea613421e1e91e56b0c31451d8ab75eb","FileSize":691472,"UserKey":"CiPEBTxRoxzCbp1ZSvoF0wJu/gBOg+40xy6Cwi8d2QdUToAdNCSxudtcmx4RZsHCxMdWsIT94QMHvj2mnJ3AsTmEJTdz5+OiYeG1qKHuShY="}]}
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
         * FileNum : 2
         * Payload : [{"MsgId":43,"Timestamp":1548732669,"FileType":1,"FileName":"/user7/u/AeqbC4ECNWYuePyboXGYx2N1dAA9SQDz2sJzjjChK2g1K4FZs5jy8RJVchTCYDDev2ZvQcg75K1PHka6Nr4HAjZj4","FileMD5":"48e5f1a0bba1ab7084cc5278889db08a","FileSize":481456,"UserKey":"xTHLF5LEx62zeyr+cAvzYblqDeCQIcfONXyzb8xgnV+4UQDbuOlVSn/skaARNuJX5wu5ugwH3058u0oUwSleSCRMie6CjrTCc2ZBFNlmL9Y="},{"MsgId":42,"Timestamp":1548732533,"FileType":1,"FileName":"/user7/u/AeqbC4ECNWYuePyboXGYx2N1dAA9SQDz2sJzjjChHhSjJdkbA5Zt6USoRdpGXahLNB6H4nPSdTtuahoA2vPq5m3ux","FileMD5":"ea613421e1e91e56b0c31451d8ab75eb","FileSize":691472,"UserKey":"CiPEBTxRoxzCbp1ZSvoF0wJu/gBOg+40xy6Cwi8d2QdUToAdNCSxudtcmx4RZsHCxMdWsIT94QMHvj2mnJ3AsTmEJTdz5+OiYeG1qKHuShY="}]
         */

        private String Action;
        private int RetCode;
        private int FileNum;
        private ArrayList<PayloadBean> Payload;

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

        public ArrayList<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(ArrayList<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean implements Parcelable {
            /**
             * MsgId : 43
             * Timestamp : 1548732669
             * FileType : 1
             * FileName : /user7/u/AeqbC4ECNWYuePyboXGYx2N1dAA9SQDz2sJzjjChK2g1K4FZs5jy8RJVchTCYDDev2ZvQcg75K1PHka6Nr4HAjZj4
             * FileMD5 : 48e5f1a0bba1ab7084cc5278889db08a
             * FileSize : 481456
             * UserKey : xTHLF5LEx62zeyr+cAvzYblqDeCQIcfONXyzb8xgnV+4UQDbuOlVSn/skaARNuJX5wu5ugwH3058u0oUwSleSCRMie6CjrTCc2ZBFNlmL9Y=
             */

            private int MsgId;
            private int Timestamp;
            private int FileType;
            private String FileName;
            private String FileMD5;
            private int FileSize;
            private String UserKey;

            protected PayloadBean(Parcel in) {
                MsgId = in.readInt();
                Timestamp = in.readInt();
                FileType = in.readInt();
                FileName = in.readString();
                FileMD5 = in.readString();
                FileSize = in.readInt();
                UserKey = in.readString();
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

            public String getUserKey() {
                return UserKey;
            }

            public void setUserKey(String UserKey) {
                this.UserKey = UserKey;
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
                parcel.writeString(UserKey);
            }
        }
    }
}
