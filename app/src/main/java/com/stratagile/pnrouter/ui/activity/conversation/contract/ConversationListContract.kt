package com.stratagile.pnrouter.ui.activity.conversation.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for ConversationListFragment
 * @Description: $description
 * @date 2018/09/10 17:25:57
 */
interface ConversationListContract {
    interface View : BaseView<ConversationListContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ConversationListContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}