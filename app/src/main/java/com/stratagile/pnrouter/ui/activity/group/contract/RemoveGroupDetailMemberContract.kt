package com.stratagile.pnrouter.ui.activity.group.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RemoveGroupDetailMemberActivity
 * @Description: $description
 * @date 2019/03/21 10:15:05
 */
interface RemoveGroupDetailMemberContract {
    interface View : BaseView<RemoveGroupDetailMemberContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RemoveGroupDetailMemberContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}