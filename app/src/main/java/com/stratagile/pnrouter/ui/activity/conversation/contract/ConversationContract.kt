package com.stratagile.pnrouter.ui.activity.conversation.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for ConversationActivity
 * @Description: $description
 * @date 2018/09/13 16:38:48
 */
interface ConversationContract {
    interface View : BaseView<ConversationContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ConversationContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}