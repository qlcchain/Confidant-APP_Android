package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerLogComponent
import com.stratagile.pnrouter.ui.activity.main.contract.LogContract
import com.stratagile.pnrouter.ui.activity.main.module.LogModule
import com.stratagile.pnrouter.ui.activity.main.presenter.LogPresenter
import com.stratagile.pnrouter.utils.LogUtil
import kotlinx.android.synthetic.main.activity_log.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/18 09:45:46
 */

class LogActivity : BaseActivity(), LogContract.View {

    @Inject
    internal lateinit var mPresenter: LogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_log)
        title.text = "Log"
    }
    override fun initData() {
        tvLog.text = LogUtil.mLogInfo.toString()
    }

    override fun setupActivityComponent() {
       DaggerLogComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .logModule(LogModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: LogContract.LogContractPresenter) {
            mPresenter = presenter as LogPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.clear_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                LogUtil.mLogInfo.setLength(0)
                tvLog.text = LogUtil.mLogInfo.toString()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}