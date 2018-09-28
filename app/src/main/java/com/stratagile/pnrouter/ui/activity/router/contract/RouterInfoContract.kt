package com.stratagile.pnrouter.ui.activity.router.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for RouterInfoActivity
 * @Description: $description
 * @date 2018/09/27 16:07:17
 */
interface RouterInfoContract {
    interface View : BaseView<RouterInfoContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface RouterInfoContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}