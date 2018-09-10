package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for ContactFragment
 * @Description: $description
 * @date 2018/09/10 17:33:27
 */
interface ContactContract {
    interface View : BaseView<ContactContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ContactContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}