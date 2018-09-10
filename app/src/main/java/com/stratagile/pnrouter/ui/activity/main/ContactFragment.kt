package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerContactComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ContactContract
import com.stratagile.pnrouter.ui.activity.main.module.ContactModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:33:27
 */

class ContactFragment : BaseFragment(), ContactContract.View {

    @Inject
    lateinit internal var mPresenter: ContactPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_contact, null);
        return view
    }


    override fun setupFragmentComponent() {
        DaggerContactComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .contactModule(ContactModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ContactContract.ContactContractPresenter) {
        mPresenter = presenter as ContactPresenter
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