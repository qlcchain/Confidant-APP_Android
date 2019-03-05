package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.view.InflateException
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerLogComponent
import com.stratagile.pnrouter.ui.activity.main.contract.LogContract
import com.stratagile.pnrouter.ui.activity.main.module.LogModule
import com.stratagile.pnrouter.ui.activity.main.presenter.LogPresenter
import com.stratagile.pnrouter.ui.adapter.user.LogAdapter
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

    var logAdapter : LogAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.mainColor))
                        view.isAllCaps = false
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_log)
        title.text = "Log"
    }
    override fun initData() {
        logAdapter = LogAdapter(arrayListOf())
        recyclerView.adapter = logAdapter
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

            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}