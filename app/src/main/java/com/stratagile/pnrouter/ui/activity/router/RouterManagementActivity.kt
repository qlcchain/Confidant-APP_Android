package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.GetDiskTotalInfoReq
import com.stratagile.pnrouter.entity.JFormatDiskRsp
import com.stratagile.pnrouter.entity.JGetDiskTotalInfoRsp
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.RouterChange
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterManagementPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.ui.adapter.router.RouterListAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
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

class RouterManagementActivity : BaseActivity(), RouterManagementContract.View, PNRouterServiceMessageReceiver.GetDiskTotalInfoBack {
    override fun getDiskTotalInfoReq(JGetDiskTotalInfoRsp: JGetDiskTotalInfoRsp) {
        if(JGetDiskTotalInfoRsp.params.retCode == 0)
        {
            var usedCapacity = 0.0
            if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("M"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("M","").toDouble() * 100
            }else if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("G"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("G","").toDouble() * 1024 * 100
            }else  if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("T"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("T","").toDouble() * 1024 * 1024 * 100
            }
            var totalCapacity= 1.0
            if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("M"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("M","").toDouble()
            }else if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("G"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("G","").toDouble() * 1024
            }else if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("T"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("T","").toDouble() * 1024 * 1024
            }
            var precent = (usedCapacity / totalCapacity).toString()
            if(precent.length > 4)
            {
                precent = precent.substring(0,4)
            }
            runOnUiThread {
                progressBar.progress = (usedCapacity  / totalCapacity).toInt()
                UsedAndTotal.text = JGetDiskTotalInfoRsp.params.usedCapacity +" / "+JGetDiskTotalInfoRsp.params.totalCapacity +" ("+precent+"% )"
//                storage.text =  getString(R.string.Used_Sapce) + JGetDiskTotalInfoRsp.params.totalCapacity
            }
        }else{
            runOnUiThread {
                toast(R.string.system_busy)
            }
        }
    }

    override fun formatDiskReq(jFormatDiskRsp: JFormatDiskRsp) {

    }

    @Inject
    internal lateinit var mPresenter: RouterManagementPresenter

    lateinit var routerListAdapter: RouterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_management)
        EventBus.getDefault().register(this)
        showViewNeedFront()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
        userAvatar.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))+".jpg"
        userAvatar.withShape = true
        userAvatar.setImageFile(fileBase58Name)
        ivBack.setOnClickListener {
            finish()
        }
    }

    var selectedRouter = RouterEntity()
    override fun initData() {
//        title.text = getString(R.string.routerManagement)
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        routerList.forEach {
            if (it.lastCheck) {
                selectedRouter = it
                return@forEach
            }
        }
        routerList.remove(selectedRouter)
        AppConfig.instance.messageReceiver?.getDiskTotalInfoBack = this
        if(ConstantValue.currentRouterSN != null && ConstantValue.currentRouterSN .indexOf("01")== 0 && ConstantValue.currentRouterSN.equals(selectedRouter.userSn))
        {//管理员才调用此接口
            var msgData = GetDiskTotalInfoReq()
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(3, msgData))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(3, msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }

        tvRouterName.text = selectedRouter.routerName
        if(ConstantValue.curreantNetworkType.equals("TOX"))
        {

        }else{
            if (ConnectStatus.currentStatus == 0) {
            } else if (ConnectStatus.currentStatus  == 1){
            } else if (ConnectStatus.currentStatus  == 2){
            }
        }
        if(ConstantValue.currentRouterSN != null && ConstantValue.currentRouterSN .indexOf("01")== 0 && ConstantValue.currentRouterSN.equals(selectedRouter.userSn))
        {
            setUI(true)
        }else{
            setUI(false)
        }
        if (selectedRouter.routerAlias == null || "".equals(selectedRouter.routerAlias)) {
            circleAlias.tvContent.text = selectedRouter.routerName
        } else {
            circleAlias.tvContent.text = selectedRouter.routerAlias
        }
        circleAlias.setOnClickListener {
            var intent = Intent(this, EditNickNameActivity::class.java)
            intent.putExtra("flag", "Alias")
            intent.putExtra("hint", "Edit alias")
            intent.putExtra("alias", selectedRouter.routerName)
            startActivityForResult(intent, 2)
        }
        circleMembers.setOnClickListener {
            var intent = Intent(this, RouterAddUserActivity::class.java)
            intent.putExtra("userEntity", selectedRouter)
            startActivityForResult(intent, 3)
        }
        circleName.tvContent.text = selectedRouter.routerName
        circleName.setOnClickListener {
            var intent = Intent(this, EditNickNameActivity::class.java)
            intent.putExtra("flag", "Name")
            intent.putExtra("hint", "Edit Circle Name")
            intent.putExtra("alias", selectedRouter.routerName)
            startActivityForResult(intent, 4)
        }

        qrCode.setOnClickListener {
            var intent = Intent(this, RouterQRCodeActivity::class.java)
            intent.putExtra("router", selectedRouter)
            startActivity(intent)
        }

        var autoLoginRouterSn = SpUtil.getString(this, ConstantValue.autoLoginRouterSn, "")
        autoLoginSwitch.isChecked = autoLoginRouterSn.equals(selectedRouter.userSn)
        autoLoginSwitch.setOnClickListener{
            if(autoLoginSwitch.isChecked)
            {
                SpUtil.putString(this, ConstantValue.autoLoginRouterSn, selectedRouter.userSn)
            }else{
                SpUtil.putString(this, ConstantValue.autoLoginRouterSn, "")
            }
        }

        manageDisk.setOnClickListener {
            startActivity(Intent(this, DiskManagementActivity::class.java))
        }

        selectCircle.setOnClickListener {
            startActivityForResult(Intent(this, SelectCircleActivity::class.java), 5)
        }
    }


    fun setUI(isManager : Boolean) {
        if(isManager) {
            circleAlias.visibility = View.GONE

            circleName.visibility = View.VISIBLE
            circleMembers.visibility = View.VISIBLE
            userdSpace.visibility = View.VISIBLE
            manageDisk.visibility = View.VISIBLE
        } else {
            circleAlias.visibility = View.VISIBLE

            circleName.visibility = View.GONE
            circleMembers.visibility = View.GONE
            userdSpace.visibility = View.GONE
            manageDisk.visibility = View.GONE
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
            }
            1 -> {
            }
            2 -> {
            }
            3 -> {
            }
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange: ConnectStatus) {
        //连接状态，0已经连接，1正在连接，2未连接
        if (statusChange.status == 0) {
        } else if (statusChange.status   == 1){
        } else if (statusChange.status   == 2){
        }else if (statusChange.status   == 3){
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
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            selectedRouter.routerAlias = data!!.getStringExtra("alias")
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(selectedRouter)
            initData()
            EventBus.getDefault().post(RouterChange())
        }
    }

}