package com.stratagile.pnrouter.ui.activity.main.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SettingsActivity
 * @Description: $description
 * @date 2019/02/28 14:55:22
 */
interface SettingsContract {
    interface View : BaseView<SettingsContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SettingsContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}