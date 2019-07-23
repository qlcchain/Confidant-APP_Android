package com.stratagile.pnrouter.ui.activity.email.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SelectEmailFriendActivity
 * @Description: $description
 * @date 2019/07/23 17:37:47
 */
interface SelectEmailFriendContract {
    interface View : BaseView<SelectEmailFriendContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SelectEmailFriendContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}