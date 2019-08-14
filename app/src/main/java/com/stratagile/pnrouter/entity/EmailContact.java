package com.stratagile.pnrouter.entity;

import java.io.Serializable;

/**
 * Created by ZL on 2019/8/14.
 */

public class EmailContact implements Serializable {
    private int userName;
    private int userAddress;

    public int getUserName() {
        return userName;
    }

    public void setUserName(int userName) {
        this.userName = userName;
    }

    public int getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(int userAddress) {
        this.userAddress = userAddress;
    }
}

