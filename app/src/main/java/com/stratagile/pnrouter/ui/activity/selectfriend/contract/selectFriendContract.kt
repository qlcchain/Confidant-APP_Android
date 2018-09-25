package com.stratagile.pnrouter.ui.activity.selectfriend.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView

/**
 * @author zl
 * @Package The contract for selectFriendActivity
 * @Description: $description
 * @date 2018/09/25 14:58:33
 */
interface selectFriendContract {
    interface View : BaseView<selectFriendContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface selectFriendContractPresenter : BasePresenter//        /**
    //         *
    //         */
    //        void getBusinessInfo(Map map);
}