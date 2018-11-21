package com.stratagile.pnrouter.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.register.component.DaggerRegisterComponent
import com.stratagile.pnrouter.ui.activity.register.contract.RegisterContract
import com.stratagile.pnrouter.ui.activity.register.module.RegisterModule
import com.stratagile.pnrouter.ui.activity.register.presenter.RegisterPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.register
 * @Description: $description
 * @date 2018/11/12 11:53:06
 */

class RegisterActivity : BaseActivity(), RegisterContract.View , PNRouterServiceMessageReceiver.RegisterMessageCallback{
    override fun loginBack(loginRsp: JLoginRsp) {
        KLog.i(loginRsp.toString())
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("The current service is not available.")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("Too many users")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("System Error")
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                closeProgressDialog()
            }
        } else {
            runOnUiThread {
                closeProgressDialog()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
        SpUtil.putString(this, ConstantValue.username,username)
        SpUtil.putString(this, ConstantValue.routerId, routerId)
    }
    override fun registerBack(registerRsp: JRegisterRsp) {
        FileUtil.saveUserId2Local(registerRsp.params!!.userId)
        var newRouterEntity = RouterEntity()
        newRouterEntity.routerId = registerRsp.params.routeId
        newRouterEntity.userSn = registerRsp.params.userSn
        newRouterEntity.username = registerKey.text.toString()
        newRouterEntity.userId = registerRsp.params.userId
        newRouterEntity.dataFileVersion = registerRsp.params.dataFileVersion
        newRouterEntity.dataFilePay = registerRsp.params.dataFilePay
        var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
        newRouterEntity.routerName = "Router " + (localData.size + 1)
        val myRouter = MyRouter()
        myRouter.setType(0)
        myRouter.setRouterEntity(newRouterEntity)
        LocalRouterUtils.insertLocalAssets(myRouter)

        var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
        var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,login))
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
        setToorBar(false)
        setContentView(R.layout.activity_register)
    }
    override fun initData() {
        userId = FileUtil.getLocalUserId()
        EventBus.getDefault().register(this)
       /* miniScanParent.setOnClickListener {
            mPresenter.getScanPermission()
        }*/

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
        AppConfig.instance.messageReceiver!!.registerListener = this
        registerBtn.setOnClickListener {
            if (registerKey.text.toString().equals("") || userName2.text.toString().equals("") || userName3.text.toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if (!userName3.text.toString().equals(userName4.text.toString())) {
                toast(getString(R.string.Password_inconsistent))
                return@setOnClickListener
            }
            val NickName = RxEncodeTool.base64Encode2String(registerKey.text.toString().toByteArray())
            var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            var login = RegeisterReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN, userName2.text.toString(),LoginKey,NickName)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,login))
        }
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.registerListener = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        when (connectStatus.status) {
            0 -> {

            }
            1 -> {

            }
            2 -> {
                toast(R.string.Network_error)
            }
            3 -> {
                toast(R.string.Network_error)
            }
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