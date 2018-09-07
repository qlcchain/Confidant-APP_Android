package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageSender
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.ui.activity.test.TestActivity
import com.stratagile.pnrouter.utils.baseDataToJson
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View {
    var isTrue = false
    override fun showToast() {
        toast("点击啦。。。。哈哈哈")
        showProgressDialog()

    }

    @Inject
    internal lateinit var mPresenter: MainPresenter

    var  messageSender :PNRouterServiceMessageSender? = null
    lateinit var signalServiceMessageReceiver: PNRouterServiceMessageReceiver
    override fun setPresenter(presenter: MainContract.MainContractPresenter) {
        mPresenter = presenter as MainPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun initData() {
        MessageRetrievalService.registerActivityStarted(this)
        tv_hello.text = "hahhaha"
        tv_hello.setOnClickListener {
            mPresenter.showToast()
            startActivity(Intent(this, TestActivity::class.java))
        }
        tv_hello.typeface.style
        signalServiceMessageReceiver = AppConfig.instance.messageReceiver!!
        send.setOnClickListener {
            if (messageSender == null) {
                messageSender = AppConfig.instance.getPNRouterServiceMessageSender()
            }
            var login = LoginReq("login", "routerid", "WOZIJI", 1)
            Log.i("MainActivity", login.Action)
            var jsonStr = BaseData("123343434", "MIFI", login).baseDataToJson()
            Log.i("MainActivity", jsonStr)
            if (messageSender!!.send(jsonStr)) {
                edittext.text.clear()
            }
        }

    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        setTitle("MainActivity")
    }

    override fun setupActivityComponent() {
        DaggerMainComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }
}
