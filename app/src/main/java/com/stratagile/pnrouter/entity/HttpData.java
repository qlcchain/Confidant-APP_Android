package com.stratagile.pnrouter.entity;

public class HttpData {


    /**
     * RetCode : 0
     * ConnStatus : 1
     * ServerPort : 10010
     * Rid : F1778E83EEDD84096D9C1F79E7E8AC62838E47BA66484E15467B7E74838910039533346DCAFD
     * ServerHost : 47.96.76.184
     * Info : conntype frp_proxy
     */

    private int RetCode;
    private int ConnStatus;
    private int ServerPort;

    @Override
    public String toString() {
        return "HttpData{" +
                "RetCode=" + RetCode +
                ", ConnStatus=" + ConnStatus +
                ", ServerPort=" + ServerPort +
                ", Rid='" + Rid + '\'' +
                ", ServerHost='" + ServerHost + '\'' +
                ", Info='" + Info + '\'' +
                '}';
    }

    private String Rid;
    private String ServerHost;
    private String Info;

    public int getRetCode() {
        return RetCode;
    }

    public void setRetCode(int RetCode) {
        this.RetCode = RetCode;
    }

    public int getConnStatus() {
        return ConnStatus;
    }

    public void setConnStatus(int ConnStatus) {
        this.ConnStatus = ConnStatus;
    }

    public int getServerPort() {
        return ServerPort;
    }

    public void setServerPort(int ServerPort) {
        this.ServerPort = ServerPort;
    }

    public String getRid() {
        return Rid;
    }

    public void setRid(String Rid) {
        this.Rid = Rid;
    }

    public String getServerHost() {
        return ServerHost;
    }

    public void setServerHost(String ServerHost) {
        this.ServerHost = ServerHost;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String Info) {
        this.Info = Info;
    }
}
