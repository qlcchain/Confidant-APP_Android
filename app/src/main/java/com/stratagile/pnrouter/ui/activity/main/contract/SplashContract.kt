package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SplashActivity
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */
interface SplashContract {
    interface View : BaseView<SplashContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SplashContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}