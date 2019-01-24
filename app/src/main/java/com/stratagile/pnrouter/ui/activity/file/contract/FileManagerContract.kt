package com.stratagile.pnrouter.ui.activity.file.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for FileManagerActivity
 * @Description: $description
 * @date 2019/01/23 14:15:29
 */
interface FileManagerContract {
    interface View : BaseView<FileManagerContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FileManagerContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}