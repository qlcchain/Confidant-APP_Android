package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicWeiXinNodeComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicWeiXinNodeContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicWeiXinNodeModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicWeiXinNodePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/03 17:30:27
 */

class PicWeiXinNodeFragment : BaseFragment(), PicWeiXinNodeContract.View {

    @Inject
    lateinit internal var mPresenter: PicWeiXinNodePresenter

    //   @Nullable
    //   @Override
    //   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //       View view = inflater.inflate(R.layout.fragment_picWeiXinNode, null);
    //       ButterKnife.bind(this, view);
    //       Bundle mBundle = getArguments();
    //       return view;
    //   }


    override fun setupFragmentComponent() {
        DaggerPicWeiXinNodeComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .picWeiXinNodeModule(PicWeiXinNodeModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: PicWeiXinNodeContract.PicWeiXinNodeContractPresenter) {
        mPresenter = presenter as PicWeiXinNodePresenter
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