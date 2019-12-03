package com.stratagile.pnrouter.ui.activity.encryption.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for PicWeiXinNodeFragment
 * @Description: $description
 * @date 2019/12/03 17:30:27
 */
interface PicWeiXinNodeContract {
    interface View : BaseView<PicWeiXinNodeContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface PicWeiXinNodeContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}