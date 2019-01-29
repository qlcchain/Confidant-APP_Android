package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.db.RouterEntity;
import com.stratagile.pnrouter.entity.file.UpLoadFile;

/**
 * Created by zl on 2018/9/12.
 */

public class MyFile {
    public static final int WIFI_ASSET = 0;
    public static final int VPN_ASSET_1 = 3;
    public static final int VPN_ASSET = 1;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    /**
     * 资产类型
     * 0，router
     */

    private int type;

    public String getUserSn() {
        return userSn;
    }

    public void setUserSn(String userSn) {
        this.userSn = userSn;
    }

    private String userSn;

    public UpLoadFile getUpLoadFile() {
        return upLoadFile;
    }

    public void setUpLoadFile(UpLoadFile upLoadFile) {
        this.upLoadFile = upLoadFile;
    }

    private UpLoadFile upLoadFile;
}
