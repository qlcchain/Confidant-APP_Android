package com.stratagile.pnrouter.ui.activity.file.contract


import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView

/**
 * @author zl
 * @Package The contract for FileChooseActivity
 * @Description: $description
 * @date 2018/09/28 16:46:15
 */
interface FileChooseContract {
    interface View : BaseView<FileInfosContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FileInfosContractPresenter : BasePresenter
    /**
     *
     *///        void loadFileInfos();
}