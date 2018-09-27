package com.stratagile.pnrouter.ui.activity.router.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RouterManagementActivity
 * @Description: $description
 * @date 2018/09/26 10:29:17
 */
interface RouterManagementContract {
    interface View : BaseView<RouterManagementContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RouterManagementContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}