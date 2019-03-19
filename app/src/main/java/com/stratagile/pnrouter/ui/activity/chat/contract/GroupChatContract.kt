package com.stratagile.pnrouter.ui.activity.chat.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for GroupChatActivity
 * @Description: $description
 * @date 2019/03/18 15:06:56
 */
interface GroupChatContract {
    interface View : BaseView<GroupChatContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface GroupChatContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}