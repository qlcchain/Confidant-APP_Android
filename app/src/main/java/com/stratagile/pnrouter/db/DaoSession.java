package com.stratagile.pnrouter.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.stratagile.pnrouter.db.RouterEntity;

import com.stratagile.pnrouter.db.RouterEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig routerEntityDaoConfig;

    private final RouterEntityDao routerEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        routerEntityDaoConfig = daoConfigMap.get(RouterEntityDao.class).clone();
        routerEntityDaoConfig.initIdentityScope(type);

        routerEntityDao = new RouterEntityDao(routerEntityDaoConfig, this);

        registerDao(RouterEntity.class, routerEntityDao);
    }
    
    public void clear() {
        routerEntityDaoConfig.clearIdentityScope();
    }

    public RouterEntityDao getRouterEntityDao() {
        return routerEntityDao;
    }

}