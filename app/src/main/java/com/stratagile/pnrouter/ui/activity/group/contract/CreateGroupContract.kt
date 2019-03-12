package com.stratagile.pnrouter.ui.activity.group.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for CreateGroupActivity
 * @Description: $description
 * @date 2019/03/12 15:29:49
 */
interface CreateGroupContract {
    interface View : BaseView<CreateGroupContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface CreateGroupContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}