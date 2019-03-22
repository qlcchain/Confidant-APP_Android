package com.stratagile.pnrouter.ui.activity.group.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for GroupMembersActivity
 * @Description: $description
 * @date 2019/03/22 15:19:37
 */
interface GroupMembersContract {
    interface View : BaseView<GroupMembersContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface GroupMembersContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}