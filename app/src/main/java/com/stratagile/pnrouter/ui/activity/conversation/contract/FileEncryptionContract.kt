package com.stratagile.pnrouter.ui.activity.conversation.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for FileEncryptionFragment
 * @Description: $description
 * @date 2019/11/20 10:12:15
 */
interface FileEncryptionContract {
    interface View : BaseView<FileEncryptionContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        abstract fun getScanPermissionSuccess()
        abstract fun getSMSPermissionSuccess()
        abstract fun getScanPermissionFaile()
        abstract fun getSMSPermissionFaile()
    }

    interface FileEncryptionContractPresenter : BasePresenter {
        abstract fun getScanPermission()
        abstract fun getSMSPermission()
    }
}