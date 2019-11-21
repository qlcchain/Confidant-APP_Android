package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWeiXinEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeiXinEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WeiXinEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeiXinEncryptionPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:26:37
 */

class WeiXinEncryptionActivity : BaseActivity(), WeiXinEncryptionContract.View {

    @Inject
    internal lateinit var mPresenter: WeiXinEncryptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_weiXinEncryption)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerWeiXinEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .weiXinEncryptionModule(WeiXinEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: WeiXinEncryptionContract.WeiXinEncryptionContractPresenter) {
            mPresenter = presenter as WeiXinEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}