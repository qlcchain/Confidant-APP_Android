package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.widget.RelativeLayout
import com.pawegio.kandroid.e
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageSender
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.ui.activity.conversation.ConversationListFragment
import com.stratagile.pnrouter.ui.activity.test.TestActivity
import com.stratagile.pnrouter.utils.UIUtils
import com.stratagile.pnrouter.utils.baseDataToJson
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import android.support.v4.view.ViewPager
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.stratagile.pnrouter.R.id.statusBar
import com.stratagile.pnrouter.R.id.tvTitle


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View {
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
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when(position) {
                    0 -> return ConversationListFragment()
                    1 -> return FileFragment()
                    2 -> return ContactFragment()
                    else -> return MyFragment()
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        // 为ViewPager添加页面改变事件
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                // 将当前的页面对应的底部标签设为选中状态
                bottomNavigation.getMenu().getItem(position).setChecked(true)
                when (position) {
                    0 -> setToNews()
                    1 -> setToFile()
                    2 -> setToContact()
                    3 -> setToMy()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        // 为bnv设置选择监听事件
        bottomNavigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.getItemId()) {
                R.id.item_news -> viewPager.setCurrentItem(0, false)
                R.id.item_file -> viewPager.setCurrentItem(1, false)
                R.id.item_contacts -> viewPager.setCurrentItem(2, false)
                R.id.item_my -> viewPager.setCurrentItem(3, false)
            }
            true
        }
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

    fun setToNews() {
        tvTitle.text = getString(R.string.news)
    }

    fun setToFile() {
        tvTitle.text = getString(R.string.file)
    }

    fun setToContact() {
        tvTitle.text = getString(R.string.contacts)
    }

    fun setToMy() {
        tvTitle.text = getString(R.string.my)
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        tvTitle.text = getString(R.string.news)
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
