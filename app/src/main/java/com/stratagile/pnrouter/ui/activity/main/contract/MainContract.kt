package com.stratagile.pnrouter.ui.activity.main.contract


import com.stratagile.pnrouter.ui.activity.base.BasePresenter
import com.stratagile.pnrouter.ui.activity.base.BaseView

/**
 * @author hzp
 * @Package The contract for MainActivity
 * @Description: $description
 * @date 2018/01/09 09:57:09
 */
interface MainContract {
    interface View : BaseView<MainContractPresenter> {
        /**
         *
         */
        fun showProgressDialog()

        /**
         *
         */
        fun closeProgressDialog()

        fun showToast()
    }

    interface MainContractPresenter : BasePresenter {
        fun latlngParseCountry(map: Map<*, *>)
        fun showToast()
        fun sendMessage(message : String)
    }
}