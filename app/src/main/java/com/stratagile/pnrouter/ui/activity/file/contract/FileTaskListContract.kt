package com.stratagile.pnrouter.ui.activity.file.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for FileTaskListActivity
 * @Description: $description
 * @date 2019/01/25 16:21:04
 */
interface FileTaskListContract {
    interface View : BaseView<FileTaskListContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FileTaskListContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}