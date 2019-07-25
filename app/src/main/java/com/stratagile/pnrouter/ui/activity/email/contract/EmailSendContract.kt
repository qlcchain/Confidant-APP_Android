package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for EmailSendActivity
 * @Description: $description
 * @date 2019/07/25 11:21:29
 */
interface EmailSendContract {
    interface View : BaseView<EmailSendContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface EmailSendContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}