package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for WexinChatActivity
 * @Description: $description
 * @date 2019/12/27 16:17:50
 */
interface WexinChatContract {
    interface View : BaseView<WexinChatContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface WexinChatContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}