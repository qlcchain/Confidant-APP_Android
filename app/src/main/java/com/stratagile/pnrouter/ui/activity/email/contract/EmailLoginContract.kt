package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for EmailLoginActivity
 * @Description: $description
 * @date 2019/07/02 15:20:41
 */
interface EmailLoginContract {
    interface View : BaseView<EmailLoginContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface EmailLoginContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}