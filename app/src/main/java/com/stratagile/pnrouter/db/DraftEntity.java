package com.stratagile.pnrouter.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DraftEntity {
    @Id(autoincrement = true)
    private Long id;
    private String content;
    private String userId;
    private String toUserId;
    private long taimeStamp;
    private int msgType;
    private int unReadCount;
    @Generated(hash = 1905195312)
    public DraftEntity(Long id, String content, String userId, String toUserId,
            long taimeStamp, int msgType, int unReadCount) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.toUserId = toUserId;
        this.taimeStamp = taimeStamp;
        this.msgType = msgType;
        this.unReadCount = unReadCount;
    }
    @Generated(hash = 165234677)
    public DraftEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getToUserId() {
        return this.toUserId;
    }
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    public long getTaimeStamp() {
        return this.taimeStamp;
    }
    public void setTaimeStamp(long taimeStamp) {
        this.taimeStamp = taimeStamp;
    }
    public int getMsgType() {
        return this.msgType;
    }
    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
    public int getUnReadCount() {
        return this.unReadCount;
    }
    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }
}
