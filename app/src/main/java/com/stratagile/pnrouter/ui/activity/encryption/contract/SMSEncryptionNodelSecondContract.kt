package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SMSEncryptionNodelSecondActivity
 * @Description: $description
 * @date 2020/02/07 23:33:10
 */
interface SMSEncryptionNodelSecondContract {
    interface View : BaseView<SMSEncryptionNodelSecondContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SMSEncryptionNodelSecondContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}