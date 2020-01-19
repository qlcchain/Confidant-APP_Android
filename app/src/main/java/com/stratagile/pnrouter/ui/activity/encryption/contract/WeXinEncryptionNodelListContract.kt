package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for WeXinEncryptionNodelListActivity
 * @Description: $description
 * @date 2019/12/26 10:33:40
 */
interface WeXinEncryptionNodelListContract {
    interface View : BaseView<WeXinEncryptionNodelListContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface WeXinEncryptionNodelListContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}