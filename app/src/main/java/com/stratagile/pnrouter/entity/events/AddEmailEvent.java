package com.stratagile.pnrouter.entity.events;

public class AddEmailEvent {
    private String email;
    public AddEmailEvent(String email) {

        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
