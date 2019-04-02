package com.stratagile.pnrouter.ui.activity.add.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for addFriendOrGroupActivity
 * @Description: $description
 * @date 2019/04/02 16:08:05
 */
interface addFriendOrGroupContract {
    interface View : BaseView<addFriendOrGroupContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface addFriendOrGroupContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}