package com.stratagile.pnrouter.ui.activity.selectfriend.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SelectFriendCreateGroupActivity
 * @Description: $description
 * @date 2019/03/12 17:49:51
 */
interface SelectFriendCreateGroupContract {
    interface View : BaseView<SelectFriendCreateGroupContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SelectFriendCreateGroupContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}