package com.stratagile.pnrouter.ui.activity.group.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for GroupChatsActivity
 * @Description: $description
 * @date 2019/03/12 15:05:01
 */
interface GroupChatsContract {
    interface View : BaseView<GroupChatsContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface GroupChatsContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}