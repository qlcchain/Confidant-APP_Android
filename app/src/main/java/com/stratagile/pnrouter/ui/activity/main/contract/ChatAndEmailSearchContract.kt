package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for ChatAndEmailSearchFragment
 * @Description: $description
 * @date 2019/08/13 15:32:23
 */
interface ChatAndEmailSearchContract {
    interface View : BaseView<ChatAndEmailSearchContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ChatAndEmailSearchContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}