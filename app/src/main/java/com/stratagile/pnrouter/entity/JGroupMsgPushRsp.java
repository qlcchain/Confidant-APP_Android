package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;

public class JGroupMsgPushRsp extends BaseEntity{


    /**
     * timestamp : 1553142757
     * params : {"Action":"GroupMsgPush","From":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","To":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","MsgType":0,"Point":0,"GId":"group3_admin13_time1553137433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","GroupName":"YWs0Nw==","GAdmin":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","MsgId":20,"TimeStamp":1553142757,"Msg":"VK8QFa5zyUmusv7r0+5BMw==","UserName":"aHc4ODg=","UserKey":"tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=","SelfKey":"zNACQmSdhRwgZOeYZD5yozesppBrkuMYYTcyFe7OYwZxELEA7SGElIm6NX6F62efLhUKkaoSW6aNt9ziMFUU49h4nUKFgah8oWBpK+L5eG0="}
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
         * Action : GroupMsgPush
         * From : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * To : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * MsgType : 0
         * Point : 0
         * GId : group3_admin13_time1553137433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * GroupName : YWs0Nw==
         * GAdmin : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * MsgId : 20
         * TimeStamp : 1553142757
         * Msg : VK8QFa5zyUmusv7r0+5BMw==
         * UserName : aHc4ODg=
         * UserKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
         * SelfKey : zNACQmSdhRwgZOeYZD5yozesppBrkuMYYTcyFe7OYwZxELEA7SGElIm6NX6F62efLhUKkaoSW6aNt9ziMFUU49h4nUKFgah8oWBpK+L5eG0=
         */

        private String Action;
        private String From;
        private String To;
        private int MsgType;
        private int Point;
        private String GId;
        private String GroupName;
        private String GAdmin;
        private int MsgId;
        private int TimeStamp;
        private String Msg;
        private String UserName;
        private String UserKey;
        private String SelfKey;

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getFrom() {
            return From;
        }

        public void setFrom(String From) {
            this.From = From;
        }

        public String getTo() {
            return To;
        }

        public void setTo(String To) {
            this.To = To;
        }

        public int getMsgType() {
            return MsgType;
        }

        public void setMsgType(int MsgType) {
            this.MsgType = MsgType;
        }

        public int getPoint() {
            return Point;
        }

        public void setPoint(int Point) {
            this.Point = Point;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public String getGroupName() {
            return GroupName;
        }

        public void setGroupName(String GroupName) {
            this.GroupName = GroupName;
        }

        public String getGAdmin() {
            return GAdmin;
        }

        public void setGAdmin(String GAdmin) {
            this.GAdmin = GAdmin;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int MsgId) {
            this.MsgId = MsgId;
        }

        public int getTimeStamp() {
            return TimeStamp;
        }

        public void setTimeStamp(int TimeStamp) {
            this.TimeStamp = TimeStamp;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getUserKey() {
            return UserKey;
        }

        public void setUserKey(String UserKey) {
            this.UserKey = UserKey;
        }

        public String getSelfKey() {
            return SelfKey;
        }

        public void setSelfKey(String SelfKey) {
            this.SelfKey = SelfKey;
        }
    }
}
