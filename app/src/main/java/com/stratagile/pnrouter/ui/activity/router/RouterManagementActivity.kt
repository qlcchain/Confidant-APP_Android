package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.RouterChange
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterManagementPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.adapter.router.RouterListAdapter
import com.stratagile.pnrouter.utils.MutableListToArrayList
import com.stratagile.tox.events.ToxStatusEvent
import kotlinx.android.synthetic.main.activity_router_management.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.concurrent.thread

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
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        title.text = getString(R.string.routerManagement)
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        var selectedRouter = RouterEntity()
        routerList.forEach {
            if (it.lastCheck) {
                selectedRouter = it
                return@forEach
            }
        }
        routerList.remove(selectedRouter)
        tvRouterName.text = selectedRouter.routerName
        routerListAdapter = RouterListAdapter(routerList.MutableListToArrayList())
        recyclerView.adapter = routerListAdapter
        routerListAdapter.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(this, RouterInfoActivity::class.java)
            intent.putExtra("router", routerListAdapter.getItem(position))
            startActivity(intent)
        }
        llRoutername.setOnClickListener {
            var intent = Intent(this, RouterInfoActivity::class.java)
            intent.putExtra("router", selectedRouter)
            startActivity(intent)
        }
        if(ConstantValue.curreantNetworkType.equals("TOX"))
        {
            ivConnectStatus.visibility = View.VISIBLE
            llReConnect.visibility = View.GONE
            tvConnectStatus.text = resources.getString(R.string.successful_connection)
            ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
            avi.smoothToHide()
        }else{
            if (ConnectStatus.currentStatus == 0) {
                ivConnectStatus.visibility = View.VISIBLE
                llReConnect.visibility = View.GONE
                tvConnectStatus.text = resources.getString(R.string.successful_connection)
                ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
                avi.smoothToHide()
            } else if (ConnectStatus.currentStatus  == 1){
                ivConnectStatus.visibility = View.GONE
                llReConnect.visibility = View.GONE
                ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
                tvConnectStatus.text = resources.getString(R.string.connection)
                avi.smoothToShow()
            } else if (ConnectStatus.currentStatus  == 2){
                avi.hide()
                ivConnectStatus.visibility = View.GONE
                llReConnect.visibility = View.VISIBLE
                tvConnectStatus.text = resources.getString(R.string.failed_to_connect)
            }
        }

        llReConnect.setOnClickListener {
            thread {
                if(ConstantValue.curreantNetworkType.equals("TOX"))
                {
                    AppConfig.instance.messageReceiver!!.reConnect()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun routerChange(routerChange: RouterChange) {
        initData()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        when (toxStatusEvent.status) {
            0 -> {
                ivConnectStatus.visibility = View.VISIBLE
                llReConnect.visibility = View.GONE
                avi.smoothToHide()
                tvConnectStatus.text = resources.getString(R.string.successful_connection)
                ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
            }
            1 -> {
                ivConnectStatus.visibility = View.GONE
                llReConnect.visibility = View.GONE
                avi.smoothToShow()
                ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
                tvConnectStatus.text = resources.getString(R.string.connection)
            }
            2 -> {
                ivConnectStatus.visibility = View.GONE
                avi.hide()
                tvConnectStatus.text = resources.getString(R.string.failed_to_connect)
                llReConnect.visibility = View.GONE
            }
            3 -> {
                ivConnectStatus.visibility = View.GONE
                avi.hide()
                tvConnectStatus.text = resources.getString(R.string.Network_error)
                llReConnect.visibility = View.GONE
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange: ConnectStatus) {
        //连接状态，0已经连接，1正在连接，2未连接
        if (statusChange.status == 0) {
            ivConnectStatus.visibility = View.VISIBLE
            llReConnect.visibility = View.GONE
            avi.smoothToHide()
            tvConnectStatus.text = resources.getString(R.string.successful_connection)
            ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
        } else if (statusChange.status   == 1){
            ivConnectStatus.visibility = View.GONE
            llReConnect.visibility = View.GONE
            avi.smoothToShow()
            ivConnectStatus.setImageDrawable(resources.getDrawable(R.mipmap.icon_connected))
            tvConnectStatus.text = resources.getString(R.string.connection)
        } else if (statusChange.status   == 2){
            ivConnectStatus.visibility = View.GONE
            avi.hide()
            tvConnectStatus.text = resources.getString(R.string.failed_to_connect)
            llReConnect.visibility = View.VISIBLE
        }else if (statusChange.status   == 3){
            ivConnectStatus.visibility = View.GONE
            avi.hide()
            tvConnectStatus.text = resources.getString(R.string.Network_error)
            llReConnect.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
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
       // menuInflater.inflate(R.menu.qr_code, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.qrCode -> {
                mPresenter.getScanPermission()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun getScanPermissionSuccess() {
        var intent = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent, 1)
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
            routerEntity.lastCheck = false
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(routerEntity)
            initData()
        }
    }

}