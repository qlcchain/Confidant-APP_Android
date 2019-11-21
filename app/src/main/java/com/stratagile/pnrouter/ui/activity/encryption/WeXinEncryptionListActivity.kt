package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWeXinEncryptionListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WeXinEncryptionListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeXinEncryptionListPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:44
 */

class WeXinEncryptionListActivity : BaseActivity(), WeXinEncryptionListContract.View {

    @Inject
    internal lateinit var mPresenter: WeXinEncryptionListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_weXinEncryptionList)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerWeXinEncryptionListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .weXinEncryptionListModule(WeXinEncryptionListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: WeXinEncryptionListContract.WeXinEncryptionListContractPresenter) {
            mPresenter = presenter as WeXinEncryptionListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}