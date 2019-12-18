package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SelectNodeMenuActivity
 * @Description: $description
 * @date 2019/12/18 09:47:54
 */
interface SelectNodeMenuContract {
    interface View : BaseView<SelectNodeMenuContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SelectNodeMenuContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}