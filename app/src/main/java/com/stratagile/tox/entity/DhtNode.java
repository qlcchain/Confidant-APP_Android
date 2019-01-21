package com.stratagile.tox.entity;

import java.util.List;

public class DhtNode {

    /**
     * ipv4 : node.tox.biribiri.org
     * ipv6 : -
     * port : 33445
     * tcp_ports : [3389,33445]
     * public_key : F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67
     * maintainer : nurupo
     * location : US
     * status_udp : true
     * status_tcp : true
     * version : 1000002008
     * motd : Welcome, stranger #6250. I'm up for 4d 08h 13m 22s, running since Dec 28 23:00:41 UTC. If I get outdated, please ping my maintainer at nurupo.contributions@gmail.com
     * last_ping : 1546413243
     */

    private String ipv4;
    private String ipv6;
    private int port;
    private String public_key;
    private String maintainer;
    private String location;
    private boolean status_udp;
    private boolean status_tcp;
    private String version;
    private String motd;
    private int last_ping;
    private List<Integer> tcp_ports;

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isStatus_udp() {
        return status_udp;
    }

    public void setStatus_udp(boolean status_udp) {
        this.status_udp = status_udp;
    }

    public boolean isStatus_tcp() {
        return status_tcp;
    }

    public void setStatus_tcp(boolean status_tcp) {
        this.status_tcp = status_tcp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getLast_ping() {
        return last_ping;
    }

    public void setLast_ping(int last_ping) {
        this.last_ping = last_ping;
    }

    public List<Integer> getTcp_ports() {
        return tcp_ports;
    }

    public void setTcp_ports(List<Integer> tcp_ports) {
        this.tcp_ports = tcp_ports;
    }
}
