package com.stratagile.pnrouter.entity.events;

public class SendEmailEvent {
    private String email;
    public SendEmailEvent(String email) {

        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
