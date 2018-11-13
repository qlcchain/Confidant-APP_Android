package com.stratagile.pnrouter.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JRegisterRsp
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.register.component.DaggerRegisterComponent
import com.stratagile.pnrouter.ui.activity.register.contract.RegisterContract
import com.stratagile.pnrouter.ui.activity.register.module.RegisterModule
import com.stratagile.pnrouter.ui.activity.register.presenter.RegisterPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.AESCipher
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.MobileSocketClient
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.register
 * @Description: $description
 * @date 2018/11/12 11:53:06
 */

class RegisterActivity : BaseActivity(), RegisterContract.View , PNRouterServiceMessageReceiver.RegisterMessageCallback{
    override fun registerBack(registerRsp: JRegisterRsp) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Inject
    internal lateinit var mPresenter: RegisterPresenter

    private var handler: Handler? = null
    val REQUEST_SELECT_ROUTER = 2
    val REQUEST_SCAN_QRCODE = 1
    var routerId = ""
    var userId = ""
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_register)
    }
    override fun initData() {
        userId = FileUtil.getLocalUserId()
        EventBus.getDefault().register(this)
        scanIcon.setOnClickListener {
            mPresenter.getScanPermission()
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_UPD_DATA -> {
                        var aa:String = msg.obj.toString()
                        var keydd2 = AESCipher.aesDecryptString(aa,"slph\$%*&^@-78231")
                        var bb = aa;
                    }
                }
            }
        }
        MobileSocketClient.getInstance().init(handler,this)
        MobileSocketClient.getInstance().receive()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        if (connectStatus.status == 0) {

        }
    }
    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun setupActivityComponent() {
       DaggerRegisterComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .registerModule(RegisterModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RegisterContract.RegisterContractPresenter) {
            mPresenter = presenter as RegisterPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            hasRouterParent.visibility = View.VISIBLE
            miniScanParent.visibility = View.VISIBLE
            scanParent.visibility = View.INVISIBLE
            noRoutergroup.visibility = View.INVISIBLE
            var result = data!!.getStringExtra("result");
            var soureData:ByteArray =  AESCipher.aesDecryptByte(result,"welcometoqlc0101")
            val keyId = ByteArray(6) //密钥ID
            val RouterId = ByteArray(76) //路由器id
            val UserSn = ByteArray(32)  //用户SN
            System.arraycopy(soureData, 0, keyId, 0, 6)
            System.arraycopy(soureData, 6, RouterId, 0, 76)
            System.arraycopy(soureData, 82, UserSn, 0, 32)
            var aa = soureData;
               /*var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
              if (routerList != null && routerList.size != 0) {
                   routerList.forEach { itt ->
                       if (itt.routerId.equals(data!!.getStringExtra("result"))) {
                           routerName?.setText(itt.routerName)
                           routerId = data!!.getStringExtra("result")
                           KLog.i("routerId为：" + routerId)
                           routerList.forEach {
                               if (it.lastCheck) {
                                   it.lastCheck = false
                                   AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                               }
                           }
                           itt.lastCheck = true
                           AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(itt)
                           return
                       }
                   }
                   routerId = data!!.getStringExtra("result")
                   routerName.text = "Router " + (routerList.size + 1)
                //newRouterEntity.routerId = data!!.getStringExtra("result")
                return
            } else {
                routerName?.setText("Router 1")
                routerId = data!!.getStringExtra("result")
            }*/
            return
        }
    }
}