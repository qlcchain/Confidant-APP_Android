package com.stratagile.pnrouter.ui.activity.tox

import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.tox.component.DaggerTestToxComponent
import com.stratagile.pnrouter.ui.activity.tox.contract.TestToxContract
import com.stratagile.pnrouter.ui.activity.tox.module.TestToxModule
import com.stratagile.pnrouter.ui.activity.tox.presenter.TestToxPresenter
import com.stratagile.tox.toxcore.KotlinToxService
import kotlinx.android.synthetic.main.activity_test_tox.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.tox
 * @Description: $description
 * @date 2019/02/01 12:07:44
 */

class TestToxActivity : BaseActivity(), TestToxContract.View {

    @Inject
    internal lateinit var mPresenter: TestToxPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_test_tox)
    }
    override fun initData() {
        toxCreate.setOnClickListener {
            var intent = Intent(this, KotlinToxService::class.java)
            startService(intent)
        }

        toxKill.setOnClickListener {
            var intent = Intent(this, KotlinToxService::class.java)
            stopService(intent)
        }
    }

    override fun setupActivityComponent() {
       DaggerTestToxComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .testToxModule(TestToxModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: TestToxContract.TestToxContractPresenter) {
            mPresenter = presenter as TestToxPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onBackPressed() {
        var intent = Intent(this, KotlinToxService::class.java)
        stopService(intent)
        finish()
        Process.killProcess(Process.myPid())
        super.onBackPressed()
    }

}