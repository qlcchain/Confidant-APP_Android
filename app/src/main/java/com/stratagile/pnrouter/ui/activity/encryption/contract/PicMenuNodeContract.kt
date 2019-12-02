package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for PicMenuNodeFragment
 * @Description: $description
 * @date 2019/12/02 16:04:58
 */
interface PicMenuNodeContract {
    interface View : BaseView<PicMenuNodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface PicMenuNodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}