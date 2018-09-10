package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerFileComponent
import com.stratagile.pnrouter.ui.activity.main.contract.FileContract
import com.stratagile.pnrouter.ui.activity.main.module.FileModule
import com.stratagile.pnrouter.ui.activity.main.presenter.FilePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:32:58
 */

class FileFragment : BaseFragment(), FileContract.View {

    @Inject
    lateinit internal var mPresenter: FilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file, null);
        return view
    }


    override fun setupFragmentComponent() {
        DaggerFileComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileModule(FileModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileContract.FileContractPresenter) {
        mPresenter = presenter as FilePresenter
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