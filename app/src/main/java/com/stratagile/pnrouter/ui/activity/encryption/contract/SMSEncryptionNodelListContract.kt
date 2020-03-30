package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SMSEncryptionNodelListActivity
 * @Description: $description
 * @date 2020/02/05 14:49:08
 */
interface SMSEncryptionNodelListContract {
    interface View : BaseView<SMSEncryptionNodelListContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SMSEncryptionNodelListContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}