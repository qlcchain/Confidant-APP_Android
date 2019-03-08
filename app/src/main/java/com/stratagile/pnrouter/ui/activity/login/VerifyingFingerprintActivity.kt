package com.stratagile.pnrouter.ui.activity.login

import android.animation.ObjectAnimator
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
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.component.DaggerVerifyingFingerprintComponent
import com.stratagile.pnrouter.ui.activity.login.contract.VerifyingFingerprintContract
import com.stratagile.pnrouter.ui.activity.login.module.VerifyingFingerprintModule
import com.stratagile.pnrouter.ui.activity.login.presenter.VerifyingFingerprintPresenter
import com.stratagile.pnrouter.utils.SpUtil
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_fingerprint.*
import javax.inject.Inject
import android.support.v4.view.ViewCompat.getTranslationY
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.SweetAlertDialog


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2019/02/26 14:40:52
 */

class VerifyingFingerprintActivity : BaseActivity(), VerifyingFingerprintContract.View {

    @Inject
    internal lateinit var mPresenter: VerifyingFingerprintPresenter
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
        setContentView(R.layout.activity_fingerprint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
        var count  = 0
        iv0.setOnClickListener {
            count++
        }
        funTv0.setOnClickListener {
            if (count == 2) {
               startActivity(Intent(this, LogActivity::class.java))
                count = 0
            } else {
                count = 0
            }
        }
    }
    override fun initData() {
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_AUTH_SUCCESS -> {
                        setResultInfo(R.string.fingerprint_success)
                        cancellationSignal = null
                        finish()
                        overridePendingTransition(0, R.anim.activity_translate_out_1)
                    }
                    MyAuthCallback.MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MyAuthCallback.MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MyAuthCallback.MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                }
            }
        }
        llNext.setOnClickListener {
            showDialog()
        }
        showDialog()
    }

    fun showFingerAnimation() {
        LogUtil.addLog("点击重新验证指纹..")
        val curTranslationY = llLogo.getTranslationY()
        val animator = ObjectAnimator.ofFloat(llLogo, "translationY", curTranslationY, curTranslationY - resources.getDimension(R.dimen.x300))
        animator.setDuration(600)
        animator.start()

        val curNexY = llNext.translationY
        var nextAnimator = ObjectAnimator.ofFloat(llNext, "translationY", curNexY, curNexY + resources.getDimension(R.dimen.x300))
        nextAnimator.setDuration(600)
        nextAnimator.start()
    }
    fun hideFingerAnimation() {
        val curTranslationY = llLogo.getTranslationY()
        val animator = ObjectAnimator.ofFloat(llLogo, "translationY", curTranslationY, curTranslationY + resources.getDimension(R.dimen.x300))
        animator.setDuration(600)
        animator.start()

        val curNexY = llNext.translationY
        var nextAnimator = ObjectAnimator.ofFloat(llNext, "translationY", curNexY, curNexY - resources.getDimension(R.dimen.x300))
        nextAnimator.setDuration(600)
        nextAnimator.start()
    }
    private fun showDialog()
    {
        showFingerAnimation()
        if (!ConstantValue.loginOut && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // init fingerprint.
            try {
                val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                    try {
                        LogUtil.addLog("开始调用系统的指纹..")
                        myAuthCallback = MyAuthCallback(handler)
                        val cryptoObjectHelper = CryptoObjectHelper()
                        if (cancellationSignal == null) {
                            cancellationSignal = CancellationSignal()
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                myAuthCallback, null)

                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
//                        val builder = AlertDialog.Builder(this)
//                        builder.setView(view)
//                        builder.setCancelable(false)

                        var formatDialog = CommonDialog(this)
                        formatDialog?.setCancelable(false)

                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容

                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            hideFingerAnimation()
                            formatDialog.dismissWithAnimation()
//                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                            /*CrashReport.closeBugly()
                            CrashReport.closeCrashReport()
                            //MiPushClient.unregisterPush(this)
                            AppConfig.instance.stopAllService()
                            AppConfig.instance?.mAppActivityManager.finishAllActivity()
                            //android进程完美退出方法。
//            AppConfig.instance.mAppActivityManager.AppExit()
                            var intent = Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
                            this.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
                            //System.runFinalizersOnExit(true);、
                            System.exit(0)*/
                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this
                        val window = formatDialog?.window
                        window?.setBackgroundDrawableResource(android.R.color.transparent)
                        formatDialog?.setView(view)
                        formatDialog?.show()
//                        builderTips = builder.create()
//                        builderTips?.show()
                    } catch (e: Exception) {
                        LogUtil.addLog("调用系统指纹错误..")
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
                        CrashReport.closeBugly()
                        CrashReport.closeCrashReport()
                        //MiPushClient.unregisterPush(this)
                        AppConfig.instance.stopAllService()
                        AppConfig.instance?.mAppActivityManager.finishAllActivity()
                        //android进程完美退出方法。
//            AppConfig.instance.mAppActivityManager.AppExit()
                        var intent = Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
                        this.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
                        //System.runFinalizersOnExit(true);、
                        System.exit(0)
                    }
                    dialog.setNegativeButton(android.R.string.cancel
                    ) { dialog, which ->
                        dialog.dismiss()
                       /* finish();
                        //android进程完美退出方法。
                        var intent = Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
                        this.startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //System.runFinalizersOnExit(true);
                        System.exit(0);*/
                    }
                    dialog.create().show()
                }

            } catch (e: Exception) {
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
                setResultInfo(R.string.ErrorHwUnavailable_warning)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {

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
       DaggerVerifyingFingerprintComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .verifyingFingerprintModule(VerifyingFingerprintModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: VerifyingFingerprintContract.VerifyingFingerprintContractPresenter) {
            mPresenter = presenter as VerifyingFingerprintPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}