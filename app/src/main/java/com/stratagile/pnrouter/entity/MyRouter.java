package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.db.RouterEntity;

/**
 * Created by zl on 2018/9/12.
 */

public class MyRouter {
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

    public RouterEntity getRouterEntity() {
        return routerEntity;
    }

    public void setRouterEntity(RouterEntity routerEntity) {
        this.routerEntity = routerEntity;
    }

    private RouterEntity routerEntity;
}
