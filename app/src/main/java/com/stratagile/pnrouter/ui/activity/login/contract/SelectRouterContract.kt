package com.stratagile.pnrouter.ui.activity.login.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SelectRouterActivity
 * @Description: $description
 * @date 2018/09/12 13:59:14
 */
interface SelectRouterContract {
    interface View : BaseView<SelectRouterContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SelectRouterContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}