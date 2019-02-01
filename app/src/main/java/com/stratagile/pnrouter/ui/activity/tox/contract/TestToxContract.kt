package com.stratagile.pnrouter.ui.activity.tox.contract

import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView
/**
 * @author hzp
 * @Package The contract for TestToxActivity
 * @Description: $description
 * @date 2019/02/01 12:07:44
 */
interface TestToxContract {
    interface View : BaseView<TestToxContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()
    }

    interface TestToxContractPresenter : BasePresenter {
//        /**
//         *
//         */
//        fun getBusinessInfo(map : Map)
    }
}