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

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.smailnet.eamil.Utils.AddressUtil;
import com.smailnet.eamil.Utils.ConfigCheckUtil;
import com.smailnet.eamil.Utils.MailUtil;
import com.smailnet.eamil.Utils.PraseMimeMessage;
import com.smailnet.eamil.Utils.TimeUtil;
import com.stratagile.pnrouter.BuildConfig;
import com.stratagile.pnrouter.utils.LogUtil;
import com.stratagile.pnrouter.utils.UIUtils;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.google.api.services.gmail.model.Draft;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;

import qlc.utils.Base64.Base64;

import static com.smailnet.eamil.Utils.ConstUtli.BLACK_HOLE;
import static com.smailnet.eamil.Utils.ConstUtli.IMAP;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAPS_PARTISLFETCH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAPS_fetchsize;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_PARTISLFETCH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_POST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_CLASS;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_FALLBACK;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_SOCKETFACTORY_PORT;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_fetchsize;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_MIME_BASE64_IGNOREERRORS;
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
 * @author
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

    private String imapEncrypted;    //加密类型
    private String smtpEncrypted;    //加密类型

    private int maxCount = 10;
    private final static int CONNECT_TIMEOUT = 20 * 1000; // milliseconds
    private final static int WRITE_TIMEOUT = 40 * 1000; // milliseconds
    private final static int READ_TIMEOUT = 40 * 1000; // milliseconds
    private final static int FETCH_SIZE = 256 * 1024; // bytes, default 16K
    private final static int POOL_TIMEOUT = 45 * 1000; // milliseconds, default 45 sec

    private static final int APPEND_BUFFER_SIZE = 4 * 1024 * 1024; // bytes

    static final int SMALL_MESSAGE_SIZE = 32 * 1024; // bytes

    static final int ATTACHMENT_BUFFER_SIZE = 8192; // bytes
    static final int DEFAULT_ATTACHMENT_DOWNLOAD_SIZE = 256 * 1024; // bytes

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
        this.imapEncrypted = emailConfig.getImapEncrypted();
        this.smtpEncrypted = emailConfig.getSmtpEncrypted();
        final Properties properties = new Properties();

        String sslSocketFactory = "javax.net.ssl.SSLSocketFactory";
        String isFallback = "false";
        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {

            properties.put(MAIL_SMTP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_SMTP_SOCKETFACTORY_PORT, smtpPort);
            properties.put(MAIL_SMTP_POST, smtpPort);
            properties.put(MAIL_SMTP_HOST, smtpHost);
            properties.put(MAIL_SMTP_AUTH, "true");
            if(smtpEncrypted != null && smtpEncrypted.equals("STARTTLS"))
            {
                //properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.starttls.enable", "true");
               /* properties.setProperty("mail.imaps.auth.plain.disable", "true");
                properties.setProperty("mail.imaps.auth.ntlm.disable", "true");*/
                //properties.put("mail.smtp.ssl.enable", "false");
              /*  MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);*/
                // properties.put("mail.smtp.ssl.checkserveridentity", "false");
                //properties.put("mail.smtp.ssl.socketFactory", sf);
            }else if(smtpEncrypted != null && smtpEncrypted.equals("None")){
                //properties.put(MAIL_SMTP_SOCKETFACTORY_CLASS, sslSocketFactory);
                //properties.put("mail.smtp.starttls.enable", "false");
                //properties.put("mail.smtp.ssl.enable", "true");
            }else {
                properties.put(MAIL_SMTP_SOCKETFACTORY_CLASS, sslSocketFactory);
                //properties.put("mail.smtp.starttls.enable", "false");
                //properties.put("mail.smtp.ssl.enable", "true");
            }
        }
        if (ConfigCheckUtil.getResult(popHost, popPort)) {
            properties.put(MAIL_POP3_SOCKETFACTORY_CLASS, sslSocketFactory);
            properties.put(MAIL_POP3_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_POP3_SOCKETFACTORY_PORT, popPort);
            properties.put(MAIL_POP3_POST, popPort);
            properties.put(MAIL_POP3_HOST, popHost);
            properties.put(MAIL_POP3_AUTH, "true");
        }
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            if(imapEncrypted != null && imapEncrypted.equals("STARTTLS"))
            {
                //properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.imap.starttls.enable", "true");
               /* properties.setProperty("mail.imaps.auth.plain.disable", "true");
                properties.setProperty("mail.imaps.auth.ntlm.disable", "true");*/
                //properties.put("mail.smtp.ssl.enable", "false");
              /*  MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);*/
                // properties.put("mail.smtp.ssl.checkserveridentity", "false");
                //properties.put("mail.smtp.ssl.socketFactory", sf);
            }else if(imapEncrypted != null && imapEncrypted.equals("None")){
                //properties.put(MAIL_SMTP_SOCKETFACTORY_CLASS, sslSocketFactory);
                //properties.put("mail.smtp.starttls.enable", "false");
                //properties.put("mail.smtp.ssl.enable", "true");
            }else {
                properties.put(MAIL_IMAP_SOCKETFACTORY_CLASS, sslSocketFactory);
                //properties.put("mail.smtp.starttls.enable", "false");
                //properties.put("mail.smtp.ssl.enable", "true");
            }
            properties.put(MAIL_IMAP_SOCKETFACTORY_FALLBACK, isFallback);
            properties.put(MAIL_IMAP_SOCKETFACTORY_PORT, imapPort);
            properties.put(MAIL_IMAP_POST, imapPort);
            properties.put(MAIL_IMAP_HOST, imapHost);
            properties.put(MAIL_IMAP_AUTH, "true");
            properties.put(MAIL_MIME_BASE64_IGNOREERRORS, "true");
            properties.put(MAIL_IMAP_PARTISLFETCH, "false");
            properties.put(MAIL_IMAPS_PARTISLFETCH, "false");
            properties.put(MAIL_IMAP_fetchsize,"1048576");
            properties.put(MAIL_IMAPS_fetchsize,"1048576");
            properties.put("mail.imap.connectionpoolsize","10");
            properties.put("mail.imaps.connectionpoolsize","10");
            properties.put("mail.imap.appendbuffersize",Integer.toString(POOL_TIMEOUT));

        }
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.imap.ssl.trust", "*");
        properties.put("mail.imaps.ssl.trust", "*");
        properties.put("mail.imaps.connectiontimeout", Integer.toString(CONNECT_TIMEOUT));
        properties.put("mail.imap.connectiontimeout", Integer.toString(CONNECT_TIMEOUT));
        properties.put("mail.smtp.connectiontimeout", Integer.toString(CONNECT_TIMEOUT));

        properties.put("mail.smtp.timeout", Integer.toString(READ_TIMEOUT));

        properties.put("mail.smtp.writetimeout", Integer.toString(WRITE_TIMEOUT));


        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = properties.getProperty("mail.user");
                String password = properties.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        if(emailConfig.getEmailType() == "4")
        {
            session = Session.getDefaultInstance(properties,authenticator);
        }else{
            session = Session.getInstance(properties);
        }
        session.setDebug(BuildConfig.DEBUG);
    }

    /**
     * 验证邮箱帐户和服务器配置信息
     * @throws MessagingException
     */
    public void authentication() throws Exception {
        Transport transport = session.getTransport(SMTP);
        Store store = session.getStore(POP3);
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);

        if (ConfigCheckUtil.getResult(smtpHost, smtpPort)) {
            transport.connect(smtpHost,Integer.parseInt(smtpPort), account, password);
        }
        /*if (ConfigCheckUtil.getResult(popHost, popPort)) {
            store.connect(popHost, account, password);
        }*/
        if (ConfigCheckUtil.getResult(imapHost, imapPort)) {
            imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        }
    }

    /**
     * 使用SMTP协议发送邮件
     * @throws MessagingException
     */
    public Message sendMail() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        transport.connect(smtpHost,Integer.parseInt(smtpPort), account, password);
        transport.sendMessage(message, message.getAllRecipients());
       /* transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.CC));
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.BCC));*/
        transport.close();
        return message;
    }
    /**
     * 使用SMTP协议发送邮件
     * @throws MessagingException
     */
    public Message gmailSendMail(Gmail service, String userId) throws MessagingException {
        try
        {
            com.google.api.services.gmail.model.Message messageGmail = createMessageWithEmail((MimeMessage)message);
            messageGmail = service.users().messages().send(userId, messageGmail).execute();
            System.out.println("Message id: " + messageGmail.getId());
            System.out.println(messageGmail.toPrettyString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;
    }
    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64url encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public com.google.api.services.gmail.model.Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
        return message;
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
    public EmailCore setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content,String[] attach,String[] cidPath,String uuid,String[] cidList) throws MessagingException {
        Message message = new MimeMessage(session);
        message.addRecipients(Message.RecipientType.TO, to);
        if (cc != null) {
            message.addRecipients(Message.RecipientType.CC, cc);
        }
        if (bcc != null) {
            message.addRecipients(Message.RecipientType.BCC, bcc);
        }
        String fromName = account.substring(0,account.indexOf("@"));
        message.setFrom(new InternetAddress(fromName + "<" + account + ">"));
        String subjectNew = subject;
       /* try {
            subjectNew = (MimeUtility.encodeText(subject,MimeUtility.mimeCharset("gb2312"), null));
        }catch (Exception e)
        {

        }*/
        try {
            subjectNew =MimeUtility.encodeText(subject);
        }catch (Exception E)
        {

        }
        message.setSubject(subjectNew);

        if (text != null){
            message.setText(text);
        }else if (content != null){
            // message.setContent(content, "text/html");
        }
        //** 附件测试

        //整封邮件的MINE消息体
        MimeMultipart mm = new MimeMultipart("mixed");//混合的组合关系

        //正文内容
        MimeBodyPart text_image = new MimeBodyPart();

        //正文（图片和文字部分）
        MimeMultipart mm_text_image  = new MimeMultipart("related");
        //html代码部分
        MimeBodyPart htmlPart = new MimeBodyPart();
        //html代码
        htmlPart.setContent(content,"text/html;charset=gb2312");
        //正文添加图片和html代码
        mm_text_image.addBodyPart(htmlPart);

        if(cidPath.length > 0)
        {
            //contentCid = createContent("", cidPath);
            int flag = 0;
            for (String cidPathItem :cidPath)
            {
                if(!cidPathItem.equals(""))
                {
                    //cid
                    MimeBodyPart imageCid = new MimeBodyPart();
                    //把文件，添加到附件1中
                    //数据源
                    File fileTxt = new File(cidPathItem);
                    if(fileTxt.exists())
                    {
                        //把内容，附件1，附件2加入到 MINE消息体中
                        try {
                            //imageCid.attachFile(fileTxt);
                            DataSource ds1 = new FileDataSource(fileTxt);
                            //数据处理器
                            DataHandler dh1 = new DataHandler(ds1 );
                            //设置第一个附件的数据
                            imageCid.setDataHandler(dh1);
                            //设置第一个附件的文件名
                           /* String fileName = cidPathItem.substring(cidPathItem.lastIndexOf("/") +1,cidPathItem.length());
                            String cid = fileName.substring(0,fileName.lastIndexOf("."));*/
                            String fileName  = cidList[flag];
                            imageCid.setContentID(uuid+fileName);
                            //imageCid.setFileName(fileName);
                            //imageCid.setHeader("Content-Type", "image/*");
                            //imageCid.setDisposition(MimeBodyPart.INLINE);
                            //imageCid.setHeader("Content-ID","b1"+fileName+"");
                            /*imageCid.setHeader("Content-Type", "image/jpg");*/
                            imageCid.setDisposition(MimeBodyPart.INLINE);
                            imageCid.setFileName(uuid+fileName);
                            mm_text_image.addBodyPart(imageCid);
                            //mm_text_image.setSubType("related");
                        }catch (Exception e)
                        {

                        }

                    }
                }
                flag ++;
            }
        }
        //设置内容为正文
        text_image.setContent(mm_text_image);
        mm.addBodyPart(text_image);


        if(attach.length > 0)
        {
            for (String attachPath :attach)
            {
                //附件
                MimeBodyPart attch1 = new MimeBodyPart();
                //把文件，添加到附件1中
                //数据源
                File fileTxt = new File(attachPath);
                if(fileTxt.exists())
                {
                    //把内容，附件1，附件2加入到 MINE消息体中
                    mm.addBodyPart(attch1);
                    String aa = "";
                    DataSource ds1 = new FileDataSource(fileTxt);
                    //数据处理器
                    DataHandler dh1 = new DataHandler(ds1 );
                    //设置第一个附件的数据
                    attch1.setDataHandler(dh1);
                    //设置第一个附件的文件名
                    String fileName = attachPath.substring(attachPath.lastIndexOf("/") +1,attachPath.length());
                    String fileNameNew = fileName;
                    try {
                        fileNameNew =MimeUtility.encodeWord(fileNameNew);
                    }catch (Exception E)
                    {

                    }
                    attch1.setFileName(fileNameNew);
                }
            }
        }
        //设置邮件的MINE消息体
        //message.setContent(mm,"text/html;charset=utf-8");
        message.setContent(mm);
        message.setSentDate(new Date());
        message.saveChanges();
        this.message = message;
        return this;
    }
    public static MimeBodyPart createContent(String body, String[] cidPath) {

        /* 创建代表组合MIME消息的MimeMultipart对象和该对象保存到的MimeBodyPart对象 */
        MimeBodyPart content = new MimeBodyPart();

        // 创建一个MimeMultipart对象
        MimeMultipart multipart = new MimeMultipart();

        if(cidPath.length > 0)
        {
            for (String cidPathItem :cidPath)
            {
                //附件
                MimeBodyPart imageCid = new MimeBodyPart();
                //把文件，添加到附件1中
                //数据源
                File fileTxt = new File(cidPathItem);
                if(fileTxt.exists())
                {
                    //把内容，附件1，附件2加入到 MINE消息体中
                    try {
                        //imageCid.attachFile(fileTxt);
                        DataSource ds1 = new FileDataSource(fileTxt);
                        //数据处理器
                        DataHandler dh1 = new DataHandler(ds1 );
                        //设置第一个附件的数据
                        imageCid.setDataHandler(dh1);
                        //设置第一个附件的文件名
                        String fileName = cidPathItem.substring(cidPathItem.lastIndexOf("/") +1,cidPathItem.length());
                        String cid = fileName.substring(0,fileName.lastIndexOf("."));
                        imageCid.setContentID(fileName);
                        //imageCid.setDisposition(MimeBodyPart.INLINE);
                        multipart.addBodyPart(imageCid);
                    }catch (Exception e)
                    {

                    }

                }
            }
        }

        // 将MimeMultipart对象保存到MimeBodyPart对象中
        try {
            content.setContent(multipart);
        }catch (Exception e)
        {

        }


        return content;
    }
    /**
     * 使用SMTP协议保存邮件
     * @throws MessagingException
     */
    public Message saveDrafts() throws MessagingException {
        return message;
    }
    /**
     * 使用POP3协议接收服务器上的邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailMessage> popReceiveMail() throws MessagingException, IOException {

        Store store = session.getStore(POP3);
        store.connect(popHost,Integer.parseInt(popPort), account, password);
        Folder folder = store.getFolder("INBOX");//获取邮件服务器的收件箱
        folder.open(Folder.READ_ONLY);//以只读权限打开收件箱
        Folder defaultFolder = store.getDefaultFolder();
        Folder[] allFolder = defaultFolder.list();
        int size = folder.getUnreadMessageCount();
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
        String subject, from, to, date, content,contentText;
        PraseMimeMessage pmm = null;
        for (Message message : messagesNew){
            pmm = new PraseMimeMessage((MimeMessage)message);
            subject = message.getSubject();
            from = AddressUtil.codeConver(String.valueOf(message.getFrom()[0]));
            to = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            date = TimeUtil.getDate(message.getSentDate());
            StringBuffer contentTemp = new StringBuffer(30);
            getMailTextContent(message, contentTemp);
            content = contentTemp.toString();
            contentText = getHtmlText(contentTemp.toString());
            EmailMessage emailMessage = new EmailMessage(message,"",subject, from, to,"","", date,true,false,"",true,0,true,2, content,contentText);
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
     * 使用IMAP协议接收服务器上的邮件属性
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailCount> imapReceiveMailCountAndMenu(final ArrayList<String> menuList) throws MessagingException, IOException {

        List<EmailCount> emailMessageList = new ArrayList<>();
        EmailCount emailData = new EmailCount();
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        Folder defaultFolder = imapStore.getDefaultFolder();
        Folder[] allFolder = defaultFolder.list();
        try {
            IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menuList.get(0));
            folder.open(Folder.READ_ONLY);

            int total = folder.getMessageCount();
            if(total > 0)
            {
                Message message = folder.getMessage(total);
                emailData.setInboxMaxMessageId(folder.getUID(message));
                emailData.setInboxMinMessageId(folder.getUID(message));
            }else{
                emailData.setInboxMaxMessageId(0);
                emailData.setInboxMinMessageId(0);
            }
            emailData.setTotalCount(total);
            int size = folder.getUnreadMessageCount();
            emailData.setUnReadCount(size);

            if(!menuList.get(1).equals(""))
            {
                IMAPFolder folderDraf = (IMAPFolder) imapStore.getFolder(menuList.get(1));
                folderDraf.open(Folder.READ_ONLY);
                int totalDraf = folderDraf.getMessageCount();
                if(totalDraf > 0)
                {
                    Message message = folderDraf.getMessage(totalDraf);
                    emailData.setDrafMaxMessageId(folderDraf.getUID(message));
                    emailData.setDrafMinMessageId(folderDraf.getUID(message));
                }else{
                    emailData.setDrafMaxMessageId(0);
                    emailData.setDrafMinMessageId(0);
                }
                emailData.setDrafTotalCount(totalDraf);
                int sizeDraf = folderDraf.getUnreadMessageCount();
                emailData.setDrafUnReadCount(sizeDraf);
                folderDraf.close(false);
            }
            if(!menuList.get(2).equals(""))
            {
                IMAPFolder folderSend = (IMAPFolder) imapStore.getFolder(menuList.get(2));
                folderSend.open(Folder.READ_ONLY);
                int totalSend = folderSend.getMessageCount();

                if(totalSend > 0)
                {
                    Message message = folderSend.getMessage(totalSend);
                    emailData.setSendMaxMessageId(folderSend.getUID(message));
                    emailData.setSendMinMessageId(folderSend.getUID(message));
                }else{
                    emailData.setSendMaxMessageId(0);
                    emailData.setSendMinMessageId(0);
                }

                emailData.setSendTotalCount(totalSend);
                int sizeSend = folderSend.getUnreadMessageCount();
                emailData.setSendunReadCount(sizeSend);
                folderSend.close(false);
            }



            if(!menuList.get(3).equals(""))
            {
                IMAPFolder folderGarbage = (IMAPFolder) imapStore.getFolder(menuList.get(3));
                folderGarbage.open(Folder.READ_ONLY);
                int totalGarbage = folderGarbage.getMessageCount();

                if(totalGarbage > 0)
                {
                    Message message = folderGarbage.getMessage(totalGarbage);
                    emailData.setGarbageMaxMessageId(folderGarbage.getUID(message));
                    emailData.setGarbageMinMessageId(folderGarbage.getUID(message));
                }else{
                    emailData.setGarbageMaxMessageId(0);
                    emailData.setGarbageMinMessageId(0);
                }
                emailData.setGarbageCount(totalGarbage);
                int sizeGarbage = folderGarbage.getUnreadMessageCount();
                emailData.setGarbageUnReadCount(sizeGarbage);
                folderGarbage.close(false);
            }


            if(!menuList.get(4).equals(""))
            {
                IMAPFolder folderDelete = (IMAPFolder) imapStore.getFolder(menuList.get(4));
                folderDelete.open(Folder.READ_ONLY);
                int totalDelete = folderDelete.getMessageCount();
                if(totalDelete > 0)
                {
                    Message message = folderDelete.getMessage(totalDelete);
                    emailData.setDeleteMaxMessageId(folderDelete.getUID(message));
                    emailData.setDeleteMinMessageId(folderDelete.getUID(message));
                }else{
                    emailData.setDeleteMaxMessageId(0);
                    emailData.setDeleteMinMessageId(0);
                }
                emailData.setDeleteTotalCount(totalDelete);
                int sizeDelete = folderDelete.getUnreadMessageCount();
                emailData.setDeleteUnReadCount(sizeDelete);
                folderDelete.close(false);
            }
            folder.close(false);




        }catch (Exception e)
        {
            e.printStackTrace();

        }finally {
            imapStore.close();
            emailMessageList.add(emailData);
            return emailMessageList;
        }

    }
    /**
     * 使用gmai API接收服务器上的邮件属性
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailCount> gmailReceiveMailCountAndMenu(final ArrayList<String> menuList,final Gmail gmailService ,final String userId) throws MessagingException, IOException {

        List<EmailCount> emailMessageList = new ArrayList<>();
        EmailCount emailData = new EmailCount();

        try {
            Label label = gmailService.users().labels().get(userId,menuList.get(0)).execute(); // have the lableName to execute together


            int total = label.getThreadsTotal();
            emailData.setInboxMaxMessageId(0);
            emailData.setInboxMinMessageId(0);
            emailData.setTotalCount(total);
            int size = label.getThreadsUnread();
            emailData.setUnReadCount(size);

            if(!menuList.get(1).equals(""))
            {
                Label labelDraf = gmailService.users().labels().get(userId,menuList.get(1)).execute(); // have the lableName to execute together
                int totalDraf = labelDraf.getThreadsTotal();
                emailData.setDrafMaxMessageId(0);
                emailData.setDrafMinMessageId(0);
                emailData.setDrafTotalCount(totalDraf);
                int sizeDraf = labelDraf.getThreadsUnread();
                emailData.setDrafUnReadCount(sizeDraf);
            }
            if(!menuList.get(2).equals(""))
            {
                Label labelSend = gmailService.users().labels().get(userId,menuList.get(2)).execute(); // have the lableName to execute together
                int totalSend = labelSend.getThreadsTotal();

                emailData.setSendMaxMessageId(0);
                emailData.setSendMinMessageId(0);

                emailData.setSendTotalCount(totalSend);
                int sizeSend = labelSend.getThreadsUnread();
                emailData.setSendunReadCount(sizeSend);

            }



            if(!menuList.get(3).equals(""))
            {
                Label labelGarbage = gmailService.users().labels().get(userId,menuList.get(3)).execute(); // have the lableName to execute together

                int totalGarbage = labelGarbage.getThreadsTotal();

                emailData.setGarbageMaxMessageId(0);
                emailData.setGarbageMinMessageId(0);
                emailData.setGarbageCount(totalGarbage);
                int sizeGarbage = labelGarbage.getThreadsUnread();
                emailData.setGarbageUnReadCount(sizeGarbage);

            }


            if(!menuList.get(4).equals(""))
            {
                Label labelDelete = gmailService.users().labels().get(userId,menuList.get(4)).execute(); // have the lableName to execute together

                int totalDelete = labelDelete.getThreadsTotal();
                emailData.setDeleteMaxMessageId(0);
                emailData.setDeleteMinMessageId(0);
                emailData.setDeleteTotalCount(totalDelete);
                int sizeDelete = labelDelete.getThreadsUnread();
                emailData.setDeleteUnReadCount(sizeDelete);

            }
        }catch (Exception e)
        {
            e.printStackTrace();

        }finally {
            emailMessageList.add(emailData);
            return emailMessageList;
        }

    }
    /**
     * 使用gmai API接收服务器上的邮件属性
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<EmailCount> gmailMailToken(final Gmail gmailService ,final String userId) throws  IOException {

        List<EmailCount> emailMessageList = new ArrayList<>();
        ListLabelsResponse listResponse =
                gmailService.users().labels().list(userId).execute(); // this way just execute and get the label list
        return emailMessageList;
    }
    /**
     * 使用IMAP协议接收服务器上的新邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> imapReceiveNewMail(String menu, final int beginIndex, final int pageSize,final int lastTotalCount) throws MessagingException, IOException {
        HashMap<String, Object> messageMap = new HashMap<>();
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapStoreBegin:"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapStoreEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_ONLY);
        int totalUnreadCount = folder.getUnreadMessageCount();
        Message[] messagesAll = null;
        int totalSize =   folder.getMessageCount();
        int newSize=  totalSize - lastTotalCount;
        boolean noMoreData = false;
        if(newSize >=pageSize)
        {
            noMoreData = false;
            int startIndex = lastTotalCount+1;
            int endIndex = lastTotalCount+pageSize;
            System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
            messagesAll = folder.getMessages(startIndex,endIndex);
        }else if(newSize >0){
            noMoreData = true;
            int startIndex = lastTotalCount+1;
            int endIndex = lastTotalCount+newSize;
            System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
            if(startIndex== 0 || startIndex > endIndex)
            {
                messagesAll = new Message[]{};
            }else{
                messagesAll = folder.getMessages(startIndex,endIndex);
            }
        }else{
            noMoreData = true;
            messagesAll = new Message[]{};
        }
        List<Message> list  = Arrays.asList(messagesAll);
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        for (Message message : list){
            uuid = folder.getUID(message) +"";
            try {
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {

                }

                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    //getMailTextContent(message, contentTemp);
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    content = contentTemp.toString();
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);

              /*  if(!file.exists()){
                    file.mkdirs();
                }
                pmm.setAttachPath(file.toString()+"/");
                try {
                    pmm.saveAttachMent((Part)message);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        folder.close(false);
        imapStore.close();
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",totalSize);
        messageMap.put("totalUnreadCount",totalUnreadCount);
        messageMap.put("noMoreData",noMoreData);

        return messageMap;
    }
    /**
     * 使用IMAP协议接收服务器上的历史邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> imapReceiveMoreMail(String menu, final int beginIndex, final int pageSize,final int lastTotalCount) throws MessagingException, IOException {
        HashMap<String, Object> messageMap = new HashMap<>();
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapStoreBeginHelp:"+menu+"##"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapStoreEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_ONLY);
        int totalUnreadCount = folder.getUnreadMessageCount();
        Message[] messagesAll = null;
        int totalSize =   folder.getMessageCount();
        int newSize=  totalSize - lastTotalCount;
        int lastTotalCountTemp = lastTotalCount;
        System.out.println("newSize_"+newSize+"lastTotalCount:"+lastTotalCount+"totalSize:"+totalSize);
        if(newSize < 0)
        {
            lastTotalCountTemp = totalSize;
        }
        boolean noMoreData = false;
        if(false)
        {
            noMoreData = true;
            messagesAll = new Message[]{};
        }else {
            if(totalSize > 0)
            {
                if(lastTotalCountTemp == 0)
                {
                    if(totalSize >= pageSize)
                    {
                        noMoreData = false;
                        int startIndex = totalSize - pageSize;
                        int endIndex = totalSize ;
                        System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
                        messagesAll = folder.getMessages(startIndex,endIndex);
                    }else{
                        noMoreData = false;
                        int startIndex = 1;
                        int endIndex = totalSize ;
                        System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
                        messagesAll = folder.getMessages(startIndex,endIndex);
                    }

                }else{
                    if(lastTotalCountTemp - beginIndex >=pageSize)
                    {
                        noMoreData = false;
                        int startIndex = totalSize -(pageSize -1) -beginIndex - newSize;
                        int endIndex = totalSize - beginIndex - newSize;
                        System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
                        messagesAll = folder.getMessages(startIndex,endIndex);
                    }else{
                        noMoreData = true;
                        int addSize = lastTotalCountTemp - beginIndex;
                        int startIndex = totalSize -(addSize -1) -beginIndex - newSize;
                        int endIndex = totalSize - beginIndex - newSize;
                        System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
                        if(startIndex== 0 || startIndex > endIndex)
                        {
                            messagesAll = new Message[]{};
                        }else{
                            messagesAll = folder.getMessages(startIndex,endIndex);
                        }

                    }
                }
            }else{
                noMoreData = true;
                messagesAll = new Message[]{};
            }
        }



        List<Message> list  = Arrays.asList(messagesAll);
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        String errorMsg = "";
        for (Message message : list){
            try {
                uuid = folder.getUID(message) +"";
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                if("".equals(from))
                {
                    from = this.account;
                }
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject5:"+hasAttachment+":"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    StringBuffer contentTemp2 = new StringBuffer(30);
                    content = contentTemp.toString();
                    if(content.contains("<body>"))
                    {
                        int beginFlag = content.indexOf("<body>")+6;
                        int endFlag =  content.indexOf("</body>");
                        content = content.substring(beginFlag,endFlag);
                        String regFormat = "\\t|\r|\n";
                        content = content.replaceAll(regFormat,"");
                        String regFormat2 = "&#43;";
                        content = content.replaceAll(regFormat2,"+");
                    }
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);

              /*  if(!file.exists()){
                    file.mkdirs();
                }
                pmm.setAttachPath(file.toString()+"/");
                try {
                    pmm.saveAttachMent((Part)message);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }catch (Exception e)
            {
                e.printStackTrace();
                errorMsg += e.getMessage();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        System.out.println("time_"+"cost:"+(System.currentTimeMillis() -beginTime));
        folder.close(false);
        imapStore.close();
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",totalSize);
        messageMap.put("totalUnreadCount",totalUnreadCount);
        messageMap.put("noMoreData",noMoreData);
        messageMap.put("errorMsg",errorMsg);
        messageMap.put("menu",menu);
        return messageMap;
    }

    /**
     * 使用gmail API接收服务器上的新邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> gmailReceiveNewMail(Gmail gmailService,String userId,String menu, final String pageToken, final long pageSize,final String firstMessageId) throws MessagingException, IOException {


        List<String> selectedMesLable = new ArrayList<String>();
        selectedMesLable.add(menu);
        ListMessagesResponse listMesResponse = gmailService.users().messages().list(userId).setLabelIds(selectedMesLable).setMaxResults(pageSize).setPageToken(pageToken).execute();
        List<com.google.api.services.gmail.model.Message> messagesGmail = new ArrayList<com.google.api.services.gmail.model.Message>();
        String pageTokenTemp = "";
        while (listMesResponse.getMessages() != null) {
            //messagesGmail.addAll(listMesResponse.getMessages());
            boolean isStop = false;
            for(com.google.api.services.gmail.model.Message messageTemp : listMesResponse.getMessages())
            {
                System.out.println("getThreadId:"+ messageTemp.getThreadId() +"--old:"+firstMessageId);
                if(messageTemp.getThreadId().equals(firstMessageId))
                {
                    isStop = true;
                    break;
                }else{
                    messagesGmail.add(messageTemp);
                }
            }
            if(!isStop)
            {
                if (listMesResponse.getNextPageToken() != null) {
                    pageTokenTemp = listMesResponse.getNextPageToken();
                    listMesResponse = gmailService.users().messages().list(userId).setLabelIds(selectedMesLable).setMaxResults(pageSize)
                            .setPageToken(pageTokenTemp).execute();
                }else{
                    break;
                }
            }else{
                break;
            }

        }
        Message[] messagesAll = new Message[messagesGmail.size()];
        int i = 0;
        HashMap<String, String> messageMapId = new HashMap<>();
        for (com.google.api.services.gmail.model.Message message : messagesGmail) {
            System.out.println(message.toPrettyString());
            com.google.api.services.gmail.model.Message messageData = gmailService.users().messages().get(userId, message.getId()).setFormat("raw").execute();

            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(messageData.getRaw());

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
            messageMapId.put(email.getMessageID(),messageData.getId());
            messagesAll[i] = email;
            i++;
        }


        HashMap<String, Object> messageMap = new HashMap<>();
        List<Message> list  = Arrays.asList(messagesAll);
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        String errorMsg = "";
        for (Message message : list){
            try {
                String messageId = ((MimeMessage)message).getMessageID();
                uuid = messageMapId.get(messageId)+"";
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                if("".equals(from))
                {
                    from = this.account;
                }
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject5:"+hasAttachment+":"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    StringBuffer contentTemp2 = new StringBuffer(30);
                    content = contentTemp.toString();
                    if(content.contains("<body>"))
                    {
                        int beginFlag = content.indexOf("<body>")+6;
                        int endFlag =  content.indexOf("</body>");
                        content = content.substring(beginFlag,endFlag);
                        String regFormat = "\\t|\r|\n";
                        content = content.replaceAll(regFormat,"");
                        String regFormat2 = "&#43;";
                        content = content.replaceAll(regFormat2,"+");
                    }
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);

              /*  if(!file.exists()){
                    file.mkdirs();
                }
                pmm.setAttachPath(file.toString()+"/");
                try {
                    pmm.saveAttachMent((Part)message);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }catch (Exception e)
            {
                e.printStackTrace();
                errorMsg += e.getMessage();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        System.out.println("time_"+"cost:"+(System.currentTimeMillis() -beginTime));
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",0);//totalSize
        messageMap.put("totalUnreadCount",0);//totalUnreadCount
        messageMap.put("pageToken",pageTokenTemp);//gmail下拉翻页参数
        messageMap.put("noMoreData",true);
        messageMap.put("errorMsg",errorMsg);
        messageMap.put("menu",menu);
        return messageMap;
    }
    /**
     * 使用gmail API接收服务器上的历史邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> gmailReceiveMoreMail(Gmail gmailService,String userId,String menu, final String pageToken, final long pageSize,final int lastTotalCount) throws MessagingException, IOException {


        List<String> selectedMesLable = new ArrayList<String>();
        selectedMesLable.add(menu);
        ListMessagesResponse listMesResponse = gmailService.users().messages().list(userId).setLabelIds(selectedMesLable).setMaxResults(pageSize).setPageToken(pageToken).execute();
        List<com.google.api.services.gmail.model.Message> messagesGmail = new ArrayList<com.google.api.services.gmail.model.Message>();
        String pageTokenTemp = "";
        while (listMesResponse.getMessages() != null) {
            messagesGmail.addAll(listMesResponse.getMessages());
            if (listMesResponse.getNextPageToken() != null) {
                pageTokenTemp = listMesResponse.getNextPageToken();
               /* listMesResponse = gmailService.users().messages().list(userId).setLabelIds(selectedMesLable).setMaxResults(pageSize)
                        .setPageToken(pageToken).execute();*/
            }
            break;
        }
        Message[] messagesAll = new Message[messagesGmail.size()];
        int i = 0;
        HashMap<String, String> messageMapId = new HashMap<>();
        for (com.google.api.services.gmail.model.Message message : messagesGmail) {
            System.out.println(message.toPrettyString());
            com.google.api.services.gmail.model.Message messageData = gmailService.users().messages().get(userId, message.getId()).setFormat("raw").execute();

            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(messageData.getRaw());

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
            messageMapId.put(email.getMessageID(),messageData.getId());
            messagesAll[i] = email;
            i++;
        }


        HashMap<String, Object> messageMap = new HashMap<>();
        List<Message> list  = Arrays.asList(messagesAll);
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        String errorMsg = "";
        for (Message message : list){
            try {
                String messageId = ((MimeMessage)message).getMessageID();
                uuid = messageMapId.get(messageId)+"";
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                if("".equals(from))
                {
                    from = this.account;
                }
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject5:"+hasAttachment+":"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    StringBuffer contentTemp2 = new StringBuffer(30);
                    content = contentTemp.toString();
                    if(content.contains("<body>"))
                    {
                        int beginFlag = content.indexOf("<body>")+6;
                        int endFlag =  content.indexOf("</body>");
                        content = content.substring(beginFlag,endFlag);
                        String regFormat = "\\t|\r|\n";
                        content = content.replaceAll(regFormat,"");
                        String regFormat2 = "&#43;";
                        content = content.replaceAll(regFormat2,"+");
                    }
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {
                    errorMsg += e.getMessage();
                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);

              /*  if(!file.exists()){
                    file.mkdirs();
                }
                pmm.setAttachPath(file.toString()+"/");
                try {
                    pmm.saveAttachMent((Part)message);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }catch (Exception e)
            {
                e.printStackTrace();
                errorMsg += e.getMessage();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        System.out.println("time_"+"cost:"+(System.currentTimeMillis() -beginTime));
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",0);//totalSize
        messageMap.put("totalUnreadCount",0);//totalUnreadCount
        messageMap.put("pageToken",pageTokenTemp);//gmail下拉翻页参数
        messageMap.put("noMoreData",true);
        messageMap.put("errorMsg",errorMsg);
        messageMap.put("menu",menu);
        return messageMap;
    }
    /**
     * 使用IMAP协议接收服务器上的历史邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> imapReceiveNewMailByUUID(String menu, final long minUIID, final int pageSize,final long maxUUID) throws MessagingException, IOException {
        LogUtil.addLogEmail("2_minUUID:"+minUIID+"  &&&  maxUUID:"+maxUUID+"  &&&  pageSize:"+pageSize,"EmailCore");
        HashMap<String, Object> messageMap = new HashMap<>();
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapStoreBeginHelp:"+menu+"##"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapStoreEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_ONLY);
        int totalUnreadCount = folder.getUnreadMessageCount();
        Message[] messagesAll = new Message[]{};
        int totalSize =   folder.getMessageCount();
        Message messageMax = null;
        if(totalSize > 0)
        {
            messageMax = folder.getMessage(totalSize);
        }
        long fromMaxUUID = 0L;
        if(messageMax != null)
        {
            fromMaxUUID = folder.getUID(messageMax);
        }
        boolean noMoreData = false;
        int len = 0;
        int lengFlag = 0;
        int pageFlag = 1;
        int k = 0;
        LogUtil.addLogEmail("3_fromMaxUUID:"+fromMaxUUID+"  &&&  maxUUID:"+maxUUID+"  &&&  totalSize:"+totalSize,"EmailCore");
        if(fromMaxUUID >0 && maxUUID < fromMaxUUID)
        {
            noMoreData = false;
            int pageSizeTemp = pageSize;
            Boolean noDataLoad = false;
            while (k < pageSize && !noDataLoad)
            {
                pageSizeTemp = pageSize + (pageFlag -1) * 2;
                long[] uuidList = new long[pageSizeTemp];
                lengFlag = 0;
                LogUtil.addLogEmail("4_pageSizeTemp:"+pageSizeTemp,"EmailCore");
                for(int i = 0 ; i < pageSizeTemp ;i++)
                {
                    long index = maxUUID + i + 1 ;
                    if(index > fromMaxUUID)
                    {
                        noDataLoad = true;
                        break;
                    }
                    uuidList[i] = index;
                    len  ++;
                    lengFlag ++;
                }
                long[] uuidListNew = new long[lengFlag];
                for(int i = 0 ; i< lengFlag ;i++)
                {
                    uuidListNew[i] = uuidList[i];
                }
                pageFlag ++;
                messagesAll = folder.getMessagesByUID(uuidListNew);

                 k = 0;
                for (Message message : messagesAll)
                {
                    if(message != null)
                    {
                        k ++;
                    }
                    if(k >= pageSize)
                    {
                        noDataLoad = true;
                        break;
                    }
                }
            }
        }else{
            noMoreData = true;
            messagesAll = new Message[]{};
        }

        List<Message> list  = Arrays.asList(messagesAll);
        LogUtil.addLogEmail("4_list size:"+list.size(),"EmailCore");
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        String errorMsg = "";
        for (Message message : list){
            if(message == null)
            {
                index++;
                continue;
            }
            try {
                uuid = folder.getUID(message) +"";
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                if("".equals(from))
                {
                    from = this.account;
                }
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    content = contentTemp.toString();
                    if(content.contains("<body>"))
                    {
                        int beginFlag = content.indexOf("<body>")+6;
                        int endFlag =  content.indexOf("</body>");
                        content = content.substring(beginFlag,endFlag);
                        String regFormat = "\\t|\r|\n";
                        content = content.replaceAll(regFormat,"");
                        String regFormat2 = "&#43;";
                        content = content.replaceAll(regFormat2,"+");
                    }
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);

              /*  if(!file.exists()){
                    file.mkdirs();
                }
                pmm.setAttachPath(file.toString()+"/");
                try {
                    pmm.saveAttachMent((Part)message);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }catch (Exception e)
            {
                e.printStackTrace();
                errorMsg+=e.getMessage();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        System.out.println("time_"+"cost:"+(System.currentTimeMillis() -beginTime));
        folder.close(false);
        imapStore.close();
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",totalSize);
        messageMap.put("minUIID",minUIID);
        messageMap.put("maxUUID",maxUUID +len);
        messageMap.put("totalUnreadCount",totalUnreadCount);
        messageMap.put("noMoreData",noMoreData);
        messageMap.put("errorMsg",errorMsg);
        messageMap.put("menu",menu);
        return messageMap;
    }
    /**
     * 使用IMAP协议接收服务器上的历史邮件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public HashMap<String, Object> imapReceiveMoreMailByUUID(String menu, final long minUIID, final int pageSize,final long maxUUID) throws MessagingException, IOException {
        HashMap<String, Object> messageMap = new HashMap<>();
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapStoreBeginHelp:"+menu+"##"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapStoreEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_ONLY);
        int totalUnreadCount = folder.getUnreadMessageCount();
        Message[] messagesAll = new Message[]{};
        int totalSize =   folder.getMessageCount();
        Message messageMin = null;
        Long minUUIDTemp = minUIID;
        if(totalSize > 0)
        {
            messageMin = folder.getMessage(1);
            Message messageMax = folder.getMessage(totalSize);
            if(minUUIDTemp == 0L)
            {
                minUUIDTemp = folder.getUID(messageMax) +1;
            }
        }
        long endMinUUID = -1L;
        if(messageMin != null)
        {
            endMinUUID = folder.getUID(messageMin);
        }
        boolean noMoreData = false;
        int len = 0;
        int lengFlag = 0;
        int pageFlag = 1;
        int k = 0;
        if(endMinUUID >=0 && minUUIDTemp > endMinUUID)
        {
            noMoreData = false;
            int pageSizeTemp = pageSize;
            Boolean noDataLoad = false;
            while (k < pageSize && !noDataLoad)
            {
                pageSizeTemp = pageSize + (pageFlag -1) * 2;
                long[] uuidList = new long[pageSizeTemp];
                lengFlag = 0;
                for(int i = 0 ; i < pageSizeTemp ;i++)
                {
                    long index = minUUIDTemp - i - 1 ;
                    if(index < endMinUUID)
                    {
                        noDataLoad = true;
                        break;
                    }
                    uuidList[i] = index;
                    len  ++;
                    lengFlag ++;
                }
                long[] uuidListNew = new long[lengFlag];
                for(int i = 0 ; i< lengFlag ;i++)
                {
                    uuidListNew[i] = uuidList[i];
                }
                pageFlag ++;
                messagesAll = folder.getMessagesByUID(uuidListNew);
                k = 0;
                for (Message message : messagesAll)
                {
                    if(message != null)
                    {
                        k ++;
                    }
                    if(k >= pageSize)
                    {
                        noDataLoad = true;
                        break;
                    }
                }
            }
        }else{
            noMoreData = true;
            messagesAll = new Message[]{};
        }

        List<Message> list  = Arrays.asList(messagesAll);
        Collections.reverse(list);
        List<EmailMessage> emailMessageList = new ArrayList<>();
        String uuid, subject, from, to,cc,bcc, date, content, contentText,priority;
        Boolean  isSeen,isStar,isReplySign,isContainerAttachment;
        int attachmentCount;
        int index = 0;
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        long beginTime = System.currentTimeMillis();
        String errorMsg = "";
        for (Message message : list){
            if(message == null)
            {
                index++;
                continue;
            }
            try {
                uuid = folder.getUID(message) +"";
                System.out.println(index+"_"+"getSubject0:"+System.currentTimeMillis()+"##uuid:"+uuid);
                subject = "";
                try {
                    subject = getSubject((MimeMessage)message);
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject1:"+System.currentTimeMillis());
                from = getFrom((MimeMessage)message);
                if("".equals(from))
                {
                    from = this.account;
                }
                System.out.println(index+"_"+"getSubject2:"+System.currentTimeMillis());
                to = getReceiveAddress((MimeMessage)message,Message.RecipientType.TO);
                cc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.CC);
                bcc =  getReceiveAddress((MimeMessage)message,Message.RecipientType.BCC);
                System.out.println(index+"_"+"getSubject3:"+System.currentTimeMillis());
                date = TimeUtil.getDate(message.getSentDate());
                System.out.println(index+"_"+"getSubject4:"+System.currentTimeMillis());
                isSeen = isSeen((MimeMessage)message);
                isStar = isStar((MimeMessage)message);
                //设置标记
                /*if(!isSeen)
                {
                    Flags flags=message.getFlags();
                    if(flags.contains(Flags.Flag.SEEN))
                    {
                        message.setFlag(Flags.Flag.SEEN,false);
                        message.saveChanges();
                    }

                }*/
                isReplySign = isReplySign((MimeMessage)message);

                List<MailAttachment> mailAttachments = new ArrayList<>();
                boolean hasAttachment = false;
                try {
                    hasAttachment = MailUtil.hasAttachment((MimeMessage)message);
                    //MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = hasAttachment;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    String contentType = message.getContentType();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        getMailTextContent2(message, contentTemp,true);
                    } else
                        getMailTextContent2(message,contentTemp, false);
                    content = contentTemp.toString();
                    if(content.contains("<body>"))
                    {
                        int beginFlag = content.indexOf("<body>")+6;
                        int endFlag =  content.indexOf("</body>");
                        content = content.substring(beginFlag,endFlag);
                        String regFormat = "\\t|\r|\n";
                        content = content.replaceAll(regFormat,"");
                        String regFormat2 = "&#43;";
                        content = content.replaceAll(regFormat2,"+");
                    }
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {
                    errorMsg+=e.getMessage();
                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(message,uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
                emailMessage.setMailAttachmentList(mailAttachments);
                System.out.println(index+"_"+"getSubject7:"+System.currentTimeMillis());
                emailMessageList.add(emailMessage);
                System.out.println(index+"_"+"getSubject8:"+System.currentTimeMillis());
                Log.i("IMAP", "邮件subject："+subject +"  时间："+date);
            }catch (Exception e)
            {
                e.printStackTrace();
                errorMsg+=e.getMessage();
            }

            index ++;
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        System.out.println("time_"+"cost:"+(System.currentTimeMillis() -beginTime));
        folder.close(false);
        imapStore.close();
        messageMap.put("emailMessageList",emailMessageList);
        messageMap.put("totalCount",totalSize);
        messageMap.put("minUIID",minUUIDTemp);
        messageMap.put("maxUUID",maxUUID +len);
        messageMap.put("totalUnreadCount",totalUnreadCount);
        messageMap.put("noMoreData",noMoreData);
        messageMap.put("errorMsg",errorMsg);
        messageMap.put("menu",menu);
        return messageMap;
    }
    /**
     * 使用IMAP协议接收服务器上的邮件附件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<MailAttachment> imapDownloadMailAttch(String menu,String uid,String path,String aesKey) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapReceiveMailAttchBegin:"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapReceiveMailAttchEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_WRITE);
        Message message= folder.getMessageByUID(Long.valueOf(uid));
        //设置标记
        /*message.setFlag(Flags.Flag.SEEN,true);
        message.saveChanges();*/
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        List<MailAttachment> mailAttachments = new ArrayList<>();
        try {
            //pmm = new PraseMimeMessage((MimeMessage)message);
            MailUtil.getAttachment(message, mailAttachments,uid,this.account);
            //pmm.setAttachPath(file.toString()+"/");
            System.out.println("saveFile_"+"begin:"+System.currentTimeMillis());
            long aa = System.currentTimeMillis();
            MailUtil.saveFile(mailAttachments,path,aesKey,menu);
            System.out.println("saveFile_"+"cost:"+(System.currentTimeMillis()- aa));
           /* try {
                pmm.saveAttachMent((Part)message);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        folder.close(false);
        imapStore.close();
        return mailAttachments;
    }

    /**
     * 使用IMAP协议接收服务器上的邮件附件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<MailAttachment> gmailDownloadMailAttch(String menu,String uid,String path,String aesKey,Gmail gmailService,String userId) throws MessagingException, IOException {
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        List<MailAttachment> mailAttachments = new ArrayList<>();
        try {
            com.google.api.services.gmail.model.Message messageData = gmailService.users().messages().get(userId, uid).setFormat("raw").execute();

            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(messageData.getRaw());

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

            MailUtil.getAttachment(email, mailAttachments,uid,this.account);
            //pmm.setAttachPath(file.toString()+"/");
            System.out.println("saveFile_"+"begin:"+System.currentTimeMillis());
            long aa = System.currentTimeMillis();
            MailUtil.saveFile(mailAttachments,path,aesKey,menu);
            System.out.println("saveFile_"+"cost:"+(System.currentTimeMillis()- aa));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        return mailAttachments;
    }
    /**
     * 使用IMAP协议接收服务器上的邮件cid资源
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<MailAttachment> imapDownloadMailCid(String menu,String uid,String path,String aesKey) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapReceiveMailAttchBegin:"+System.currentTimeMillis());
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        System.out.println("time_"+"imapReceiveMailAttchEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_WRITE);
        Message message= folder.getMessageByUID(Long.valueOf(uid));
        //设置标记
        /*message.setFlag(Flags.Flag.SEEN,true);
        message.saveChanges();*/
        PraseMimeMessage pmm = null;
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        List<MailAttachment> mailAttachments = new ArrayList<>();
        try {
            pmm = new PraseMimeMessage((MimeMessage)message);
            MailUtil.getCid(message, mailAttachments,uid,this.account);
            //pmm.setAttachPath(file.toString()+"/");
            System.out.println("saveFile_"+"begin:"+System.currentTimeMillis());
            long aa = System.currentTimeMillis();
            MailUtil.saveFile(mailAttachments,path,aesKey,menu);
            System.out.println("saveFile_"+"cost:"+(System.currentTimeMillis()- aa));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        folder.close(false);
        imapStore.close();
        return mailAttachments;
    }
    /**
     * 使用IMAP协议接收服务器上的邮件cid资源
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<MailAttachment> gmailDownloadMailCid(String menu,String uid,String path,String aesKey,Gmail gmailService,String userId) throws MessagingException, IOException {
        System.out.println("time_"+"begin:"+System.currentTimeMillis());
        List<MailAttachment> mailAttachments = new ArrayList<>();
        try {

            com.google.api.services.gmail.model.Message messageData = gmailService.users().messages().get(userId, uid).setFormat("raw").execute();

            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(messageData.getRaw());

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

            MailUtil.getCid(email, mailAttachments,uid,this.account);
            //pmm.setAttachPath(file.toString()+"/");
            System.out.println("saveFile_"+"begin:"+System.currentTimeMillis());
            long aa = System.currentTimeMillis();
            MailUtil.saveFile(mailAttachments,path,aesKey,menu);
            System.out.println("saveFile_"+"cost:"+(System.currentTimeMillis()- aa));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("time_"+"end:"+System.currentTimeMillis());
        return mailAttachments;
    }
    /**
     * 使用IMAP协议接收服务器上的邮件附件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean imapMarkMail(String menu,String uid,int flag,boolean value,String toMenu) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        if(menu.equals("star"))
        {
            menu = "INBOX";
        }
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        IMAPFolder folderTo = (IMAPFolder) imapStore.getFolder(toMenu);
        folder.open(Folder.READ_WRITE);
        try {
            Message message= folder.getMessageByUID(Long.valueOf(uid));
            //设置标记
            switch (flag)
            {
                case 1:
                    message.setFlag(Flags.Flag.ANSWERED,value);
                    break;
                case 2:
                    if(folderTo!=null){
                        folderTo.open(Folder.READ_WRITE);
                        folder.copyMessages(new Message[]{message}, folderTo);
                    }
                    message.setFlag(Flags.Flag.DELETED,value);
                    break;
                case 4:
                    message.setFlag(Flags.Flag.DRAFT,value);
                    break;
                case 8:
                    message.setFlag(Flags.Flag.FLAGGED,value);
                    break;
                case 16:
                    message.setFlag(Flags.Flag.RECENT,value);
                    break;
                case 32:
                    message.setFlag(Flags.Flag.SEEN,value);
                    break;
            }
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(folder!=null && folder.isOpen()){
                folder.close(false);
            }
            if(folderTo!=null && folderTo.isOpen()){
                folderTo.close(true);
            }
            imapStore.close();



        }
        return false;
    }
    /**
     * 使用gmail API标记已读未读
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean gmailMarkMail(Gmail service, String userId, String messageId,
                                 List<String> labelsToAdd, List<String> labelsToRemove) throws MessagingException, IOException {

        try {
            ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(labelsToAdd)
                    .setRemoveLabelIds(labelsToRemove);
            com.google.api.services.gmail.model.Message message = service.users().messages().modify(userId, messageId, mods).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
        }
        return false;
    }
    /**
     * 使用IMAP协议接收服务器上的邮件附件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean gmailDeleteMail(Gmail gmailService,String userId,String threadId,Boolean delete) throws MessagingException, IOException {
        try {
            if(delete)
            {
                //彻底删除
                gmailService.users().threads().delete(userId, threadId).execute();
            }else{
                //移动到“已删除”
                gmailService.users().messages().trash(userId, threadId).execute();
            }
            System.out.println("Thread with id: " + threadId + " deleted successfully.");
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
        }
        return false;
    }
    /**
     * 使用IMAP协议保存已发送或者草稿箱
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean imapSaveMail(Message message, String toMenu,String flag) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(toMenu);
        folder.open(Folder.READ_WRITE);
        try {
            if(flag.equals("draf"))
            {
                message.setFlag(Flags.Flag.DRAFT,true);
                message.setFlag(Flags.Flag.SEEN,true);
            }
            folder.appendMessages(new Message[] { message });
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(folder!=null && folder.isOpen()){
                folder.close(false);
            }
            imapStore.close();
        }
        return false;
    }
    /**
     * 使用gmail API保存已发送或者草稿箱
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean gmailSaveMail(Message message,Gmail service, String userId) throws MessagingException, IOException {
        try
        {
            com.google.api.services.gmail.model.Message messageGmail = createMessageWithEmail((MimeMessage)message);
            Draft draft = new Draft();
            draft.setMessage(messageGmail);
            draft = service.users().drafts().create(userId, draft).execute();
            System.out.println("draft id: " + draft.getId());
            System.out.println(draft.toPrettyString());
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 使用IMAP协议保存已发送或者草稿箱
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean imapDeleteDrsftsMail(String uid, String toMenu) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost,Integer.parseInt(imapPort), account, password);
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(toMenu);
        folder.open(Folder.READ_WRITE);
        try {
            Message message= folder.getMessageByUID(Long.valueOf(uid));
            message.setFlag(Flags.Flag.DELETED,true);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(folder!=null && folder.isOpen()){
                folder.close(false);
            }
            imapStore.close();
        }
        return false;
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
    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        /*String subjectStr = getUTF8(msg.getSubject());
        String subjectStr2 = getUTF82(msg.getSubject());
        String subject = MimeUtility.unfold(msg.getSubject());*/
        String  aa = msg.getHeader("subject")[0];
        String bb = MimeUtility.decodeText(aa);
        String cc = MimeUtility.decodeText(msg.getSubject());
        return bb;
    }
    public static String decodeMime(String text) {
        if (text == null)
            return null;

        // https://tools.ietf.org/html/rfc2047
        // encoded-word = "=?" charset "?" encoding "?" encoded-text "?="

        int i = 0;
        boolean first = true;
        List<MimeTextPart> parts = new ArrayList<>();

        while (i < text.length()) {
            int s = text.indexOf("=?", i);
            if (s < 0)
                break;

            int q1 = text.indexOf("?", s + 2);
            if (q1 < 0)
                break;

            int q2 = text.indexOf("?", q1 + 1);
            if (q2 < 0)
                break;

            int e = text.indexOf("?=", q2 + 1);
            if (e < 0)
                break;

            String plain = text.substring(i, s);
            if (!first)
                plain = plain.replaceAll("[ \t\n\r]$", "");
            if (!TextUtils.isEmpty(plain))
                parts.add(new MimeTextPart(plain));

            parts.add(new MimeTextPart(
                    text.substring(s + 2, q1),
                    text.substring(q1 + 1, q2),
                    text.substring(q2 + 1, e)));

            i = e + 2;
            first = false;
        }

        if (i < text.length())
            parts.add(new MimeTextPart(text.substring(i)));

        // Fold words to not break encoding
        int p = 0;
        while (p + 1 < parts.size()) {
            MimeTextPart p1 = parts.get(p);
            MimeTextPart p2 = parts.get(p + 1);
            if (p1.charset != null && p1.charset.equalsIgnoreCase(p2.charset) &&
                    p1.encoding != null && p1.encoding.equalsIgnoreCase(p2.encoding)) {
                p1.text += p2.text;
                parts.remove(p + 1);
            } else
                p++;
        }

        StringBuilder sb = new StringBuilder();
        for (MimeTextPart part : parts)
            sb.append(part);
        return sb.toString();
    }
    private static class MimeTextPart {
        String charset;
        String encoding;
        String text;

        MimeTextPart(String text) {
            this.text = text;
        }

        MimeTextPart(String charset, String encoding, String text) {
            this.charset = charset;
            this.encoding = encoding;
            this.text = text;
        }

        @Override
        public String toString() {
            if (charset == null)
                return text;

            String word = "=?" + charset + "?" + encoding + "?" + text + "?=";
            try {
                return decodeMime(MimeUtility.decodeWord(word));
            } catch (Throwable ex) {

                return word;
            }
        }
    }
    /**
     * 获得邮件发件人
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        if (froms == null || froms.length < 1)
            return "";
        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     * @param msg 邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
            return "";
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress)address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length()-1); //删除最后一个逗号

        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null)
            return "";

        if (pattern == null || "".equals(pattern))
            pattern = "yyyy年MM月dd日 E HH:mm ";

        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttachment(Part part,int count) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT))) {
                    flag = true;
                    count ++;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart,count);
                    if(flag)
                    {
                        count ++;
                    }
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                        count ++;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                        count ++;
                    }
                }

                //if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent(),count);
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }
    /**
     * 判断邮件是否星标
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isStar(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.FLAGGED);
    }
    /**
     * ---判断此邮件是否已读，如果未读返回返回false,反之返回true---
     */
    public boolean isNew(MimeMessage msg)throws MessagingException{
        boolean isnew = false;
        Flags flags = msg.getFlags();
        Flags.Flag []flag = flags.getSystemFlags();
        System.out.println("flags's length: "+flag.length);
        for(int i=0;i<flag.length;i++){
            if(flag[i] == Flags.Flag.SEEN){
                isnew=true;
                System.out.println("seen Message.......");
                break;
            }
        }
        return isnew;
    }
    /**
     * 判断邮件是否需要阅读回执
     * @param msg 邮件内容
     * @return 需要回执返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }
    }
    public static void getMailTextContent2(Part part, StringBuffer content, boolean plainFlag) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/plain") && !isContainTextAttach && plainFlag) {
            content.append(MimeUtility.decodeText(part.getContent().toString()));
        } else if(part.isMimeType("text/html") && !isContainTextAttach && !plainFlag){
            content.append(part.getContent().toString());
            plainFlag = false;
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent2((Part)part.getContent(),content,plainFlag);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent2(bodyPart,content,plainFlag);
            }
        }
    }
    public static String getHtmlText(String htmlStr)
    {
        if(htmlStr.contains("/head>"))
        {
            int begin = htmlStr.indexOf("/head>") + 6;
            htmlStr = htmlStr.substring(begin,htmlStr.length());
        }
        String regFormat = "\\t|\r|\n";
        String regTag = "<[^>]*>";
        String text = htmlStr.replaceAll(regFormat,"").replaceAll(regTag,"").replaceAll("&nbsp;"," ").replaceAll("&quot;","");
        return text;
    }
    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getOnlyMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        if (part.isMimeType("text/*")) {
            content.append(part.getContent().toString());
        } else {
            content.append("");
        }
    }
    /**
     * 保存附件
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param destDir  附件保存目录
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveAttachment(Part part, String destDir) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT))) {
                    InputStream is = bodyPart.getInputStream();
                    saveFile(is, destDir, decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart,destDir);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(),destDir);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     * @param is 输入流
     * @param fileName 文件名
     * @param destDir 文件存储目录
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void saveFile(InputStream is, String destDir, String fileName)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File(destDir + fileName)));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
    }

    /**
     * 文本解码
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
    public static String getUTF8(String source)
    {
        String newStr = source;
        try {
            newStr = new String(source.getBytes("UTF-8"), "ISO-8859-1");
        }catch (Exception e)
        {

        }
        return  newStr;
    }
    public static String getUTF82(String source)
    {
        String newStr = source;
        try {
            newStr = new String(source.getBytes("ISO-8859-1"), "UTF-8");
        }catch (Exception e)
        {

        }
        return  newStr;
    }
}
