package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
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
import com.stratagile.pnrouter.utils.UIUtils
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
        bottomNavigation.enableAnimation(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.setTextSize(10F)
        viewPager.offscreenPageLimit = 2
        bottomNavigation.setIconSizeAt(0, 17.6f, 21.2f)
        bottomNavigation.setIconSizeAt(1, 23.6f, 18.8f)
        bottomNavigation.setIconSizeAt(2, 22F, 18.8f)
        bottomNavigation.setIconsMarginTop(resources.getDimension(R.dimen.x22).toInt())
        bottomNavigation.selectedItemId = R.id.item_news
//        tv_hello.text = "hahhaha"
//        tv_hello.setOnClickListener {
//            mPresenter.showToast()
//            startActivity(Intent(this, TestActivity::class.java))
//        }
//        tv_hello.typeface.style
        signalServiceMessageReceiver = AppConfig.instance.messageReceiver!!
//        send.setOnClickListener {
//            if (messageSender == null) {
//                messageSender = AppConfig.instance.getPNRouterServiceMessageSender()
//            }
//            var login = LoginReq("login", "routerid", "WOZIJI", 1)
//            var jsonStr = BaseData("123343434", "MIFI", login)
//            Log.i("MainActivity", jsonStr.baseDataToJson())
//            messageSender!!.send(jsonStr)
//            edittext.text.clear()
//        }

    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        tvTitle.text = ("MainActivity")
        val llp = RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
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
        needFront = true
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }
}
