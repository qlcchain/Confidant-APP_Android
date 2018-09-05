package com.stratagile.pnrouter.base;

import android.app.Activity;

/**
 * @author zhaoyun
 * @desc 功能描述
 * @date 2016/7/22 16:33
 */
public interface UIContainer {

    Activity getContainerActivity();

    boolean isContainerDead();

}
