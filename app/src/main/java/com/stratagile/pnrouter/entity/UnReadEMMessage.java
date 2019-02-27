package com.stratagile.pnrouter.entity;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.message.EMAMessage;

public class UnReadEMMessage {
    private int unReadCount = 0;
    private EMMessage emMessage;

    public UnReadEMMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public UnReadEMMessage(int unReadCount, EMMessage emMessage) {
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
