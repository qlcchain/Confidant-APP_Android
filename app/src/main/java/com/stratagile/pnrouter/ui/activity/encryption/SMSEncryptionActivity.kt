package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.SMSEntityDao
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionPresenter
import com.stratagile.pnrouter.utils.FileUtil
import kotlinx.android.synthetic.main.picencry_sms_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/17 14:47:42
 */

class SMSEncryptionActivity : BaseActivity(), SMSEncryptionContract.View {
    override fun getScanPermissionSuccess() {
        var count = FileUtil.getAllSmsCount(this@SMSEncryptionActivity)
        runOnUiThread {
            localContacts.text = count.toString();
        }
        var smsDataList = FileUtil.getAllSms(this@SMSEncryptionActivity)
        for(item in smsDataList)
        {
            var list = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().where(SMSEntityDao.Properties.SmsId.eq(item.smsId)).list()
            if(list == null || list!!.size == 0)
            {
                AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.insert(item)
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_sms_list)
    }
    override fun initData() {
        title.text = getString(R.string.SMS)
        localRoot.setOnClickListener {
            startActivity(Intent(this, SMSEncryptionListActivity::class.java))
        }
        nodeRoot.setOnClickListener {
            startActivity(Intent(this, SMSEncryptionNodelListActivity::class.java))
        }
        mPresenter.getScanPermission()
    }

    override fun setupActivityComponent() {
       DaggerSMSEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionModule(SMSEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionContract.SMSEncryptionContractPresenter) {
            mPresenter = presenter as SMSEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}