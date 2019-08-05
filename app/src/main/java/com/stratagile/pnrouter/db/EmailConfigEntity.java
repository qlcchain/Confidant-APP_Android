package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.SpUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailConfigEntity implements Parcelable{
    @Id(autoincrement = true)
    private Long id;

    private int smtpPort;           //SMTP端口
    private int popPort;            //POP端口
    private int imapPort;           //IMAP端口
    private String smtpHost;        //SMTP的Host
    private String popHost;         //POP的Host
    private String imapHost;        //IMAP的Host
    private String account;         //邮箱帐号
    private String emailType;       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱
    private String password;        //邮箱密码
    private int totalCount;        //Inbox消息总数
    private int unReadCount;        //Inbox未读数量

    private int nodeTotalCount;        //node消息总数
    private int nodeUReadCount;        //node未读数量

    private int starTotalCount;        //star消息总数
    private int starunReadCount;        //star未读数量
    private int drafTotalCount;        //draf消息总数
    private int drafUnReadCount;        //draf未读数量
    private int sendTotalCount;        //send消息总数
    private int sendunReadCount;        //send未读数量
    private int garbageCount;          //garbage未读邮件总数
    private int garbageUnReadCount;        //garbage未读数量
    private int deleteTotalCount;        //delete消息总数
    private int deleteUnReadCount;        //delete未读数量
    private String inboxMenu;        //收件箱
    private String nodeMenu;        //节点菜单
    private String starMenu;        //星标
    private String drafMenu;        //草稿
    private String sendMenu;        //已发送
    private String garbageMenu;      //垃圾邮件
    private String deleteMenu;       //已删除
    private Boolean inboxMenuRefresh;        //收件箱是否需要刷新
    private Boolean nodeMenuRefresh;        //节点菜单否需要刷新
    private Boolean starMenuRefresh;        //星标否需要刷新
    private Boolean drafMenuRefresh;        //草稿否需要刷新
    private Boolean sendMenuRefresh;        //已发送否需要刷新
    private Boolean garbageMenuRefresh;      //垃圾邮件否需要刷新
    private Boolean deleteMenuRefresh;       //已删除否需要刷新

    private Boolean isChoose;        //是否默认邮箱

    public EmailConfigEntity() {

    }

    protected EmailConfigEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        smtpPort = in.readInt();
        popPort = in.readInt();
        imapPort = in.readInt();
        smtpHost = in.readString();
        popHost = in.readString();
        imapHost = in.readString();
        account = in.readString();
        emailType = in.readString();
        password = in.readString();
        totalCount = in.readInt();
        unReadCount = in.readInt();
        nodeTotalCount = in.readInt();
        nodeUReadCount = in.readInt();
        starTotalCount = in.readInt();
        starunReadCount = in.readInt();
        drafTotalCount = in.readInt();
        drafUnReadCount = in.readInt();
        sendTotalCount = in.readInt();
        sendunReadCount = in.readInt();
        garbageCount = in.readInt();
        garbageUnReadCount = in.readInt();
        deleteTotalCount = in.readInt();
        deleteUnReadCount = in.readInt();
        inboxMenu = in.readString();
        nodeMenu = in.readString();
        starMenu = in.readString();
        drafMenu = in.readString();
        sendMenu = in.readString();
        garbageMenu = in.readString();
        deleteMenu = in.readString();
        byte tmpInboxMenuRefresh = in.readByte();
        inboxMenuRefresh = tmpInboxMenuRefresh == 0 ? null : tmpInboxMenuRefresh == 1;
        byte tmpNodeMenuRefresh = in.readByte();
        nodeMenuRefresh = tmpNodeMenuRefresh == 0 ? null : tmpNodeMenuRefresh == 1;
        byte tmpStarMenuRefresh = in.readByte();
        starMenuRefresh = tmpStarMenuRefresh == 0 ? null : tmpStarMenuRefresh == 1;
        byte tmpDrafMenuRefresh = in.readByte();
        drafMenuRefresh = tmpDrafMenuRefresh == 0 ? null : tmpDrafMenuRefresh == 1;
        byte tmpSendMenuRefresh = in.readByte();
        sendMenuRefresh = tmpSendMenuRefresh == 0 ? null : tmpSendMenuRefresh == 1;
        byte tmpGarbageMenuRefresh = in.readByte();
        garbageMenuRefresh = tmpGarbageMenuRefresh == 0 ? null : tmpGarbageMenuRefresh == 1;
        byte tmpDeleteMenuRefresh = in.readByte();
        deleteMenuRefresh = tmpDeleteMenuRefresh == 0 ? null : tmpDeleteMenuRefresh == 1;
        byte tmpIsChoose = in.readByte();
        isChoose = tmpIsChoose == 0 ? null : tmpIsChoose == 1;
    }

    @Generated(hash = 1289100305)
    public EmailConfigEntity(Long id, int smtpPort, int popPort, int imapPort, String smtpHost,
            String popHost, String imapHost, String account, String emailType, String password,
            int totalCount, int unReadCount, int nodeTotalCount, int nodeUReadCount, int starTotalCount,
            int starunReadCount, int drafTotalCount, int drafUnReadCount, int sendTotalCount,
            int sendunReadCount, int garbageCount, int garbageUnReadCount, int deleteTotalCount,
            int deleteUnReadCount, String inboxMenu, String nodeMenu, String starMenu, String drafMenu,
            String sendMenu, String garbageMenu, String deleteMenu, Boolean inboxMenuRefresh,
            Boolean nodeMenuRefresh, Boolean starMenuRefresh, Boolean drafMenuRefresh,
            Boolean sendMenuRefresh, Boolean garbageMenuRefresh, Boolean deleteMenuRefresh,
            Boolean isChoose) {
        this.id = id;
        this.smtpPort = smtpPort;
        this.popPort = popPort;
        this.imapPort = imapPort;
        this.smtpHost = smtpHost;
        this.popHost = popHost;
        this.imapHost = imapHost;
        this.account = account;
        this.emailType = emailType;
        this.password = password;
        this.totalCount = totalCount;
        this.unReadCount = unReadCount;
        this.nodeTotalCount = nodeTotalCount;
        this.nodeUReadCount = nodeUReadCount;
        this.starTotalCount = starTotalCount;
        this.starunReadCount = starunReadCount;
        this.drafTotalCount = drafTotalCount;
        this.drafUnReadCount = drafUnReadCount;
        this.sendTotalCount = sendTotalCount;
        this.sendunReadCount = sendunReadCount;
        this.garbageCount = garbageCount;
        this.garbageUnReadCount = garbageUnReadCount;
        this.deleteTotalCount = deleteTotalCount;
        this.deleteUnReadCount = deleteUnReadCount;
        this.inboxMenu = inboxMenu;
        this.nodeMenu = nodeMenu;
        this.starMenu = starMenu;
        this.drafMenu = drafMenu;
        this.sendMenu = sendMenu;
        this.garbageMenu = garbageMenu;
        this.deleteMenu = deleteMenu;
        this.inboxMenuRefresh = inboxMenuRefresh;
        this.nodeMenuRefresh = nodeMenuRefresh;
        this.starMenuRefresh = starMenuRefresh;
        this.drafMenuRefresh = drafMenuRefresh;
        this.sendMenuRefresh = sendMenuRefresh;
        this.garbageMenuRefresh = garbageMenuRefresh;
        this.deleteMenuRefresh = deleteMenuRefresh;
        this.isChoose = isChoose;
    }

    public static final Creator<EmailConfigEntity> CREATOR = new Creator<EmailConfigEntity>() {
        @Override
        public EmailConfigEntity createFromParcel(Parcel in) {
            return new EmailConfigEntity(in);
        }

        @Override
        public EmailConfigEntity[] newArray(int size) {
            return new EmailConfigEntity[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public int getPopPort() {
        return popPort;
    }

    public void setPopPort(int popPort) {
        this.popPort = popPort;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getPopHost() {
        return popHost;
    }

    public void setPopHost(String popHost) {
        this.popHost = popHost;
    }

    public String getImapHost() {
        return imapHost;
    }

    public void setImapHost(String imapHost) {
        this.imapHost = imapHost;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public int getNodeTotalCount() {
        return nodeTotalCount;
    }

    public void setNodeTotalCount(int nodeTotalCount) {
        this.nodeTotalCount = nodeTotalCount;
    }

    public int getNodeUReadCount() {
        return nodeUReadCount;
    }

    public void setNodeUReadCount(int nodeUReadCount) {
        this.nodeUReadCount = nodeUReadCount;
    }

    public int getStarTotalCount() {
        return starTotalCount;
    }

    public void setStarTotalCount(int starTotalCount) {
        this.starTotalCount = starTotalCount;
    }

    public int getStarunReadCount() {
        return starunReadCount;
    }

    public void setStarunReadCount(int starunReadCount) {
        this.starunReadCount = starunReadCount;
    }

    public int getDrafTotalCount() {
        return drafTotalCount;
    }

    public void setDrafTotalCount(int drafTotalCount) {
        this.drafTotalCount = drafTotalCount;
    }

    public int getDrafUnReadCount() {
        return drafUnReadCount;
    }

    public void setDrafUnReadCount(int drafUnReadCount) {
        this.drafUnReadCount = drafUnReadCount;
    }

    public int getSendTotalCount() {
        return sendTotalCount;
    }

    public void setSendTotalCount(int sendTotalCount) {
        this.sendTotalCount = sendTotalCount;
    }

    public int getSendunReadCount() {
        return sendunReadCount;
    }

    public void setSendunReadCount(int sendunReadCount) {
        this.sendunReadCount = sendunReadCount;
    }

    public int getGarbageCount() {
        return garbageCount;
    }

    public void setGarbageCount(int garbageCount) {
        this.garbageCount = garbageCount;
    }

    public int getGarbageUnReadCount() {
        return garbageUnReadCount;
    }

    public void setGarbageUnReadCount(int garbageUnReadCount) {
        this.garbageUnReadCount = garbageUnReadCount;
    }

    public int getDeleteTotalCount() {
        return deleteTotalCount;
    }

    public void setDeleteTotalCount(int deleteTotalCount) {
        this.deleteTotalCount = deleteTotalCount;
    }

    public int getDeleteUnReadCount() {
        return deleteUnReadCount;
    }

    public void setDeleteUnReadCount(int deleteUnReadCount) {
        this.deleteUnReadCount = deleteUnReadCount;
    }

    public String getInboxMenu() {
        return inboxMenu;
    }

    public void setInboxMenu(String inboxMenu) {
        this.inboxMenu = inboxMenu;
    }

    public String getNodeMenu() {
        return nodeMenu;
    }

    public void setNodeMenu(String nodeMenu) {
        this.nodeMenu = nodeMenu;
    }

    public String getStarMenu() {
        return starMenu;
    }

    public void setStarMenu(String starMenu) {
        this.starMenu = starMenu;
    }

    public String getDrafMenu() {
        return drafMenu;
    }

    public void setDrafMenu(String drafMenu) {
        this.drafMenu = drafMenu;
    }

    public String getSendMenu() {
        return sendMenu;
    }

    public void setSendMenu(String sendMenu) {
        this.sendMenu = sendMenu;
    }

    public String getGarbageMenu() {
        return garbageMenu;
    }

    public void setGarbageMenu(String garbageMenu) {
        this.garbageMenu = garbageMenu;
    }

    public String getDeleteMenu() {
        return deleteMenu;
    }

    public void setDeleteMenu(String deleteMenu) {
        this.deleteMenu = deleteMenu;
    }

    public Boolean getInboxMenuRefresh() {
        return inboxMenuRefresh;
    }

    public void setInboxMenuRefresh(Boolean inboxMenuRefresh) {
        this.inboxMenuRefresh = inboxMenuRefresh;
    }

    public Boolean getNodeMenuRefresh() {
        return nodeMenuRefresh;
    }

    public void setNodeMenuRefresh(Boolean nodeMenuRefresh) {
        this.nodeMenuRefresh = nodeMenuRefresh;
    }

    public Boolean getStarMenuRefresh() {
        return starMenuRefresh;
    }

    public void setStarMenuRefresh(Boolean starMenuRefresh) {
        this.starMenuRefresh = starMenuRefresh;
    }

    public Boolean getDrafMenuRefresh() {
        return drafMenuRefresh;
    }

    public void setDrafMenuRefresh(Boolean drafMenuRefresh) {
        this.drafMenuRefresh = drafMenuRefresh;
    }

    public Boolean getSendMenuRefresh() {
        return sendMenuRefresh;
    }

    public void setSendMenuRefresh(Boolean sendMenuRefresh) {
        this.sendMenuRefresh = sendMenuRefresh;
    }

    public Boolean getGarbageMenuRefresh() {
        return garbageMenuRefresh;
    }

    public void setGarbageMenuRefresh(Boolean garbageMenuRefresh) {
        this.garbageMenuRefresh = garbageMenuRefresh;
    }

    public Boolean getDeleteMenuRefresh() {
        return deleteMenuRefresh;
    }

    public void setDeleteMenuRefresh(Boolean deleteMenuRefresh) {
        this.deleteMenuRefresh = deleteMenuRefresh;
    }

    public Boolean getChoose() {
        return isChoose;
    }

    public void setChoose(Boolean choose) {
        isChoose = choose;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(smtpPort);
        dest.writeInt(popPort);
        dest.writeInt(imapPort);
        dest.writeString(smtpHost);
        dest.writeString(popHost);
        dest.writeString(imapHost);
        dest.writeString(account);
        dest.writeString(emailType);
        dest.writeString(password);
        dest.writeInt(totalCount);
        dest.writeInt(unReadCount);
        dest.writeInt(nodeTotalCount);
        dest.writeInt(nodeUReadCount);
        dest.writeInt(starTotalCount);
        dest.writeInt(starunReadCount);
        dest.writeInt(drafTotalCount);
        dest.writeInt(drafUnReadCount);
        dest.writeInt(sendTotalCount);
        dest.writeInt(sendunReadCount);
        dest.writeInt(garbageCount);
        dest.writeInt(garbageUnReadCount);
        dest.writeInt(deleteTotalCount);
        dest.writeInt(deleteUnReadCount);
        dest.writeString(inboxMenu);
        dest.writeString(nodeMenu);
        dest.writeString(starMenu);
        dest.writeString(drafMenu);
        dest.writeString(sendMenu);
        dest.writeString(garbageMenu);
        dest.writeString(deleteMenu);
        dest.writeByte((byte) (inboxMenuRefresh == null ? 0 : inboxMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (nodeMenuRefresh == null ? 0 : nodeMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (starMenuRefresh == null ? 0 : starMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (drafMenuRefresh == null ? 0 : drafMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (sendMenuRefresh == null ? 0 : sendMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (garbageMenuRefresh == null ? 0 : garbageMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (deleteMenuRefresh == null ? 0 : deleteMenuRefresh ? 1 : 2));
        dest.writeByte((byte) (isChoose == null ? 0 : isChoose ? 1 : 2));
    }

    public Boolean getIsChoose() {
        return this.isChoose;
    }

    public void setIsChoose(Boolean isChoose) {
        this.isChoose = isChoose;
    }
}
