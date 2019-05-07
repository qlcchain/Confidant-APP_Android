package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.db.UserEntity;

import java.util.ArrayList;

/**
 * Created by zl on 2019/2/21.
 */

public class MyFriend {
    private String UserName; //名称用于排序
    private String remarks; //用户备注
    private String UserKey; //key
    private UserEntity userEntity;//第一个数据
    private ArrayList<UserEntity> routerItemList;//数据列表

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public ArrayList<UserEntity> getRouterItemList() {
        return routerItemList;
    }

    public void setRouterItemList(ArrayList<UserEntity> routerItemList) {
        this.routerItemList = routerItemList;
    }
}
