package com.stratagile.pnrouter.entity;

import java.io.Serializable;

/**
 * Created by ZL on 2019/8/14.
 */

public class EmailContact implements Serializable {
    private String userName;
    private String userAddress;

    public EmailContact(String userName, String userAddress) {
        this.userName = userName;
        this.userAddress = userAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}

