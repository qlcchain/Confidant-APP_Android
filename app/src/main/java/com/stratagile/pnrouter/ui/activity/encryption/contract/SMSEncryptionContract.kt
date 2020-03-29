package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for SMSEncryptionActivity
 * @Description: $description
 * @date 2020/01/17 14:47:42
 */
interface SMSEncryptionContract {
    interface View : BaseView<SMSEncryptionContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        abstract fun getScanPermissionSuccess()
    }

    interface SMSEncryptionContractPresenter : BasePresenter {
        abstract fun getScanPermission()
    }
}