package com.stratagile.pnrouter.ui.activity.admin.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for AdminUpCodeActivity
 * @Description: $description
 * @date 2019/01/19 15:31:09
 */
interface AdminUpCodeContract {
    interface View : BaseView<AdminUpCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface AdminUpCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}