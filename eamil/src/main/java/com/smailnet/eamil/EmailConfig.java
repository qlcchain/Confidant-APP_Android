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

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author
 * @author
 * @version 2.3
 */
public class EmailConfig {

    private String emailType;       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱
    private int smtpPort;           //SMTP端口
    private int popPort;            //POP端口
    private int imapPort;           //IMAP端口
    private String smtpHost;        //SMTP的Host
    private String popHost;         //POP的Host
    private String imapHost;        //IMAP的Host
    private String account;         //邮箱帐号
    private String password;        //邮箱密码
    private String name;            //昵称
    private String imapEncrypted;    //加密类型
    private String smtpEncrypted;    //加密类型

    public String getEmailType() {
        return emailType;
    }

    public EmailConfig setEmailType(String emailType) {
        this.emailType = emailType;
        return this;
    }

    public EmailConfig setSmtpHost(String smtpHost){
        this.smtpHost = smtpHost;
        return this;
    }

    public EmailConfig setPopHost(String popHost){
        this.popHost = popHost;
        return this;
    }

    public EmailConfig setImapHost(String imapHost){
        this.imapHost = imapHost;
        return this;
    }

    public EmailConfig setSmtpPort(int smtpPort){
       this.smtpPort = smtpPort;
       return this;
    }

    public EmailConfig setPopPort(int popPort){
        this.popPort = popPort;
        return this;
    }

    public EmailConfig setImapPort(int imapPort){
        this.imapPort = imapPort;
        return this;
    }

    public EmailConfig setAccount(String account){
        this.account = account;
        return this;
    }

    public EmailConfig setPassword(String password){
        this.password = password;
        return this;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getPopHost() {
        return popHost;
    }

    public int getPopPort() {
        return popPort;
    }

    public String getImapHost(){
        return  imapHost;
    }

    public int getImapPort(){
        return imapPort;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public EmailConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getImapEncrypted() {
        return imapEncrypted;
    }

    public EmailConfig setImapEncrypted(String imapEncrypted) {
        this.imapEncrypted = imapEncrypted;
        return this;
    }

    public String getSmtpEncrypted() {
        return smtpEncrypted;
    }

    public EmailConfig setSmtpEncrypted(String smtpEncrypted) {
        this.smtpEncrypted = smtpEncrypted;
        return this;
    }
}
