package com.stratagile.pnrouter.ui.activity.login

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.*
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.fingerprint.MyAuthCallback.*
import com.stratagile.pnrouter.ui.activity.login.component.DaggerLoginActivityComponent
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.finger_dialog_layout.*
import java.util.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View {

    @Inject
    internal lateinit var mPresenter: LoginActivityPresenter

    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null

    private var handler: Handler? = null

    private var builderTips: AlertDialog? = null

    internal var finger: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_login)
    }
    override fun initData() {
        LoginBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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

                        val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_comfirm.visibility = View.VISIBLE
                        builder.setCancelable(true)
                        btn_comfirm.setOnClickListener {
                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
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
                }
                /*}else{
                    etPassword.requestFocus();
                }*/
            } catch (e: NoClassDefFoundError) {
                SpUtil.putString(this, ConstantValue.fingerPassWord, "")
            }

        } else {
            SpUtil.putString(this, ConstantValue.fingerPassWord, "")
        }
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
        //case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, FingerprintManager.FINGERPRINT_ERROR_NO_SPACE, FingerprintManager.FINGERPRINT_ERROR_TIMEOUT, FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                builderTips?.dismiss()
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

}