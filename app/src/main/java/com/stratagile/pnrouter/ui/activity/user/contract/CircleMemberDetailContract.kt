package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for CircleMemberDetailActivity
 * @Description: $description
 * @date 2019/04/17 10:21:23
 */
interface CircleMemberDetailContract {
    interface View : BaseView<CircleMemberDetailContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface CircleMemberDetailContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}