package com.stratagile.pnrouter.ui.activity.main

import android.content.ClipData
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerLogComponent
import com.stratagile.pnrouter.ui.activity.main.contract.LogContract
import com.stratagile.pnrouter.ui.activity.main.module.LogModule
import com.stratagile.pnrouter.ui.activity.main.presenter.LogPresenter
import com.stratagile.pnrouter.ui.adapter.user.LogAdapter
import com.stratagile.pnrouter.utils.LogUtil
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.ease_search_bar.*

import javax.inject.Inject;
import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import com.pawegio.kandroid.toast


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/18 09:45:46
 */

class LogActivity : BaseActivity(), LogContract.View, LogUtil.OnLogListener {
    override fun onLog(string: String) {
        runOnUiThread {
            logAdapter?.addData(string)
        }
    }

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
                } catch (e: Exception) {
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
        LogUtil.onLogListener = this
        title.text = "Log"
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fiter(s.toString())
                if (s.length > 0) {
                    search_clear.setVisibility(View.VISIBLE)
                } else {
                    search_clear.setVisibility(View.INVISIBLE)
                    logAdapter?.setNewData(LogUtil.logList)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {

            }
        })
        search_clear.setOnClickListener(View.OnClickListener {
            query.getText().clear()
            logAdapter?.setNewData(LogUtil.logList)
        })
    }

    fun fiter(key:String)
    {
        var contactListTemp:ArrayList<String> = arrayListOf<String>()
        for (i in LogUtil.logList) {
            if(i.toLowerCase().contains(key))
            {
                contactListTemp.add(i)
            }
        }
        logAdapter?.setNewData(contactListTemp)
    }

    override fun onDestroy() {
        LogUtil.onLogListener = null
        super.onDestroy()
    }
    override fun initData() {
        logAdapter = LogAdapter(LogUtil.logList)
        recyclerView.adapter = logAdapter
        recyclerView.smoothScrollToPosition(logAdapter!!.data.size)
        logAdapter?.setOnItemClickListener { adapter, view, position ->
            val myClipboard: ClipboardManager
            myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData
            myClip = ClipData.newPlainText("text", logAdapter?.getItem(position)!!)
            myClipboard.setPrimaryClip(myClip)
            toast("copy success")
        }
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
                LogUtil.logList.clear()
                logAdapter?.setNewData(LogUtil.logList)
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}