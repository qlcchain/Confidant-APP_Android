package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewGroupComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewGroupContract
import com.stratagile.pnrouter.ui.activity.user.module.NewGroupModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewGroupPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/03/21 19:41:45
 */

class NewGroupFragment : BaseFragment(), NewGroupContract.View {

    @Inject
    lateinit internal var mPresenter: NewGroupPresenter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.layout_fragment_recyclerview, null);
        return view
    }

    override fun setupFragmentComponent() {
        DaggerNewGroupComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .newGroupModule(NewGroupModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: NewGroupContract.NewGroupContractPresenter) {
        mPresenter = presenter as NewGroupPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}