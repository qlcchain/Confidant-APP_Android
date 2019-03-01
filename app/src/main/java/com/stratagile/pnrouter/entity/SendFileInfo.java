package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.FormatTransfer;

import java.io.Serializable;



public class SendFileInfo implements Serializable {

    private String userId;
    private String friendId;
    private String files_dir;
    private String type;  // 0 文本，1图片，2语音，3视频，4其他文件
    private String msgId;
    private String friendSignPublicKey;
    private String friendMiPublicKey;
    private int  voiceTimeLen;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFiles_dir() {
        return files_dir;
    }

    public void setFiles_dir(String files_dir) {
        this.files_dir = files_dir;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFriendSignPublicKey() {
        return friendSignPublicKey;
    }

    public void setFriendSignPublicKey(String friendSignPublicKey) {
        this.friendSignPublicKey = friendSignPublicKey;
    }

    public String getFriendMiPublicKey() {
        return friendMiPublicKey;
    }

    public void setFriendMiPublicKey(String friendMiPublicKey) {
        this.friendMiPublicKey = friendMiPublicKey;
    }

    public int getVoiceTimeLen() {
        return voiceTimeLen;
    }

    public void setVoiceTimeLen(int voiceTimeLen) {
        this.voiceTimeLen = voiceTimeLen;
    }
}
