package com.stratagile.pnrouter.ui.activity.router.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RouterAliasSetActivity
 * @Description: $description
 * @date 2019/02/21 11:00:31
 */
interface RouterAliasSetContract {
    interface View : BaseView<RouterAliasSetContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RouterAliasSetContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}