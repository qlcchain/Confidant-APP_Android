package com.stratagile.pnrouter.ui.activity.login.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for VerifyingFingerprintActivity
 * @Description: $description
 * @date 2019/02/26 14:40:52
 */
interface VerifyingFingerprintContract {
    interface View : BaseView<VerifyingFingerprintContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface VerifyingFingerprintContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}