package com.stratagile.pnrouter.message;

public class Message {

    /**
     * MsgId : 1537262116
     * MsgType : 1
     * TimeStatmp : 1537262116
     * From : EEA02E58D797E4C2D34AA5727A5547FD415A21AFD255CE4825F05836FC1D0267ACF17C109788
     * To : 14EB061F2A983B966B79030AF773AE74BE703315A4E56EA9D801DAC5DC840522C62EA32ECBC5
     * Msg : 。。。
     */

    private int MsgId;
    private int MsgType;
    private long TimeStatmp;
    private String From;
    private String To;
    private String Msg;

    public int getMsgId() {
        return MsgId;
    }

    public void setMsgId(int MsgId) {
        this.MsgId = MsgId;
    }

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int MsgType) {
        this.MsgType = MsgType;
    }

    public long getTimeStatmp() {
        return TimeStatmp;
    }

    public void setTimeStatmp(long TimeStatmp) {
        this.TimeStatmp = TimeStatmp;
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

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }
}
