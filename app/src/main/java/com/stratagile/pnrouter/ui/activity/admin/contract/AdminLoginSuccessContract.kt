package com.stratagile.pnrouter.ui.activity.admin.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for AdminLoginSuccessActivity
 * @Description: $description
 * @date 2019/01/19 17:18:46
 */
interface AdminLoginSuccessContract {
    interface View : BaseView<AdminLoginSuccessContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface AdminLoginSuccessContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}