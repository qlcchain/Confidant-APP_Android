package com.stratagile.pnrouter.ui.activity.selectfriend.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for SelectFriendGroupDetailActivity
 * @Description: $description
 * @date 2019/03/21 10:15:49
 */
interface SelectFriendGroupDetailContract {
    interface View : BaseView<SelectFriendGroupDetailContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface SelectFriendGroupDetailContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}