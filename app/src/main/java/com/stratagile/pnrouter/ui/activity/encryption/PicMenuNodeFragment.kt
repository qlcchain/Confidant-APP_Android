package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicMenuNodeComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicMenuNodeContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicMenuNodeModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicMenuNodePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/02 16:04:58
 */

class PicMenuNodeFragment : BaseFragment(), PicMenuNodeContract.View {

    @Inject
    lateinit internal var mPresenter: PicMenuNodePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.picencry_menu_list, null);
        return view
    }

    override fun setupFragmentComponent() {
        DaggerPicMenuNodeComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .picMenuNodeModule(PicMenuNodeModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: PicMenuNodeContract.PicMenuNodeContractPresenter) {
        mPresenter = presenter as PicMenuNodePresenter
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