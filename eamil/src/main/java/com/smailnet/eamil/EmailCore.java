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

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.smailnet.eamil.Utils.AddressUtil;
import com.smailnet.eamil.Utils.ConfigCheckUtil;
import com.smailnet.eamil.Utils.ConstUtli;
import com.smailnet.eamil.Utils.ContentUtil;
import com.smailnet.eamil.Utils.PraseMimeMessage;
import com.smailnet.eamil.Utils.TimeUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;

import static com.smailnet.eamil.Utils.ConstUtli.BLACK_HOLE;
import static com.smailnet.eamil.Utils.ConstUtli.IMAP;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_POP3_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_SMTP_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.POP3;
import static com.smailnet.eamil.Utils.ConstUtli.SMTP;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author 张观湖
 * @author E-mail: zguanhu@foxmail.com
 * @version 2.3
 */
class EmailCore {

    private String smtpHost;
    private String popHost;
    private String imapHost;
    private String smtpPort;
    private String popPort;
    private String imapPort;
    private String account;
    private String password;
    private Session session;

    private Message message;

    /**
     * 默认构造器
     */
    EmailCore(){

    }

    /**
     * 在构造器中初始化Properties和Session
     * @param emailConfig
     */
    EmailCore(EmailConfig emailConfig){
        this.smtpHost = emailConfig.getSmtpHost();
        this.popHost = emailConfig.getPopHost();
        this.imapHost = emailConfig.getImapHost();
        this.smtpPort = String.valueOf(emailConfig.getSmtpPort());
        this.popPort = String.valueOf(emailConfig.getPopPort());
        this.imapPort = String.valueOf(emailConfig.getImapPort());
        this.account = emailConfig.getAccount();
        this.password = emailConfig.getPassword();
        Properties properties = new Properties();

        String sslSocketFactory = "javax.net.ssl.SSLSocketFactory";
        String isFallback = "false";
        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {
            properties.put(MAIL_SMTP_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_SMTP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_SMTP_SOCKETFACTORY_PORT, smtpPort);
            properties.put(MAIL_SMTP_POST, smtpPort);
            properties.put(MAIL_SMTP_HOST, smtpHost);
            properties.put(MAIL_SMTP_AUTH, true);
        }
        if (ConfigCheckUtil.getResult(popHost, popPort)) {
            properties.put(MAIL_POP3_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_POP3_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_POP3_SOCKETFACTORY_PORT, popPort);
            properties.put(MAIL_POP3_POST, popPort);
            properties.put(MAIL_POP3_HOST, popHost);
            properties.put(MAIL_POP3_AUTH, true);
        }
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            properties.put(MAIL_IMAP_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_IMAP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_IMAP_SOCKETFACTORY_PORT, imapPort);
            properties.put(MAIL_IMAP_POST, imapPort);
            properties.put(MAIL_IMAP_HOST, imapHost);
            properties.put(MAIL_IMAP_AUTH, true);
        }

        session = Session.getInstance(properties);
    }

    /**
     * 验证邮箱帐户和服务器配置信息
     * @throws MessagingException
     */
    public void authentication() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        Store store = session.getStore(POP3);
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);

        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {
            transport.connect(smtpHost, account, password);
        }
        if (ConfigCheckUtil.getResult(popHost, popPort)) {
            store.connect(popHost, account, password);
        }
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            imapStore.connect(imapHost, account, password);
        }
    }

    /**
     * 组装邮件的信息
     * @param nickname
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param content
     * @throws MessagingException
     */
    public EmailCore setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content) throws MessagingException {
        Message message = new MimeMessage(session);
        message.addRecipients(Message.RecipientType.TO, to);
        if (cc != null) {
            message.addRecipients(Message.RecipientType.CC, cc);
        }
        if (bcc != null) {
            message.addRecipients(Message.RecipientType.BCC, bcc);
        }
        message.setFrom(new InternetAddress(nickname + "<" + account + ">"));
        message.setSubject(subject);
        if (text != null){
            message.setText(text);
        }else if (content != null){
           // message.setContent(content, "text/html");
        }
        //** 附件测试

        //整封邮件的MINE消息体
        MimeMultipart msgMultipart = new MimeMultipart("mixed");//混合的组合关系
        //设置邮件的MINE消息体
        message.setContent(msgMultipart);
        //附件1
        MimeBodyPart attch1 = new MimeBodyPart();
        //正文内容
        MimeBodyPart contentMimeBodyPart = new MimeBodyPart();
        msgMultipart.addBodyPart(contentMimeBodyPart);
        msgMultipart.addBodyPart(contentMimeBodyPart);

        //正文内容
        MimeBodyPart contentaaa = new MimeBodyPart();


        //把文件，添加到附件1中
        //数据源
        File file1 = Environment.getExternalStorageDirectory();

        File fileTxt = new File(ConstUtli.attchPath);
        if(fileTxt.exists())
        {
            //把内容，附件1，附件2加入到 MINE消息体中
            msgMultipart.addBodyPart(attch1);
            String aa = "";
            DataSource ds1 = new FileDataSource(fileTxt);
            //数据处理器
            DataHandler dh1 = new DataHandler(ds1 );
            //设置第一个附件的数据
            attch1.setDataHandler(dh1);
            //设置第一个附件的文件名
            String fileName = ConstUtli.attchPath.substring(ConstUtli.attchPath.lastIndexOf("/") +1,ConstUtli.attchPath.length());
            attch1.setFileName(fileName);
        }

        //正文（图片和文字部分）
        MimeMultipart bodyMultipart  = new MimeMultipart("related");
        //设置内容为正文
        contentMimeBodyPart.setContent(bodyMultipart);

        //html代码部分
        MimeBodyPart htmlPart = new MimeBodyPart();

        //正文添加图片和html代码
        bodyMultipart.addBodyPart(htmlPart);
        //html代码
        htmlPart.setContent(content,"text/html;charset=utf-8");
        message.setSentDate(new Date());
        message.saveChanges();
        this.message = message;
        return this;
    }

    /**
     * 使用SMTP协议发送邮件
     * @throws MessagingException
     */
    public void sendMail() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        transport.connect(smtpHost, account, password);
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    /**
     * 使用POP3协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> popReceiveMail() throws MessagingException, IOException {

        Store store = session.getStore(POP3);
        store.connect(popHost, account, password);
        Folder folder = store.getFolder("INBOX");//获取邮件服务器的收件箱
        folder.open(Folder.READ_ONLY);//以只读权限打开收件箱
        //folder.getUnreadMessageCount();
        Message[] messagesAll = folder.getMessages();
        //Message[] messages = folder.getMessages(1,1);
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // false代表未读，true代表已读
        Message messagesUnRead[] = folder.search(ft);
        List<Message> list = Arrays.asList(messagesAll);
        Collections.sort(list, comparator);
        Message[] messagesAllNew = ( Message[])list.toArray();
        Message[] messagesNew = new Message[1];
        System.arraycopy(messagesAllNew,0,messagesNew,0,messagesNew.length);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        PraseMimeMessage pmm = null;
        for (Message message : messagesNew){
            pmm = new PraseMimeMessage((MimeMessage)message);
            subject = message.getSubject();
            from = AddressUtil.codeConver(String.valueOf(message.getFrom()[0]));
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            date = TimeUtil.getDate(message.getSentDate());
            content = ContentUtil.getContent(message);
            EmailMessage emailMessage = new EmailMessage(subject, from, to, date, content);
            emailMessageList.add(emailMessage);
            Log.i("POP3", "邮件subject："+subject +"  时间："+date);
            File file = Environment.getExternalStorageDirectory();
            if(!file.exists()){
                file.mkdirs();
            }
            pmm.setAttachPath(file.toString()+"/");
            try {
                pmm.saveAttachMent((Part)message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        folder.close(false);
        store.close();
        return emailMessageList;
    }
    // 这里排序规则：按票数从高到低排，票数相同按年龄从低到高排
    Comparator<Message> comparator = new Comparator<Message>() {
        public int compare(Message o1, Message o2) {
            int result = 0;
            try
            {
                result =  (int)(o2.getReceivedDate().getTime() - o1.getReceivedDate().getTime());
            }
            catch (Exception e)
            {

            }

            return result;
        }
    };
    /**
     * 使用IMAP协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> imapReceiveMail() throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost, account, password);
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        //folder.getUnreadMessageCount();
        Message[] messagesAlla = folder.getMessages();
        Message[] messagesAll = folder.getMessages(messagesAlla.length -5,messagesAlla.length);
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false); // false代表未读，true代表已读
        Message messagesUnRead[] = folder.search(ft);
        List<Message> list = Arrays.asList(messagesAll);
        Collections.sort(list, comparator);
        Message[] messagesAllNew = ( Message[])list.toArray();
        Message[] messagesNew = new Message[5];
        System.arraycopy(messagesAllNew,0,messagesNew,0,messagesNew.length);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String subject, from, to, date, content;
        int index = 0;
        PraseMimeMessage pmm = null;
        for (Message message : messagesNew){
            pmm = new PraseMimeMessage((MimeMessage)message);

            System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis());
            subject = message.getSubject();
            System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
            from = AddressUtil.codeConver(String.valueOf(message.getFrom()[0]));
            System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
            date = TimeUtil.getDate(message.getSentDate());
            System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
            content = ContentUtil.getContent(message);
            System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
            EmailMessage emailMessage = new EmailMessage(subject, from, to, date, content);
            System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
            emailMessageList.add(emailMessage);
            System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
            Log.i("IMAP", "邮件subject："+subject +"  时间："+date);
            File file = Environment.getExternalStorageDirectory();
            if(!file.exists()){
                file.mkdirs();
            }
            pmm.setAttachPath(file.toString()+"/");
            try {
                pmm.saveAttachMent((Part)message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            index ++;
        }
        folder.close(false);
        imapStore.close();
        return emailMessageList;
    }

    /**
     *
     * @param host
     * @throws UnknownHostException
     */
    public void spamCheck(String host) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(host);
        byte[] bytes = inetAddress.getAddress();
        StringBuilder query = new StringBuilder(BLACK_HOLE);
        for (byte octet : bytes){
            int unsignedByte = (octet < 0)? octet + 256 : octet;
            query.insert(0, unsignedByte + ".");
        }
        InetAddress.getByName(query.toString());
    }
}
