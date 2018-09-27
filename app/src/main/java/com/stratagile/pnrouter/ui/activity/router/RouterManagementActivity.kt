package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterManagementPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.adapter.router.RouterListAdapter
import com.stratagile.pnrouter.utils.MutableListToArrayList
import kotlinx.android.synthetic.main.activity_router_management.*

import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/09/26 10:29:17
 */

class RouterManagementActivity : BaseActivity(), RouterManagementContract.View {

    @Inject
    internal lateinit var mPresenter: RouterManagementPresenter

    lateinit var routerListAdapter: RouterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_management)
    }
    override fun initData() {
        title.text = "Router Management"
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        var selectedRouter = RouterEntity()
        routerList.forEach {
            if (it.lastCheck) {
                selectedRouter = it
                routerList.remove(it)
                return@forEach
            }
        }
        tvRouterName.text = selectedRouter.routerName
        routerListAdapter = RouterListAdapter(routerList.MutableListToArrayList())
        recyclerView.adapter = routerListAdapter
        routerListAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(this, RouterQRCodeActivity::class.java)
            intent.putExtra("router", routerListAdapter.getItem(position))
            startActivity(intent)
        }
        tvRouterName.setOnClickListener {
            var intent = Intent(this, RouterQRCodeActivity::class.java)
            intent.putExtra("router", selectedRouter)
            startActivity(intent)
        }
    }

    override fun setupActivityComponent() {
       DaggerRouterManagementComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerManagementModule(RouterManagementModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterManagementContract.RouterManagementContractPresenter) {
            mPresenter = presenter as RouterManagementPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.qr_code, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.qrCode -> {
                var intent = Intent(this, ScanQrCodeActivity::class.java)
                startActivityForResult(intent, 1)
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            routerList.forEach {
                if (it.routerId.equals(data!!.getStringExtra("result"))) {
                    toast("this router has been added")
                    return
                }
            }
            var routerEntity = RouterEntity()
            routerEntity.routerName = ("Router " + (routerList.size + 1))
            routerEntity.routerId = data!!.getStringExtra("result")
            routerEntity.username = ""
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(routerEntity)
            initData()
        }
    }

}