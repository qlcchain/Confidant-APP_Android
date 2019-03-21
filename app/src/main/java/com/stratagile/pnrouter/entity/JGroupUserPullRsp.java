package com.stratagile.pnrouter.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JGroupUserPullRsp extends BaseEntity{


    /**
     * timestamp : 1553065392
     * params : {"Action":"GroupUserPull","RetCode":0,"ToId":"8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF","UserNum":2,"Payload":[{"Id":22,"Type":2,"Local":1,"ToxId":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","Nickname":"aHc4ODg=","Remarks":"","UserKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4="},{"Id":13,"Type":2,"Local":1,"ToxId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Nickname":"cmVkODg4","Remarks":"","UserKey":"QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA="}]}
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
         * Action : GroupUserPull
         * RetCode : 0
         * ToId : 8EAFEFA958FF15A10C5DFF698948987EB1C33F7A6AD4161DC53A7FD20F5B997EF5EBADB348BF
         * UserNum : 2
         * Payload : [{"Id":22,"Type":2,"Local":1,"ToxId":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","Nickname":"aHc4ODg=","Remarks":"","UserKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4="},{"Id":13,"Type":2,"Local":1,"ToxId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Nickname":"cmVkODg4","Remarks":"","UserKey":"QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA="}]
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int UserNum;
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

        public String getToId() {
            return ToId;
        }

        public void setToId(String ToId) {
            this.ToId = ToId;
        }

        public int getUserNum() {
            return UserNum;
        }

        public void setUserNum(int UserNum) {
            this.UserNum = UserNum;
        }

        public List<PayloadBean> getPayload() {
            return Payload;
        }

        public void setPayload(List<PayloadBean> Payload) {
            this.Payload = Payload;
        }

        public static class PayloadBean implements Parcelable {
            public PayloadBean() {
            }

            /**
             * Id : 22
             * Type : 2
             * Local : 1
             * ToxId : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
             * Nickname : aHc4ODg=
             * Remarks :
             * UserKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
             */

            private int Id;
            private int Type;
            private int Local;
            private String ToxId;
            private String Nickname;
            private String Remarks;
            private String UserKey;

            protected PayloadBean(Parcel in) {
                Id = in.readInt();
                Type = in.readInt();
                Local = in.readInt();
                ToxId = in.readString();
                Nickname = in.readString();
                Remarks = in.readString();
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

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public int getType() {
                return Type;
            }

            public void setType(int Type) {
                this.Type = Type;
            }

            public int getLocal() {
                return Local;
            }

            public void setLocal(int Local) {
                this.Local = Local;
            }

            public String getToxId() {
                return ToxId;
            }

            public void setToxId(String ToxId) {
                this.ToxId = ToxId;
            }

            public String getNickname() {
                return Nickname;
            }

            public void setNickname(String Nickname) {
                this.Nickname = Nickname;
            }

            public String getRemarks() {
                return Remarks;
            }

            public void setRemarks(String Remarks) {
                this.Remarks = Remarks;
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
                parcel.writeInt(Id);
                parcel.writeInt(Type);
                parcel.writeInt(Local);
                parcel.writeString(ToxId);
                parcel.writeString(Nickname);
                parcel.writeString(Remarks);
                parcel.writeString(UserKey);
            }
        }
    }
}
