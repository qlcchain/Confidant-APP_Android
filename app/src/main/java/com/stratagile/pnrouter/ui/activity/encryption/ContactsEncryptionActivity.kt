package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import android.os.Environment
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerContactsEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.ContactsEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.ContactsEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ContactsEncryptionPresenter
import com.stratagile.pnrouter.utils.FileUtil
import kotlinx.android.synthetic.main.picencry_contacts_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/07 15:46:53
 */

class ContactsEncryptionActivity : BaseActivity(), ContactsEncryptionContract.View {
    override fun getScanPermissionSuccess() {


        var toPath = Environment.getExternalStorageDirectory().toString()+"/test.vcf";
        FileUtil.exportContacts(this,toPath);
    }

    @Inject
    internal lateinit var mPresenter: ContactsEncryptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_contacts_list)
    }
    override fun initData() {
        selectNodeBtn.setOnClickListener {
            mPresenter.getScanPermission()
        }
        recoveryBtn.setOnClickListener {

        }
    }

    override fun setupActivityComponent() {
       DaggerContactsEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .contactsEncryptionModule(ContactsEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ContactsEncryptionContract.ContactsEncryptionContractPresenter) {
            mPresenter = presenter as ContactsEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}