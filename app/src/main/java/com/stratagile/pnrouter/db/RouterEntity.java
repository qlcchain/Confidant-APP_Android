package com.stratagile.pnrouter.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RouterEntity {
    @Id(autoincrement = true)
    private Long id;

    private String routerId;
    private String username;
    private String userId;
    private String routerName;

    @Generated(hash = 1870467570)
    public RouterEntity(Long id, String routerId, String username, String userId,
            String routerName) {
        this.id = id;
        this.routerId = routerId;
        this.username = username;
        this.userId = userId;
        this.routerName = routerName;
    }

    @Generated(hash = 997370902)
    public RouterEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouterId() {
        return this.routerId;
    }

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouterName() {
        return this.routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

}
