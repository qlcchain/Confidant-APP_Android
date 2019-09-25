package com.stratagile.pnrouter.entity;

public class JGroupMsgPushRsp extends BaseEntity{


    /**
     * timestamp : 1553161871
     * params : {"Action":"GroupMsgPush","From":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","To":"FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E","MsgType":1,"Point":0,"GId":"group3_admin13_time1553137433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA","GroupName":"YWs0Nw==","GAdmin":"BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305","MsgId":30,"TimeStamp":0,"UserName":"cmVkODg4","UserKey":"QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA=","SelfKey":"W+kzr4115WKrXmzZFsY/V2DYp3cZg7a+JBLrixdtuW9E078deWykvIMJjDKRJNyCQQ2SEgvl8n2SalBGaOrBUjDQjncvW4nvLH7kqmPbcPY=","FileName":"pdhRoMhLjLo8ga9iHBm6vyAq9ZUfxWwtMWHXXhewgkWgBqkN6mHPAiLP8","FilePath":"/gpdata/g3/pdhRoMhLjLo8ga9iHBm6vyAq9ZUfxWwtMWHXXhewgkWgBqkN6mHPAiLP8","FileMD5":"df5389c0f33fa7457335b998ac53fa97","FileSize":8384,"FileInfo":",132.0000000*132.0000000"}
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
         * From : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * To : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
         * MsgType : 1
         * Point : 0
         * GId : group3_admin13_time1553137433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
         * GroupName : YWs0Nw==
         * GAdmin : BEDAC3BF12F9F7AD55BE741F98232D64267821CB1E028F62D9C8930216B8557D6C82EC8C1305
         * MsgId : 30
         * TimeStamp : 0
         * UserName : cmVkODg4
         * UserKey : QuBYY+vr4BGOpuIBtLx0jIRydh9d9QeeHRAM54aPunA=
         * SelfKey : W+kzr4115WKrXmzZFsY/V2DYp3cZg7a+JBLrixdtuW9E078deWykvIMJjDKRJNyCQQ2SEgvl8n2SalBGaOrBUjDQjncvW4nvLH7kqmPbcPY=
         * FileName : pdhRoMhLjLo8ga9iHBm6vyAq9ZUfxWwtMWHXXhewgkWgBqkN6mHPAiLP8
         * FilePath : /gpdata/g3/pdhRoMhLjLo8ga9iHBm6vyAq9ZUfxWwtMWHXXhewgkWgBqkN6mHPAiLP8
         * FileMD5 : df5389c0f33fa7457335b998ac53fa97
         * FileSize : 8384
         * FileInfo : ,132.0000000*132.0000000
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
        private String UserName;
        private String UserKey;
        private String SelfKey;
        private String FileName;
        private String FilePath;
        private String FileMD5;
        private int FileSize;
        private String FileInfo;
        private String Msg;
        private String FileKey;//从文件转发过来的消息特有
        private int AssocId;
        private String AssocMsgInfo;

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

        public String getFileName() {
            return FileName;
        }

        public void setFileName(String FileName) {
            this.FileName = FileName;
        }

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
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

        public String getFileInfo() {
            return FileInfo;
        }

        public void setFileInfo(String FileInfo) {
            this.FileInfo = FileInfo;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        public String getFileKey() {
            return FileKey;
        }

        public void setFileKey(String fileKey) {
            FileKey = fileKey;
        }

        public int getAssocId() {
            return AssocId;
        }

        public void setAssocId(int assocId) {
            AssocId = assocId;
        }

        public String getAssocMsgInfo() {
            return AssocMsgInfo;
        }

        public void setAssocMsgInfo(String assocMsgInfo) {
            AssocMsgInfo = assocMsgInfo;
        }
    }
}
