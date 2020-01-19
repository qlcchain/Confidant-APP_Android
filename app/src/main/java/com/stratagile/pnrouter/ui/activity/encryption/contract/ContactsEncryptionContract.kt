package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for ContactsEncryptionActivity
 * @Description: $description
 * @date 2020/01/07 15:46:53
 */
interface ContactsEncryptionContract {
    interface View : BaseView<ContactsEncryptionContractPresenter> {
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

    interface ContactsEncryptionContractPresenter : BasePresenter {
        abstract fun getScanPermission()
    }
}