package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for EmailFileAttachmentShowActivity
 * @Description: $description
 * @date 2020/05/14 10:45:17
 */
interface EmailFileAttachmentShowContract {
    interface View : BaseView<EmailFileAttachmentShowContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface EmailFileAttachmentShowContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}