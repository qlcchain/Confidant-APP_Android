package com.stratagile.pnrouter.ui.activity.router.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RouterQRCodeActivity
 * @Description: $description
 * @date 2018/09/26 11:53:16
 */
interface RouterQRCodeContract {
    interface View : BaseView<RouterQRCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RouterQRCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}