package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for WeiXinEncryptionActivity
 * @Description: $description
 * @date 2019/11/21 15:26:37
 */
interface WeiXinEncryptionContract {
    interface View : BaseView<WeiXinEncryptionContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface WeiXinEncryptionContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}