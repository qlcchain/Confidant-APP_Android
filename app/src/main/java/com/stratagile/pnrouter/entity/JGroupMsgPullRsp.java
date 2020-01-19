package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;
import com.message.Message;

import java.util.List;

public class JGroupMsgPullRsp extends BaseEntity{


    /**
     * timestamp : 1552976817
     * params : {"Action":"GroupMsgPull","RetCode":0,"UserId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","GId":"group3_admin13_time1552901378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","MsgNum":1,"Payload":[{"MsgId":1,"MsgType":0,"TimeStatmp":1552975302,"Sender":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Msg":"oSjhe64OlszDcCU2TyYp7Q==","Point":0,"FilePath":"","FileInfo":""}]}
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
         * Action : GroupMsgPull
         * RetCode : 0
         * UserId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * GId : group3_admin13_time1552901378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * MsgNum : 1
         * Payload : [{"MsgId":1,"MsgType":0,"TimeStatmp":1552975302,"Sender":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Msg":"oSjhe64OlszDcCU2TyYp7Q==","Point":0,"FilePath":"","FileInfo":""}]
         */

        private String Action;
        private int RetCode;
        private String UserId;
        private String GId;
        private int MsgNum;
        private int SrcMsgId;
        private List<Message> Payload;

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

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getGId() {
            return GId;
        }

        public void setGId(String GId) {
            this.GId = GId;
        }

        public int getMsgNum() {
            return MsgNum;
        }

        public void setMsgNum(int MsgNum) {
            this.MsgNum = MsgNum;
        }

        public int getSrcMsgId() {
            return SrcMsgId;
        }

        public void setSrcMsgId(int srcMsgId) {
            SrcMsgId = srcMsgId;
        }

        public List<Message> getPayload() {
            return Payload;
        }

        public void setPayload(List<Message> Payload) {
            this.Payload = Payload;
        }

    }
}
