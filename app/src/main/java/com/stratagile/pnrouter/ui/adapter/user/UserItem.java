package com.stratagile.pnrouter.ui.adapter.user;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.stratagile.pnrouter.db.UserEntity;

public class UserItem implements MultiItemEntity {
    private UserEntity userEntity;

    public UserItem(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}

