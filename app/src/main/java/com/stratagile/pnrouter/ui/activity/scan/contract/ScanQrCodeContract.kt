package com.stratagile.pnrouter.ui.activity.scan.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for ScanQrCodeActivity
 * @Description: $description
 * @date 2018/09/11 15:29:14
 */
interface ScanQrCodeContract {
    interface View : BaseView<ScanQrCodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface ScanQrCodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}