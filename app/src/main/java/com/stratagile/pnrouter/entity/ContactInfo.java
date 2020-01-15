package com.stratagile.pnrouter.entity;

public class ContactInfo {
    private String displayName;
    /** 手机号 */
    private String mobileNum;
    /** 家庭电话 */
    private String homeNum;

    public ContactInfo(String displayName, String mobileNum, String homeNum) {
        this.displayName = displayName;
        this.mobileNum = mobileNum;
        this.homeNum = homeNum;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getHomeNum() {
        return homeNum;
    }
}
