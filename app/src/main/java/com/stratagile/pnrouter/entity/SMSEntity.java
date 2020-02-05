package com.stratagile.pnrouter.entity;

import java.io.Serializable;

/**
 * Created by ZL on 2020/2/5.
 */

public class SMSEntity implements Serializable {
    // 电话
    private String address;
    // 联系人位置
    private int person;
    // 时间
    private int date;
    // 是否阅读
    private int read;
    // 消息类型
    private int type;
    // 主题
    private String subject;
    // 内容
    private String body;
    // 短信服务号
    private int service_center;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getService_center() {
        return service_center;
    }

    public void setService_center(int service_center) {
        this.service_center = service_center;
    }
}

