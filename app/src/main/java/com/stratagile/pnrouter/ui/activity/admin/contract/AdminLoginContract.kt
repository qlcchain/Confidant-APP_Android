package com.stratagile.pnrouter.ui.activity.admin.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for AdminLoginActivity
 * @Description: $description
 * @date 2019/01/19 15:30:16
 */
interface AdminLoginContract {
    interface View : BaseView<AdminLoginContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface AdminLoginContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}