package com.stratagile.pnrouter.ui.activity.file.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for FileSendShareActivity
 * @Description: $description
 * @date 2019/04/12 15:17:33
 */
interface FileSendShareContract {
    interface View : BaseView<FileSendShareContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FileSendShareContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}