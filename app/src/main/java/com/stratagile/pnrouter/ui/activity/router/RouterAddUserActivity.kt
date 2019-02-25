package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import butterknife.ButterKnife
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterAddUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAddUserContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterAddUserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAddUserPresenter
import kotlinx.android.synthetic.main.activity_select_friend.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 11:43:15
 */

class RouterAddUserActivity : BaseActivity(), RouterAddUserContract.View {

    @Inject
    internal lateinit var mPresenter: RouterAddUserPresenter
    var fragment: UserFragment? = null
    var routerEntity: RouterEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        ButterKnife.bind(this)
        setToorBar(false)
       setContentView(R.layout.activity_router_adduser)
    }
    override fun initData() {

        EventBus.getDefault().register(this)
        routerEntity = intent.getParcelableExtra("userEntity")
        fragment = UserFragment()
        val bundle = Bundle()
        title.text = getString(R.string.User_Management)
        bundle.putString(ConstantValue.selectFriend, "select")
        bundle.putParcelable("userEntity",routerEntity)
        fragment!!.setArguments(bundle)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragment!!
            }

            override fun getCount(): Int {
                return 1
            }
        }
        viewPager.offscreenPageLimit = 1


    }

    override fun setupActivityComponent() {
       DaggerRouterAddUserComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerAddUserModule(RouterAddUserModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterAddUserContract.RouterAddUserContractPresenter) {
            mPresenter = presenter as RouterAddUserPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}