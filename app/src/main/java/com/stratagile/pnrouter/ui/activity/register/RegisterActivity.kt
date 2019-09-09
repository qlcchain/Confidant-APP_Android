package com.stratagile.pnrouter.ui.activity.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.AESCipher
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
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.register
 * @Description: $description
 * @date 2018/11/12 11:53:06
 */

class RegisterActivity : BaseActivity(), RegisterContract.View , PNRouterServiceMessageReceiver.RegisterMessageCallback{
    var newRouterEntity = RouterEntity()
    private var exitTime: Long = 0
    override fun loginBack(loginRsp: JLoginRsp) {
        KLog.i(loginRsp.toString())
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast(R.string.need_Verification)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast(R.string.rid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast(R.string.uid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast(R.string.Validation_failed)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast(R.string.Verification_code_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 7) {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                    closeProgressDialog()
                }
            }
            else{
                runOnUiThread {
                    toast(R.string.other_error)
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast(R.string.userId_is_empty)
                closeProgressDialog()
            }
        } else {
            ConstantValue.loginOut = false
            ConstantValue.logining = true
            FileUtil.saveUserData2Local(loginRsp.params!!.userId,"userid")
            FileUtil.saveUserData2Local("","userIndex")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn,"usersn")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid,"routerid")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            newRouterEntity.userId = loginRsp.params!!.userId
            newRouterEntity.index  = ""
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            //SpUtil.putString(this, ConstantValue.username,ConstantValue.localUserName!!)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
            newRouterEntity.loginKey = userName3.text.toString().trim();
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            var myUserData = UserEntity()
            myUserData.userId = loginRsp.params!!.userId
            myUserData.nickName = newRouterEntity.username;
            UserDataManger.myUserData = myUserData
            var contains = false
            for (i in routerList) {
                if (i.userSn.equals(loginRsp.params!!.userSn)) {
                    contains = true
                    newRouterEntity = i
                    newRouterEntity.lastCheck = true
                    break
                }
            }
            var needUpdate :ArrayList<MyRouter> = ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter:MyRouter = MyRouter();
                myRouter.setType(0)
                myRouter.setRouterEntity(it)
                needUpdate.add(myRouter);
            }
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
            LocalRouterUtils.updateList(needUpdate)
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = userName3.text.toString().trim();
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            newRouterEntity.dataFileVersion = 0
            newRouterEntity.dataFilePay =  ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
            }
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            ConstantValue.currentRouterId = ConstantValue.scanRouterId
            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    override fun registerBack(registerRsp: JRegisterRsp) {
        if (registerRsp.params.retCode != 0) {
            if (registerRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("QR code has been activated by other users.")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("Error Verification Code")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Other Error")
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(registerRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                closeProgressDialog()
            }
        } else {
            runOnUiThread {
                closeProgressDialog()
            }
            ConstantValue.isNewUser = true;
            var newRouterEntity = RouterEntity()
            newRouterEntity.routerId = registerRsp.params.routeId
            newRouterEntity.userSn = registerRsp.params.userSn
            newRouterEntity.username = createName.text.toString().trim()
            newRouterEntity.userId = registerRsp.params.userId
            newRouterEntity.loginKey = userName3.text.toString().trim();
            newRouterEntity.dataFileVersion = registerRsp.params.dataFileVersion
            newRouterEntity.dataFilePay = registerRsp.params.dataFilePay
            newRouterEntity.adminId = registerRsp.params!!.adminId
            newRouterEntity.adminName = registerRsp.params!!.adminName
            newRouterEntity.adminKey = registerRsp.params!!.adminKey
            var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(registerRsp.params!!.routerName))
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() /1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            val NickName = RxEncodeTool.base64Encode2String(createName.text.toString().trim().toByteArray())
            //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
            var login = LoginReq_V4(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,signBase64, registerRsp.params.dataFileVersion,NickName)

            ConstantValue.loginReq = login
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(registerRsp.params.routeId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, registerRsp.params.routeId.substring(0, 64))
                }
            }
        }

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
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_register)
    }
    override fun initData() {
        newRouterEntity = RouterEntity()
        var flag:Int = intent.getIntExtra("flag",0)
        if(flag != null && flag == 1)
        {
            IdentifyCode.visibility = View.GONE
            IdentifyCode.setText("")
        }
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
        createName.setText(ConstantValue.localUserName!!)
        if( AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.registerListener = this
        registerBtn.setOnClickListener {
            if(flag != null && flag == 1)
            {
                if (createName.text.toString().equals("")) {
                    toast(getString(R.string.Cannot_be_empty))
                    return@setOnClickListener
                }
            }
          /*  if(flag != null && flag == 1)
            {
                if (createName.text.toString().equals("") || userName3.text.toString().equals("")) {
                    toast(getString(R.string.Cannot_be_empty))
                    return@setOnClickListener
                }
            }else{
                if (createName.text.toString().equals("") || IdentifyCode.text.toString().equals("") || userName3.text.toString().equals("")) {
                    toast(getString(R.string.Cannot_be_empty))
                    return@setOnClickListener
                }
            }

            if (!userName3.text.toString().equals(userName4.text.toString())) {
                toast(getString(R.string.Password_inconsistent))
                return@setOnClickListener
            }*/
            showProgressDialog("waiting...")
            val NickName = RxEncodeTool.base64Encode2String(createName.text.toString().trim().toByteArray())
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() /1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
            var regeister = RegeisterReq_V4( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, signBase64,pulicMiKey,NickName)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(4,regeister)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                }
            }

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

            }
            3 -> {
                closeProgressDialog()
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
    override fun onResume() {
        exitTime = System.currentTimeMillis() - 2001
        super.onResume()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitToast()
        }
        return false
    }

    fun exitToast(): Boolean {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.Press_again, Toast.LENGTH_SHORT)
                    .show()
            exitTime = System.currentTimeMillis()
        } else {
            AppConfig.instance.stopAllService()
            //android进程完美退出方法。
            var intent = Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
            this.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.runFinalizersOnExit(true);
            System.exit(0);
        }
        return false
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