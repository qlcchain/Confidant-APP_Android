package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import butterknife.ButterKnife
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterAddUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAddUserContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterAddUserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAddUserPresenter
import kotlinx.android.synthetic.main.activity_select_friend.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        ButterKnife.bind(this)
        setToorBar(false)
       setContentView(R.layout.activity_router_adduser)
    }
    override fun initData() {
        fragment = UserFragment();
        val bundle = Bundle()
        bundle.putString(ConstantValue.selectFriend, "select")
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

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}