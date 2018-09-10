package com.stratagile.pnrouter.ui.activity.login.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for LoginActivityActivity
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */
interface LoginActivityContract {
    interface View : BaseView<LoginActivityContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface LoginActivityContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}