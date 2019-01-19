package com.stratagile.pnrouter.ui.activity.admin.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for AdminUpPasswordActivity
 * @Description: $description
 * @date 2019/01/19 15:30:48
 */
interface AdminUpPasswordContract {
    interface View : BaseView<AdminUpPasswordContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface AdminUpPasswordContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}