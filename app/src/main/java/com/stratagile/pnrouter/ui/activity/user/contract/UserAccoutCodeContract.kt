package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for UserAccoutCodeActivity
 * @Description: $description
 * @date 2019/04/12 19:25:26
 */
interface UserAccoutCodeContract {
    interface View : BaseView<UserAccoutCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface UserAccoutCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}