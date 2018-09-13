package com.stratagile.pnrouter.ui.activity.chat.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for ChatActivity
 * @Description: $description
 * @date 2018/09/13 13:18:46
 */
interface ChatContract {
    interface View : BaseView<ChatContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ChatContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}