package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for EmailInfoActivity
 * @Description: $description
 * @date 2019/07/15 15:18:54
 */
interface EmailInfoContract {
    interface View : BaseView<EmailInfoContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface EmailInfoContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}