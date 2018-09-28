package com.stratagile.pnrouter.ui.activity.file.contract


import com.stratagile.pnrouter.data.fileInfo.FileInfo
import com.stratagile.pnrouter.data.fileInfo.FileInfosRepository
import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView

/**
 * @author zl
 * @Package The contract for FileInfosFragment
 * @Description: $description
 * @date 2018/09/28 16:46:15
 */
interface FileInfosContract {
    interface View : BaseView<FileInfosContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        fun showNoFileInfos()

        fun showFileInfos(fileInfos: List<FileInfo>)
    }

    interface FileInfosContractPresenter : BasePresenter {
        //        /**
        //         *
        //         */
        fun loadFileInfos()

        fun init(fileInfosRepository: FileInfosRepository, fileInfo: FileInfo)
    }
}