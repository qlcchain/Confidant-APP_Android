package com.smailnet.eamil;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;

public class MyEmailMessage extends Message implements Serializable {

    @Override
    public Address[] getFrom() throws MessagingException {
        return new Address[0];
    }

    @Override
    public void setFrom() throws MessagingException {

    }

    @Override
    public void setFrom(Address address) throws MessagingException {

    }

    @Override
    public void addFrom(Address[] addresses) throws MessagingException {

    }

    @Override
    public Address[] getRecipients(RecipientType recipientType) throws MessagingException {
        return new Address[0];
    }

    @Override
    public void setRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {

    }

    @Override
    public void addRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {

    }

    @Override
    public String getSubject() throws MessagingException {
        return null;
    }

    @Override
    public void setSubject(String s) throws MessagingException {

    }

    @Override
    public Date getSentDate() throws MessagingException {
        return null;
    }

    @Override
    public void setSentDate(Date date) throws MessagingException {

    }

    @Override
    public Date getReceivedDate() throws MessagingException {
        return null;
    }

    @Override
    public Flags getFlags() throws MessagingException {
        return null;
    }

    @Override
    public void setFlags(Flags flags, boolean b) throws MessagingException {

    }

    @Override
    public Message reply(boolean b) throws MessagingException {
        return null;
    }

    @Override
    public void saveChanges() throws MessagingException {

    }

    @Override
    public int getSize() throws MessagingException {
        return 0;
    }

    @Override
    public int getLineCount() throws MessagingException {
        return 0;
    }

    @Override
    public String getContentType() throws MessagingException {
        return null;
    }

    @Override
    public boolean isMimeType(String s) throws MessagingException {
        return false;
    }

    @Override
    public String getDisposition() throws MessagingException {
        return null;
    }

    @Override
    public void setDisposition(String s) throws MessagingException {

    }

    @Override
    public String getDescription() throws MessagingException {
        return null;
    }

    @Override
    public void setDescription(String s) throws MessagingException {

    }

    @Override
    public String getFileName() throws MessagingException {
        return null;
    }

    @Override
    public void setFileName(String s) throws MessagingException {

    }

    @Override
    public InputStream getInputStream() throws IOException, MessagingException {
        return null;
    }

    @Override
    public DataHandler getDataHandler() throws MessagingException {
        return null;
    }

    @Override
    public Object getContent() throws IOException, MessagingException {
        return null;
    }

    @Override
    public void setDataHandler(DataHandler dataHandler) throws MessagingException {

    }

    @Override
    public void setContent(Object o, String s) throws MessagingException {

    }

    @Override
    public void setText(String s) throws MessagingException {

    }

    @Override
    public void setContent(Multipart multipart) throws MessagingException {

    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException, MessagingException {

    }

    @Override
    public String[] getHeader(String s) throws MessagingException {
        return new String[0];
    }

    @Override
    public void setHeader(String s, String s1) throws MessagingException {

    }

    @Override
    public void addHeader(String s, String s1) throws MessagingException {

    }

    @Override
    public void removeHeader(String s) throws MessagingException {

    }

    @Override
    public Enumeration getAllHeaders() throws MessagingException {
        return null;
    }

    @Override
    public Enumeration getMatchingHeaders(String[] strings) throws MessagingException {
        return null;
    }

    @Override
    public Enumeration getNonMatchingHeaders(String[] strings) throws MessagingException {
        return null;
    }
}
