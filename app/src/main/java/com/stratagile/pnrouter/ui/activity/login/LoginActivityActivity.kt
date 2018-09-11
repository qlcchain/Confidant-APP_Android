package com.stratagile.pnrouter.ui.activity.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.ConstantValue.Companion.routerId
import com.stratagile.pnrouter.constant.ConstantValue.Companion.userId
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageSender
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.entity.LoginRsp
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.fingerprint.MyAuthCallback.*
import com.stratagile.pnrouter.ui.activity.login.component.DaggerLoginActivityComponent
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

import javax.inject.Inject;
import kotlin.math.log


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View, PNRouterServiceMessageReceiver.LoginMessageCallback {
    override fun loginBack(loginRsp: LoginRsp) {
        KLog.i(loginRsp.toString())
        runOnUiThread {
            closeProgressDialog()
        }
        if ("".equals(loginRsp.UserId)) {
            runOnUiThread {
                toast("Too many users")
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            SpUtil.putString(this, ConstantValue.userId, loginRsp.UserId)
            SpUtil.putString(this, ConstantValue.username, userName.text.toString())
            SpUtil.putString(this, ConstantValue.routerId, routerId)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = routerId
            newRouterEntity.routerName = "Router " + (routerList.size + 1)
            newRouterEntity.username = userName.text.toString()
            newRouterEntity.userId = loginRsp.UserId
            var contains = false
            for (i in routerList) {
                if (i.routerId.equals(routerId)) {
                    contains = true
                    break
                }
            }
            if (contains) {
                KLog.i("数据局中已经包含了这个routerId")
            } else {
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            finish()
        }
    }


    @Inject
    internal lateinit var mPresenter: LoginActivityPresenter

    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null

    private var handler: Handler? = null

    private var builderTips: AlertDialog? = null

    internal var finger: ImageView? = null

    var newRouterEntity = RouterEntity()

    var routerId = ""
    var userId = ""
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_login)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.loginBackListener = null
    }

    override fun initData() {
        swipeBackLayout.isEnabled = false
        AppConfig.instance.messageReceiver!!.loginBackListener = this
        loginBtn.setOnClickListener {
            if (userName.text.toString().equals("")) {
                toast("please type your username")
                return@setOnClickListener
            }
            if (routerId.equals("")) {
                return@setOnClickListener
            }
            var login = LoginReq("Login", routerId, userId!!, 0)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
            showProgressDialog()
        }
        scanIcon.setOnClickListener {
            mPresenter.getScanPermission()
        }
        miniScanIcon.setOnClickListener {
            mPresenter.getScanPermission()
        }

        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_AUTH_SUCCESS -> {
                        setResultInfo(R.string.fingerprint_success)
                        cancellationSignal = null
                    }
                    MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                }
            }
        }
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        if (routerList.size != 0) {
            routerId = routerList[0].routerId
            userId = routerList[0].userId
            username = routerList[0].username
            routerName.text = routerList[0].routerName
            if (!username.equals("")) {
                userName.isEnabled = false
                userName.setText(username)
            }
            hasRouterParent.visibility = View.VISIBLE
            scanParent.visibility = View.INVISIBLE
            noRoutergroup.visibility = View.INVISIBLE
            miniScanParent.visibility = View.VISIBLE
        } else {
            scanParent.visibility = View.VISIBLE
            noRoutergroup.visibility = View.VISIBLE
            miniScanParent.visibility = View.INVISIBLE
            hasRouterParent.visibility = View.INVISIBLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SpUtil.getBoolean(this, ConstantValue.fingerprintUnLock, true)) {
            // init fingerprint.
            try {
                val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                    try {
                        myAuthCallback = MyAuthCallback(handler)
                        val cryptoObjectHelper = CryptoObjectHelper()
                        if (cancellationSignal == null) {
                            cancellationSignal = CancellationSignal()
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                myAuthCallback, null)
                        val builder = AlertDialog.Builder(this)
                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                        builder.setView(view)
                        builder.setCancelable(false)
                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容

                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_cancel.visibility = View.VISIBLE
                        builder.setCancelable(true)
                        btn_cancel.setOnClickListener {
                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid())
                            System.exit(0)
                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this
                        builderTips = builder.create()
                        builderTips?.show()
                    } catch (e: Exception) {
                        try {
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                            /* fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                                cancellationSignal, myAuthCallback, null);*/
                            val builder = AlertDialog.Builder(this)
                            val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                            builder.setView(view)
                            builder.setCancelable(false)
                            val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                            val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//
                            btn_comfirm.setText(R.string.cancel_btn_dialog)
                            tvContent.setText(R.string.choose_finger_dialog_title)
                            val currentContext = this
                            builderTips = builder.create()
                            builderTips?.show()
                        } catch (er: Exception) {
                            er.printStackTrace()
                            builderTips?.dismiss()
                            toast(R.string.Fingerprint_init_failed_Try_again)
                        }

                    }

                } else {
                    SpUtil.putString(this, ConstantValue.fingerPassWord, "")
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(R.string.No_fingerprints_do_you_want_to_set_them_up)
                    dialog.setCancelable(false)

                    dialog.setPositiveButton(android.R.string.ok
                    ) { dialog, which ->
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    }
                    dialog.setNegativeButton(android.R.string.cancel
                    ) { dialog, which ->
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    }
                    dialog.create().show()
                }

            } catch (e: NoClassDefFoundError) {
                SpUtil.putString(this, ConstantValue.fingerPassWord, "")
            }

        } else {
            SpUtil.putString(this, ConstantValue.fingerPassWord, "")
        }
    }

    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, 1)
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
            //case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, FingerprintManager.FINGERPRINT_ERROR_NO_SPACE, FingerprintManager.FINGERPRINT_ERROR_TIMEOUT, FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                setResultInfo(R.string.ErrorHwUnavailable_warning)
            }
        }
    }

    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD, FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY, FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT, FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> setResultInfo(R.string.AcquiredToSlow_warning)
        }
    }

    private fun setResultInfo(stringId: Int) {
        if (stringId == R.string.fingerprint_success) {
            finger?.setImageDrawable(resources.getDrawable(R.mipmap.icon_fingerprint_complete))
            setResult(RESULT_OK, intent)
            SpUtil.putString(this, ConstantValue.fingerPassWord, "888888")
            builderTips?.dismiss()
        } else {
            toast(stringId)
        }
    }

    override fun setupActivityComponent() {
        DaggerLoginActivityComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .loginActivityModule(LoginActivityModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: LoginActivityContract.LoginActivityContractPresenter) {
        mPresenter = presenter as LoginActivityPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

   override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            hasRouterParent.visibility = View.VISIBLE
            miniScanParent.visibility = View.VISIBLE
            scanParent.visibility = View.INVISIBLE
            noRoutergroup.visibility = View.INVISIBLE
            routerName?.setText(data!!.getStringExtra("result"))
            routerId = data!!.getStringExtra("result")
            newRouterEntity.routerId = data!!.getStringExtra("result")
            return
        }
    }
}