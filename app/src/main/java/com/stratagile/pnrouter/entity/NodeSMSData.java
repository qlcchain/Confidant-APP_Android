package com.stratagile.pnrouter.entity;

import java.io.Serializable;


public class NodeSMSData implements Serializable {

    // 节点ID
    private Integer Index;
    // 电话
    private String Tel;
    //短信数量
    private Integer Num;

    private Integer Id;
    // 时间
    private Long Time;

    // 是否阅读
    private Integer Read;
    // 消息类型
    private Integer Send;
    // 联系人
    private String User;
    // 主题
    private String Title;

    // 内容
    private String Cont;
    // 加密
    private String Key;
    // 联系人位置
    private Integer Uid;


    public Integer getIndex() {
        return Index;
    }

    public void setIndex(Integer index) {
        Index = index;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public Integer getNum() {
        return Num;
    }

    public void setNum(Integer num) {
        Num = num;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Long getTime() {
        return Time;
    }

    public void setTime(Long time) {
        Time = time;
    }

    public Integer getRead() {
        return Read;
    }

    public void setRead(Integer read) {
        Read = read;
    }

    public Integer getSend() {
        return Send;
    }

    public void setSend(Integer send) {
        Send = send;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCont() {
        return Cont;
    }

    public void setCont(String cont) {
        Cont = cont;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Integer getUid() {
        return Uid;
    }

    public void setUid(Integer uid) {
        Uid = uid;
    }
}
