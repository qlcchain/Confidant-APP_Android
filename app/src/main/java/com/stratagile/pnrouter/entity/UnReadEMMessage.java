package com.stratagile.pnrouter.entity;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.message.EMAMessage;

public class UnReadEMMessage {
    private int unReadCount = 0;
    private String draft;

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    private EMMessage emMessage;

    public UnReadEMMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public UnReadEMMessage(EMMessage emMessage, String draft, int unReadCount) {
        this.unReadCount = unReadCount;
        this.draft = draft;
        this.emMessage = emMessage;
    }

    public UnReadEMMessage(EMMessage emMessage, int unReadCount) {
        this.unReadCount = unReadCount;
        this.emMessage = emMessage;
    }

    public EMMessage getEmMessage() {
        return emMessage;
    }

    public void setEmMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }


}
