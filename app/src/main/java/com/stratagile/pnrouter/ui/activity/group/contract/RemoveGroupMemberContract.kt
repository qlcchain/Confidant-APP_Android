package com.stratagile.pnrouter.ui.activity.group.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RemoveGroupMemberActivity
 * @Description: $description
 * @date 2019/03/14 10:20:11
 */
interface RemoveGroupMemberContract {
    interface View : BaseView<RemoveGroupMemberContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RemoveGroupMemberContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}