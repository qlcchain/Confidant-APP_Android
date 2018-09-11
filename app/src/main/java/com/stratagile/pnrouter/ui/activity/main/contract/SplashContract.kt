package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SplashActivity
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */
interface SplashContract {
    interface View : BaseView<SplashContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        /**
         * 登录成功，跳转到主页面
         */
        fun loginSuccees()

        /**
         * 跳转到登录界面
         */
        fun jumpToLogin()

        /**
         * 跳转到欢迎页面
         */
        fun jumpToGuest()
    }

    interface SplashContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)

        fun observeJump()

        /**
         * 申请需要的权限,如果权限不够,在某些手机上比如小米.会发生闪退现象
         */
        fun getPermission()

        /**
         * 尝试自动登录
         */
        fun doAutoLogin()

        /**
         * 获取最新的版本
         */
        fun getLastVersion()
    }
}