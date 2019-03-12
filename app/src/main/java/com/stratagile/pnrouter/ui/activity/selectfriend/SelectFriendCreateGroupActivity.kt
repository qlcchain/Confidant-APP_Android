package com.stratagile.pnrouter.ui.activity.selectfriend

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerSelectFriendCreateGroupComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendCreateGroupContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.SelectFriendCreateGroupModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.SelectFriendCreateGroupPresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: $description
 * @date 2019/03/12 17:49:51
 */

class SelectFriendCreateGroupActivity : BaseActivity(), SelectFriendCreateGroupContract.View {

    @Inject
    internal lateinit var mPresenter: SelectFriendCreateGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_select_friend_create_group)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerSelectFriendCreateGroupComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectFriendCreateGroupModule(SelectFriendCreateGroupModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectFriendCreateGroupContract.SelectFriendCreateGroupContractPresenter) {
            mPresenter = presenter as SelectFriendCreateGroupPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}