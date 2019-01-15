package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for NewFriendActivity
 * @Description: $description
 * @date 2018/09/13 21:25:01
 */
interface NewFriendContract {
    interface View : BaseView<NewFriendContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        fun getScanPermissionSuccess()
    }

    interface NewFriendContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)

        fun getScanPermission()
    }
}