package com.stratagile.pnrouter.ui.activity.selectfriend.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for selectFriendSendFileActivity
 * @Description: $description
 * @date 2019/03/06 15:41:57
 */
interface selectFriendSendFileContract {
    interface View : BaseView<selectFriendSendFileContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface selectFriendSendFileContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}