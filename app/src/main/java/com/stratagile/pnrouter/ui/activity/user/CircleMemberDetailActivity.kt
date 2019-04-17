package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerCircleMemberDetailComponent
import com.stratagile.pnrouter.ui.activity.user.contract.CircleMemberDetailContract
import com.stratagile.pnrouter.ui.activity.user.module.CircleMemberDetailModule
import com.stratagile.pnrouter.ui.activity.user.presenter.CircleMemberDetailPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/17 10:21:23
 */

class CircleMemberDetailActivity : BaseActivity(), CircleMemberDetailContract.View {

    @Inject
    internal lateinit var mPresenter: CircleMemberDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
       setContentView(R.layout.activity_circlemember_detail)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerCircleMemberDetailComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .circleMemberDetailModule(CircleMemberDetailModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: CircleMemberDetailContract.CircleMemberDetailContractPresenter) {
            mPresenter = presenter as CircleMemberDetailPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}