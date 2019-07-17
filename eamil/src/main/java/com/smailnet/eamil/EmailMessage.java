/*
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smailnet.eamil;

import java.util.List;

/**
 * 邮件内容集合
 */
public class EmailMessage {

    private String id;
    private String subject;
    private String from;
    private String to;//收件人
    private String cc;//抄送
    private String bcc;//密送
    private String date;
    private boolean isSeen;
    private String priority;
    private boolean isReplySign;
    private long size;
    private boolean isContainerAttachment;
    private int attachmentCount;
    private String content;
    private String contentText;
    private List<MailAttachment> mailAttachmentList;


    public EmailMessage()
    {

    }

    public   EmailMessage(String id, String subject, String from, String to, String cc, String bcc, String date, boolean isSeen, String priority, boolean isReplySign, long size, boolean isContainerAttachment, int attachmentCount, String content,String contentText) {
        this.id = id;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.date = date;
        this.isSeen = isSeen;
        this.priority = priority;
        this.isReplySign = isReplySign;
        this.size = size;
        this.isContainerAttachment = isContainerAttachment;
        this.attachmentCount = attachmentCount;
        this.content = content;
        this.contentText = contentText;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isReplySign() {
        return isReplySign;
    }

    public void setReplySign(boolean replySign) {
        isReplySign = replySign;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isContainerAttachment() {
        return isContainerAttachment;
    }

    public void setContainerAttachment(boolean containerAttachment) {
        isContainerAttachment = containerAttachment;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public List<MailAttachment> getMailAttachmentList() {
        return mailAttachmentList;
    }

    public void setMailAttachmentList(List<MailAttachment> mailAttachmentList) {
        this.mailAttachmentList = mailAttachmentList;
    }
}
