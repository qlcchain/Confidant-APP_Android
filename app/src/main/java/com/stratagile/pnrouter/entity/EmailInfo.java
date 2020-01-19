package com.stratagile.pnrouter.entity;

import java.io.Serializable;

/**
 * Created by ZL on 2019/8/14.
 */

public class EmailInfo implements Serializable {
    // 自己公钥加密的key
    private String dsKey;
    // 标志
    private int flags;
    // 附件数量
    private int attchCount;
    // 标题
    private String subTitle;
    // 正文 截取前面 50个字符
    private String content;
    // 日期 秒时间搓
    private int revDate;
    // 发送人名字
    private String fromName;
    // 发送人邮箱
    private String fromEmailBox;
    // 收件人 名字 地址 json
    private String toUserJosn;
    // 抄送人 名字 地址 json
    private String ccUserJosn;
    // 密送人 名字 地址 json
    private String bccUserJosn;

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getAttchCount() {
        return attchCount;
    }

    public void setAttchCount(int attchCount) {
        this.attchCount = attchCount;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRevDate() {
        return revDate;
    }

    public void setRevDate(int revDate) {
        this.revDate = revDate;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromEmailBox() {
        return fromEmailBox;
    }

    public void setFromEmailBox(String fromEmailBox) {
        this.fromEmailBox = fromEmailBox;
    }

    public String getToUserJosn() {
        return toUserJosn;
    }

    public void setToUserJosn(String toUserJosn) {
        this.toUserJosn = toUserJosn;
    }

    public String getCcUserJosn() {
        return ccUserJosn;
    }

    public void setCcUserJosn(String ccUserJosn) {
        this.ccUserJosn = ccUserJosn;
    }

    public String getBccUserJosn() {
        return bccUserJosn;
    }

    public void setBccUserJosn(String bccUserJosn) {
        this.bccUserJosn = bccUserJosn;
    }
}

