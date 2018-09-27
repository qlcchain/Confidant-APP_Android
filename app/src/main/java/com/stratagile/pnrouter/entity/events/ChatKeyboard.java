package com.stratagile.pnrouter.entity.events;

public class ChatKeyboard {
    private boolean lock;

    public boolean isLock() {
        return lock;
    }

    public ChatKeyboard(boolean lock) {
        this.lock = lock;
    }

    public void setLock(boolean lock) {

        this.lock = lock;
    }
}
