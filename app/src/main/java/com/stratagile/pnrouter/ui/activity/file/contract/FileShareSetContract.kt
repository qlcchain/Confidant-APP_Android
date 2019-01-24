package com.stratagile.pnrouter.ui.activity.file.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for FileShareSetActivity
 * @Description: $description
 * @date 2019/01/24 10:26:38
 */
interface FileShareSetContract {
    interface View : BaseView<FileShareSetContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FileShareSetContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}