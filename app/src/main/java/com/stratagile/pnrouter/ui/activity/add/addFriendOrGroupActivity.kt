package com.stratagile.pnrouter.ui.activity.add

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.add.component.DaggeraddFriendOrGroupComponent
import com.stratagile.pnrouter.ui.activity.add.contract.addFriendOrGroupContract
import com.stratagile.pnrouter.ui.activity.add.module.addFriendOrGroupModule
import com.stratagile.pnrouter.ui.activity.add.presenter.addFriendOrGroupPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.add
 * @Description: $description
 * @date 2019/04/02 16:08:05
 */

class addFriendOrGroupActivity : BaseActivity(), addFriendOrGroupContract.View {

    @Inject
    internal lateinit var mPresenter: addFriendOrGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_addFriendOrGroup)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggeraddFriendOrGroupComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .addFriendOrGroupModule(addFriendOrGroupModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: addFriendOrGroupContract.addFriendOrGroupContractPresenter) {
            mPresenter = presenter as addFriendOrGroupPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}