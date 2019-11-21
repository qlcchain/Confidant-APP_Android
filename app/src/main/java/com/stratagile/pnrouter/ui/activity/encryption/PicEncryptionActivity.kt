package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:26:11
 */

class PicEncryptionActivity : BaseActivity(), PicEncryptionContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_picEncryption)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerPicEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .picEncryptionModule(PicEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionContract.PicEncryptionContractPresenter) {
            mPresenter = presenter as PicEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}