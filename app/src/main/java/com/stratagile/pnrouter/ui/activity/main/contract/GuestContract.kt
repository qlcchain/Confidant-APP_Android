package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for GuestActivity
 * @Description: $description
 * @date 2018/09/18 14:25:55
 */
interface GuestContract {
    interface View : BaseView<GuestContractPresenter> {
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

    interface GuestContractPresenter : BasePresenter {
        abstract fun getScanPermission()
    }
}