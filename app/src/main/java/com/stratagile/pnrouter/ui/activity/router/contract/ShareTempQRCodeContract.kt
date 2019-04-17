package com.stratagile.pnrouter.ui.activity.router.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for ShareTempQRCodeActivity
 * @Description: $description
 * @date 2019/04/17 14:04:59
 */
interface ShareTempQRCodeContract {
    interface View : BaseView<ShareTempQRCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ShareTempQRCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}