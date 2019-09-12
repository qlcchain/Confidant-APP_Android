package com.stratagile.pnrouter.ui.adapter.user;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.stratagile.pnrouter.db.UserEntity;

public class UserHead extends AbstractExpandableItem<UserItem> implements MultiItemEntity {
    private String userName;
    private String remarks; //用户备注
    private boolean isChecked = false;
    private boolean isShowRouteName = true;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public boolean isShowRouteName() {
        return isShowRouteName;
    }

    public void setShowRouteName(boolean showRouteName) {
        isShowRouteName = showRouteName;
    }
}

