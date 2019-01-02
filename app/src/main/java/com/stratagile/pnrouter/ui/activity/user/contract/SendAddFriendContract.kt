package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SendAddFriendActivity
 * @Description: $description
 * @date 2019/01/02 11:19:43
 */
interface SendAddFriendContract {
    interface View : BaseView<SendAddFriendContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SendAddFriendContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}