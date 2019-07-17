package com.smailnet.eamil;

import java.io.InputStream;

/**
 * @author jiangyw
 * @date 2019/1/10 21:14
 *
 * 邮件附件类
 */
public class MailAttachment {
    private String msgId;
    private String account;
    private String name;
    private InputStream inputStream;

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

    public MailAttachment(String name, InputStream inputStream,String msgId,String account) {
        this.msgId = msgId;
        this.account = account;
        this.name = name;
        this.inputStream = inputStream;
    }
}
