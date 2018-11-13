package com.stratagile.pnrouter.ui.activity.register.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for RegisterActivity
 * @Description: $description
 * @date 2018/11/12 11:53:06
 */
interface RegisterContract {
    interface View : BaseView<RegisterContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        abstract fun getScanPermissionSuccess()
    }

    interface RegisterContractPresenter : BasePresenter {
        abstract fun getScanPermission()
    }
}