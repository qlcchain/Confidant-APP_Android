package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for QRFriendCodeActivity
 * @Description: $description
 * @date 2018/12/25 11:45:06
 */
interface QRFriendCodeContract {
    interface View : BaseView<QRFriendCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface QRFriendCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}