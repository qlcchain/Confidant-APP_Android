package com.stratagile.pnrouter.entity;

import com.google.gson.annotations.SerializedName;
import com.stratagile.pnrouter.db.GroupEntity;

import java.util.List;

public class JGroupListPullRsp extends BaseEntity{


    /**
     * timestamp : 1552898325
     * params : {"Action":"GroupListPull","RetCode":0,"ToId":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","GroupNum":1,"Payload":[{"GId":0,"GName":"dG93","GAdmin":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Remark":"","UserKey":"jJP7c5cPix4E7Bo5Y/GHybixUcyEhSWiPNzGvgrisX0H7qvQN/P7zX89YkB7VIpT+yrBoOcHCEH6NeSrooA6BnWyKE1ry0obY+G6oEwpxnI="}]}
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
         * Action : GroupListPull
         * RetCode : 0
         * ToId : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * GroupNum : 1
         * Payload : [{"GId":0,"GName":"dG93","GAdmin":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","Remark":"","UserKey":"jJP7c5cPix4E7Bo5Y/GHybixUcyEhSWiPNzGvgrisX0H7qvQN/P7zX89YkB7VIpT+yrBoOcHCEH6NeSrooA6BnWyKE1ry0obY+G6oEwpxnI="}]
         */

        private String Action;
        private int RetCode;
        private String ToId;
        private int GroupNum;
        private List<GroupEntity> Payload;

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

        public int getGroupNum() {
            return GroupNum;
        }

        public void setGroupNum(int GroupNum) {
            this.GroupNum = GroupNum;
        }

        public List<GroupEntity> getPayload() {
            return Payload;
        }

        public void setPayload(List<GroupEntity> Payload) {
            this.Payload = Payload;
        }


    }
}
