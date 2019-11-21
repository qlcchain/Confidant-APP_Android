package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionlListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionlListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionlListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionlListPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:22
 */

class PicEncryptionlListActivity : BaseActivity(), PicEncryptionlListContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionlListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_picEncryptionlList)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerPicEncryptionlListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .picEncryptionlListModule(PicEncryptionlListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionlListContract.PicEncryptionlListContractPresenter) {
            mPresenter = presenter as PicEncryptionlListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}