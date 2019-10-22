package com.smailnet.eamil;

import java.io.InputStream;

/**
 * @author zl
 * @date 2019/1/10 21:14
 *
 * 邮件附件类
 */
public class MailAttachment {
    private String msgId;
    private String account;
    private String name;
    private InputStream inputStream;
    private byte[] byt;
    private String cid;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] getByt() {
        return byt;
    }

    public void setByt(byte[] byt) {
        this.byt = byt;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public MailAttachment(String name, InputStream inputStream, byte[] byt, String msgId, String account, String cid) {
        this.msgId = msgId;
        this.account = account;
        this.name = name;
        this.inputStream = inputStream;
        this.byt = byt;
        this.cid = cid;
    }
}
