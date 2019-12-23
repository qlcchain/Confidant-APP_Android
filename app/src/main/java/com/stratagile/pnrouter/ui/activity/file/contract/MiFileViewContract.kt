package com.stratagile.pnrouter.ui.activity.file.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for MiFileViewActivity
 * @Description: $description
 * @date 2019/12/23 18:07:51
 */
interface MiFileViewContract {
    interface View : BaseView<MiFileViewContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface MiFileViewContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}