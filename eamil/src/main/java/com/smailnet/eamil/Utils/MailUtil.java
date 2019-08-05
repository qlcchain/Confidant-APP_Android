package com.smailnet.eamil.Utils;
import android.os.Environment;
import android.util.Log;

import com.smailnet.eamil.MailAttachment;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author zl
 * @date 2019/1/7 19:29
 * <p>
 * 处理邮件工具类
 * 接收/发送服务器根据不同邮箱有不同的地址
 * 在邮箱设置里都可找到
 */
public class MailUtil {
    // 发送服务器
    private static final String SEND_SMTP_HOST = "smtp.exmail.qq.com";
    private static final int SEND_SMTP_PORT = 465;

    // 接收服务器
    private static final String RECEIVE_IMAP_HOST = "imap.exmail.qq.com";
    private static final int RECEIVE_IMAP_PORT = 993;

    private static final String RECEIVE_POP_HOST = "pop.exmail.qq.com";
    private static final int RECEIVE_POP_PORT = 995;

    // 账号
    private String user;
    // 密码
    private String password;

    private IMAPFolder imapFolder = null;
    private IMAPStore imapStore = null;

    private POP3Store pop3Store = null;
    private POP3Folder pop3Folder = null;

    public MailUtil() {
        this("emaildev@qlink.mobi", "Qlcchain@123");
    }

    private MailUtil(String user, String password) {
        this.user = user;
        this.password = password;
    }


    /**
     * 发送邮件
     *
     * @param toEmail 收件人邮箱地址
     * @param subject 邮件标题
     * @param content 邮件内容 可以是html内容
     */
    public void send(String toEmail, String subject, String content) {
        Session session = loadSendSession();
        // session.setDebug(true);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(session);
        try {
            // 设置发件人
            message.setFrom(new InternetAddress(user));
            Address[] a = new Address[1];
            a[0] = new InternetAddress(user);
            message.setReplyTo(a);
            // 设置收件人
            InternetAddress to = new InternetAddress(toEmail);
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            // 设置邮件标题
            message.setSubject(subject);
            // 设置邮件的内容体
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        }
    }


    /**
     * 发送邮件 带附件
     *
     * @param toEmail    收件人邮箱地址
     * @param subject    邮件标题
     * @param content    邮件内容 可以是html内容
     * @param attachPath 附件路径
     */
    public void send(String toEmail, String subject, String content, String attachPath) {
        Session session = loadSendSession();

        MimeMessage mm = new MimeMessage(session);
        try {
            //发件人
            mm.setFrom(new InternetAddress(user));
            //收件人
            mm.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // 设置收件人
            // mm.setRecipient(Message.RecipientType.CC, new
            // InternetAddress("XXXX@qq.com")); //设置抄送人
            //标题
            mm.setSubject(subject);
            //内容
            Multipart multipart = new MimeMultipart();
            //body部分
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);

            //附件部分
            BodyPart attachPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(attachPath);
            attachPart.setDataHandler(new DataHandler(fileDataSource));
            attachPart.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
            multipart.addBodyPart(attachPart);

            mm.setContent(multipart);
            Transport.send(mm);
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        }

    }


    private Session loadSendSession() {
        try {
            // 配置发送邮件的环境属性
            final Properties props = new Properties();
            // 表示SMTP发送邮件，需要进行身份验证
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", SEND_SMTP_HOST);
            // props.put("mail.smtp.port", SEND_SMTP_PORT);
            // 如果使用ssl，则去掉使用25端口的配置，进行如下配置,
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", SEND_SMTP_PORT);
            props.put("mail.smtp.port", SEND_SMTP_PORT);
            // 发件人的账号
            props.put("mail.user", user);
            // 访问SMTP服务时需要提供的密码
            props.put("mail.password", password);
            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            return Session.getInstance(props, authenticator);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("mail session is null");
        }
        return null;
    }

    public static void saveFile(List<MailAttachment> list, String savaPath) throws IOException {
        for (MailAttachment mailAttachment : list) {
            try{
                InputStream inputStream = mailAttachment.getInputStream();

                BufferedInputStream in=null;
                FileOutputStream out= null;
                in=new BufferedInputStream(inputStream);
                out=new FileOutputStream(savaPath+mailAttachment.getAccount()+"_"+mailAttachment.getName());
                int len=-1;
                byte[] b=new byte[4096];
                while((len=in.read(b))!=-1){
                    out.write(b,0,len);
                }
                in.close();
                out.close();
            }catch(Exception exception){
                exception.printStackTrace();
            }finally{

            }
        }

    }
    /**
     * 保存附件到指定目录里
     */
    public static void saveFile(List<MailAttachment> list)throws Exception{
        for (MailAttachment mailAttachment : list) {
            InputStream in = mailAttachment.getInputStream();
            String osName = System.getProperty("os.name");
            File file = Environment.getExternalStorageDirectory();
            String storedir = file.toString()+"/";
            String separator = "";
            System.out.println(osName);
            File storefile = new File(storedir+separator+mailAttachment.getName());
            System.out.println("文件路径: "+storefile.toString());
            /*for(int i=0;storefile.exists();i++){
                storefile = new File(storedir+separator+mailAttachment.getName()+i);
            }*/
            if(!storefile.exists())
            {
                try {
                    storefile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            try{
                bos = new BufferedOutputStream(new FileOutputStream(storefile));
                bis = new BufferedInputStream(in);
                int c;
                while((c=bis.read()) != -1){
                    bos.write(c);
                    bos.flush();
                }
            }catch(Exception exception){
                exception.printStackTrace();
                throw new Exception("文件保存失败!");
            }finally{
                bos.close();
                bis.close();
            }
        }

    }
    /**
     * ----判断此邮件是否包含附件----
     */
    public boolean isContainAttach(Part part)throws Exception{
        boolean attachflag = false;
        if(part.isMimeType("multipart/*")){
            Multipart mp = (Multipart)part.getContent();
            for(int i=0;i<mp.getCount();i++){
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if((disposition != null) &&((disposition.equals(Part.ATTACHMENT)) ||(disposition.equals(Part.INLINE))))
                    attachflag = true;
                else if(mpart.isMimeType("multipart/*")){

                }else{
                    String contype = mpart.getContentType();
                    if(contype.toLowerCase().indexOf("application") != -1) attachflag=true;
                    if(contype.toLowerCase().indexOf("name") != -1) attachflag=true;
                }
            }
        }else if(part.isMimeType("message/rfc822")){
            attachflag = isContainAttach((Part)part.getContent());
        }
        return attachflag;
    }
    /**
     * 获取附件
     * 只获取附件里的
     * 邮件内容里的附件（图片等）忽略
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param list 附件容器
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void getAttachment(Part part, List<MailAttachment> list,String msgId,String account) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        System.out.println("getAttachment:begin"+System.currentTimeMillis());
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            System.out.println("getAttachment:for"+System.currentTimeMillis());
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                System.out.println("getAttachment:for:"+i+System.currentTimeMillis());
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {

                    InputStream is = bodyPart.getInputStream();
                    System.out.println("getAttachment:for:toByteArrayText:"+i+System.currentTimeMillis());
                    //byte[] byt = MailUtil.toByteArray(is);
                    byte[] byt = new byte[0];
                    System.out.println("getAttachment:for:toByteArrayEndText:"+i+System.currentTimeMillis());
                    Log.i("getAttachment",byt.length +"");
                    // 附件名通过MimeUtility解码，否则是乱码
                    System.out.println("getAttachment:for:decodeText:"+i+System.currentTimeMillis());
                    int aaabb= bodyPart.getSize();
                    String name = "";
                    if(bodyPart.getFileName() != null)
                    {
                        name = MimeUtility.decodeText(bodyPart.getFileName());
                    }
                    System.out.println("getAttachment:for:decodeEndText:"+i+System.currentTimeMillis());
                    list.add(new MailAttachment(name,is, byt,msgId,account));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    getAttachment(bodyPart, list,msgId,account);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("name") || contentType.contains("application")) {

                    }
                }
            }
            System.out.println("getAttachment:end"+System.currentTimeMillis());
        } else if (part.isMimeType("message/rfc822")) {
            getAttachment((Part) part.getContent(), list,msgId,account);
        }
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @return 邮件中是否存在附件
     * @throws MessagingException
     * @throws IOException
     */
    public boolean hasAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = hasAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }

                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = hasAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 获得邮件文本内容
     *
     * @param part        邮件体
     * @param textContent 存储邮件文本内容的字符串
     * @param htmlContent 存储邮件html内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public void getMailTextContent(Part part, StringBuffer textContent, StringBuffer htmlContent) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            String contentType = part.getContentType();
            if (contentType.startsWith("TEXT/PLAIN") || contentType.startsWith("text/plain")) {
                textContent.append(part.getContent().toString());
            }
            if (contentType.startsWith("TEXT/HTML") || contentType.startsWith("text/html")) {
                htmlContent.append(part.getContent().toString());
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), textContent, htmlContent);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, textContent, htmlContent);
            }
        }
    }

    public String getSubject(Message message) throws MessagingException {
        return message.getSubject();
    }

    /**
     * 获取邮件信息
     * IMAP协议
     * 下载附件的时候巨慢
     * 如果需要获取附件的时候不推荐使用
     * 该协议下只能查询receivedDate
     * sentDate都为null
     *
     * @throws MessagingException
     */
    public Message[] getImapMessages() throws MessagingException {
        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty("mail.mime.splitlongparameters", "false");
        Properties props = System.getProperties();
        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.port", "993");
        props.setProperty("mail.imapStore.protocol", "imap");
        props.setProperty("mail.imap.host", RECEIVE_IMAP_HOST);
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.auth.login.disable", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        imapStore = (IMAPStore) session.getStore("imap");  // 使用imap会话机制，连接服务器
        imapStore.connect(RECEIVE_IMAP_HOST, RECEIVE_IMAP_PORT, user, password);
        imapFolder = (IMAPFolder) imapStore.getFolder("INBOX"); //收件箱

        imapFolder.open(Folder.READ_WRITE);

        // 获取前一天的邮件信息
        SearchTerm endTerm = new ReceivedDateTerm(ComparisonTerm.LE, new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));
        SearchTerm startTerm = new ReceivedDateTerm(ComparisonTerm.GE, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L));
        SearchTerm searchTerm = new AndTerm(startTerm, endTerm);

        return imapFolder.search(searchTerm);
    }

    /**
     * 通过pop3协议获取邮件信息
     * pop3协议下只能通过sentDate来查询
     * receivedDate都为null
     *
     * @return Message[]
     * @throws MessagingException
     */
    public Message[] getPopMessages() throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.imapStore.protocol", "pop3");       // 使用pop3协议
        props.setProperty("mail.pop3.port", "110");           // 端口
        props.setProperty("mail.pop3.host", RECEIVE_POP_HOST);       // pop3服务器
        Session session = Session.getInstance(props);
        pop3Store = (POP3Store) session.getStore("pop3");
        pop3Store.connect(RECEIVE_POP_HOST, RECEIVE_POP_PORT, user, password);

        // 获得收件箱
        pop3Folder = (POP3Folder) pop3Store.getFolder("INBOX");
        /* Folder.READ_ONLY：只读权限
         * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
         */
        pop3Folder.open(Folder.READ_ONLY); //打开收件箱

        // 获取前一天的邮件信息
        SearchTerm endTerm = new SentDateTerm(ComparisonTerm.LE, new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));
        SearchTerm startTerm = new SentDateTerm(ComparisonTerm.GE, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L));
        SearchTerm searchTerm = new AndTerm(startTerm, endTerm);
//         得到收件箱中的所有邮件,并解析
        return pop3Folder.search(searchTerm);
    }

    /**
     * 关闭folder和store资源
     *
     * @throws MessagingException
     */
    public void close() throws MessagingException {
        if (imapFolder != null) {
            imapFolder.close(false);
        }
        if (imapStore != null) {
            imapStore.close();
        }
        if (pop3Folder != null) {
            pop3Folder.close(false);
        }
        if (pop3Store != null) {
            pop3Store.close();
        }
    }
    public static int getInputStreamSize(InputStream inStream)
    {
        int count = 0;
        BufferedInputStream  bis = null;
        try{
            bis = new BufferedInputStream(inStream);


            int c;
            while((c=bis.read()) != -1){
                count +=c;
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }finally{
            try {
                bis.close();
            }catch (Exception e)
            {

            }

        }
        return  count;
    }
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        try{
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();
        }catch (Exception e)
        {

        }finally {
            output.close();
        }
        return output.toByteArray();
    }
    public static byte[] read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int num = inputStream.read(buffer);
            while (num != -1) {
                baos.write(buffer, 0, num);
                num = inputStream.read(buffer);
            }
            baos.flush();

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (baos != null) {
                baos.close();
            }
            return baos.toByteArray();
        }

    }

}

