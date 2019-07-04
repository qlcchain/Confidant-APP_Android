package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for EmailMainActivity
 * @Description: $description
 * @date 2019/07/02 15:22:53
 */
interface EmailMainContract {
    interface View : BaseView<EmailMainContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface EmailMainContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}