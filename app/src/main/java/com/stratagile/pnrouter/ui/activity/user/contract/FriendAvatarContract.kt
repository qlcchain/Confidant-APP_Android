package com.stratagile.pnrouter.ui.activity.user.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author zl
 * @Package The contract for FriendAvatarActivity
 * @Description: $description
 * @date 2019/04/11 18:10:07
 */
interface FriendAvatarContract {
    interface View : BaseView<FriendAvatarContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface FriendAvatarContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}