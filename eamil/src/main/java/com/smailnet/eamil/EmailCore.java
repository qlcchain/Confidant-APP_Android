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
import android.util.Log;

import com.smailnet.eamil.Utils.AddressUtil;
import com.smailnet.eamil.Utils.ConfigCheckUtil;
import com.smailnet.eamil.Utils.ConstUtli;
import com.smailnet.eamil.Utils.MailUtil;
import com.smailnet.eamil.Utils.PraseMimeMessage;
import com.smailnet.eamil.Utils.TimeUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
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
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAPS_PARTISLFETCH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_AUTH;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_HOST;
import static com.smailnet.eamil.Utils.ConstUtli.MAIL_IMAP_PARTISLFETCH;
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

    private int maxCount = 10;



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
            properties.put(MAIL_IMAP_PARTISLFETCH, false);
            properties.put(MAIL_IMAPS_PARTISLFETCH, false);
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
    public EmailCore setMessage(String nickname, Address[] to, Address[] cc, Address[] bcc, String subject, String text, Object content,String[] attach) throws MessagingException {
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
                    msgMultipart.addBodyPart(attch1);
                    String aa = "";
                    DataSource ds1 = new FileDataSource(fileTxt);
                    //数据处理器
                    DataHandler dh1 = new DataHandler(ds1 );
                    //设置第一个附件的数据
                    attch1.setDataHandler(dh1);
                    //设置第一个附件的文件名
                    String fileName = attachPath.substring(attachPath.lastIndexOf("/") +1,attachPath.length());
                    attch1.setFileName(fileName);
                }
            }
        }

        //正文内容
        MimeBodyPart contentMimeBodyPart = new MimeBodyPart();
        msgMultipart.addBodyPart(contentMimeBodyPart);





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
    public Message sendMail() throws MessagingException {
        Transport transport = session.getTransport(SMTP);
        transport.connect(smtpHost, account, password);
        transport.sendMessage(message, message.getAllRecipients());
       /* transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.CC));
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.BCC));*/
        transport.close();
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
        store.connect(popHost, account, password);
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
            EmailMessage emailMessage = new EmailMessage("",subject, from, to,"","", date,true,false,"",true,0,true,2, content,contentText);
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
        imapStore.connect(imapHost, account, password);


        /*Folder defaultFolder = imapStore.getDefaultFolder();
        Folder[] allFolder = defaultFolder.list();*/

        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menuList.get(0));
        folder.open(Folder.READ_ONLY);
        int total = folder.getMessageCount();
        emailData.setTotalCount(total);
        int size = folder.getUnreadMessageCount();
        emailData.setUnReadCount(size);

        IMAPFolder folderDraf = (IMAPFolder) imapStore.getFolder(menuList.get(1));
        folderDraf.open(Folder.READ_ONLY);
        int totalDraf = folderDraf.getMessageCount();
        emailData.setDrafTotalCount(totalDraf);
        int sizeDraf = folderDraf.getUnreadMessageCount();
        emailData.setDrafUnReadCount(sizeDraf);

        IMAPFolder folderSend = (IMAPFolder) imapStore.getFolder(menuList.get(2));
        folderSend.open(Folder.READ_ONLY);
        int totalSend = folderSend.getMessageCount();
        emailData.setSendTotalCount(totalSend);
        int sizeSend = folderSend.getUnreadMessageCount();
        emailData.setSendunReadCount(sizeSend);

        IMAPFolder folderGarbage = (IMAPFolder) imapStore.getFolder(menuList.get(3));
        folderGarbage.open(Folder.READ_ONLY);
        int totalGarbage = folderGarbage.getMessageCount();
        emailData.setGarbageCount(totalGarbage);
        int sizeGarbage = folderGarbage.getUnreadMessageCount();
        emailData.setGarbageUnReadCount(sizeGarbage);

        IMAPFolder folderDelete = (IMAPFolder) imapStore.getFolder(menuList.get(4));
        folderDelete.open(Folder.READ_ONLY);
        int totalDelete = folderDelete.getMessageCount();
        emailData.setDeleteTotalCount(totalDelete);
        int sizeDelete = folderDelete.getUnreadMessageCount();
        emailData.setDeleteUnReadCount(sizeDelete);


        folder.close(false);
        folderDraf.close(false);
        folderSend.close(false);
        folderGarbage.close(false);
        folderDelete.close(false);

        imapStore.close();
        emailMessageList.add(emailData);
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
        imapStore.connect(imapHost, account, password);
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
            pmm = new PraseMimeMessage((MimeMessage)message);
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
                try {
                    MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = attachmentCount > 0;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    getMailTextContent(message, contentTemp);
                    content = contentTemp.toString();
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
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
        System.out.println("time_"+"imapStoreBegin:"+System.currentTimeMillis());
        imapStore.connect(imapHost, account, password);
        System.out.println("time_"+"imapStoreEnd:"+System.currentTimeMillis());
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(menu);
        folder.open(Folder.READ_ONLY);
        int totalUnreadCount = folder.getUnreadMessageCount();
        Message[] messagesAll = null;
        int totalSize =   folder.getMessageCount();
        int newSize=  totalSize - lastTotalCount;
        boolean noMoreData = false;
        if(lastTotalCount - beginIndex >=pageSize)
        {
            noMoreData = false;
            int startIndex = totalSize -(pageSize -1) -beginIndex - newSize;
            int endIndex = totalSize - beginIndex - newSize;
            System.out.println(startIndex+"###"+endIndex +"###"+noMoreData);
            messagesAll = folder.getMessages(startIndex,endIndex);
        }else{
            noMoreData = true;
            int addSize = lastTotalCount - beginIndex;
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
            pmm = new PraseMimeMessage((MimeMessage)message);
            MimeMessage ooo = (MimeMessage)message;

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
                try {
                    MailUtil.getAttachment(message, mailAttachments,uuid,this.account);
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject5:"+System.currentTimeMillis());
                attachmentCount = mailAttachments.size();
                isContainerAttachment = attachmentCount > 0;
                StringBuffer contentTemp = new StringBuffer(30);
                content = "";
                contentText = "";
                try {
                    getMailTextContent(message, contentTemp);
                    content = contentTemp.toString();
                    contentText = getHtmlText(contentTemp.toString());
                }catch (Exception e)
                {

                }
                System.out.println(index+"_"+"getSubject6:"+System.currentTimeMillis());
                EmailMessage emailMessage = new EmailMessage(uuid,subject, from, to,cc,bcc, date,isSeen,isStar,"",isReplySign,message.getSize(),isContainerAttachment,attachmentCount ,content,contentText);
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
     * 使用IMAP协议接收服务器上的邮件附件
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public List<MailAttachment> imapDownloadMailAttch(String menu,String uid,String path) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        System.out.println("time_"+"imapReceiveMailAttchBegin:"+System.currentTimeMillis());
        imapStore.connect(imapHost, account, password);
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
            MailUtil.getAttachment(message, mailAttachments,uid,this.account);
            //pmm.setAttachPath(file.toString()+"/");
            MailUtil.saveFile(mailAttachments,path);
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
    public boolean imapMarkMail(String menu,String uid,int flag,boolean value,String toMenu) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost, account, password);
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
     * 使用IMAP协议保存已发送或者草稿箱
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public boolean imapSaveToSendMail(Message message,String toMenu) throws MessagingException, IOException {
        IMAPStore imapStore = (IMAPStore) session.getStore(IMAP);
        imapStore.connect(imapHost, account, password);
        IMAPFolder folder = (IMAPFolder) imapStore.getFolder(toMenu);
        folder.open(Folder.READ_WRITE);
        try {
            message.setFlag(Flags.Flag.SEEN,true);
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
        return MimeUtility.decodeText(msg.getSubject());
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
        if (froms.length < 1)
            throw new MessagingException("没有发件人!");

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
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
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
    public static String getHtmlText(String htmlStr)
    {
        String regFormat = "\\s*|\t|\r|\n";
        String regTag = "<[^>]*>";
        String text = htmlStr.replaceAll(regFormat,"").replaceAll(regTag,"").replaceAll("&nbsp;","");
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
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
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
}
