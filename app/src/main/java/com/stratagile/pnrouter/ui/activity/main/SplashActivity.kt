package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.statusbar.StatusBarCompat
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.*
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URI
import javax.inject.Inject


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    private var handler: Handler? = null

    //    private var myAuthCallback: MyAuthCallback? = null
//    private var cancellationSignal: CancellationSignal? = null
    private var countDownTimerUtils: CountDownTimerUtils? = null
    override fun loginSuccees() {
//        MobileSocketClient.getInstance().destroy()
        if (SpUtil.getBoolean(this, ConstantValue.firstOpenApp, true)) {
            SpUtil.putBoolean(this, ConstantValue.firstOpenApp, false)
            FileUtil.savaData(ConstantValue.localPath + "/autoLogin.txt", "")
            startActivity(Intent(this, LoginActivityActivity::class.java))
            finish()
        } else {
            if (!"".equals(FileUtil.readData(ConstantValue.localPath + "/autoLogin.txt"))) {
                ConstantValue.logining = true
                startActivity(Intent(this, MainActivity::class.java).putExtra("autoLogin", true))
                finish()
            } else {
                startActivity(Intent(this, LoginActivityActivity::class.java))
                finish()
            }
        }
    }

    override fun jumpToLogin() {
//        MobileSocketClient.getInstance().destroy()
        if (SpUtil.getBoolean(this, ConstantValue.firstOpenApp, true)) {
            SpUtil.putBoolean(this, ConstantValue.firstOpenApp, false)
            FileUtil.savaData(ConstantValue.localPath + "/autoLogin.txt", "")
            startActivity(Intent(this, LoginActivityActivity::class.java))
            finish()
        } else {
            if (!"".equals(FileUtil.readData(ConstantValue.localPath + "/autoLogin.txt"))) {
                ConstantValue.logining = true
                startActivity(Intent(this, MainActivity::class.java).putExtra("autoLogin", true))
                finish()
            } else {
                startActivity(Intent(this, LoginActivityActivity::class.java))
                finish()
            }
        }
    }

    override fun jumpToGuest() {
//        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, GuestActivity::class.java))
        finish()
    }

    override fun exitApp() {
        finish()
        System.exit(0)
    }

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        sentContent = false
        super.onCreate(savedInstanceState)
        StatusBarCompat.translucentStatusBar(this, false)

        KLog.i("SplashActivityAAAA：onCreate")
        var sodium: Sodium = NaCl.sodium()
        AppConfig.instance.stopAllService()
        FireBaseUtils.logEvent(this, FireBaseUtils.eventStartApp)

        /*try {
            var intent = getIntent();
            var action = intent.getAction();//action
            var type = intent.getType();//类型

            //类型
            if (Intent.ACTION_SEND.equals(action) && type != null *//*&& "video/mp4".equals(type)*//*) {
                var uri =  intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
                //如果是媒体类型需要从数据库获取路径
                var filePath=getRealPathFromURI(uri);
                KLog.i("外部分享："+filePath)
                ConstantValue.shareFromLocalPath = filePath;
            }
        }catch (e:Exception)
        {

        }*/

    }

    /**
     * 通过Uri获取文件在本地存储的真实路径
     */
    fun getRealPathFromURI(contentUri: Uri?): String {
        var proj = arrayOf(MediaStore.MediaColumns.DATA)
        var cursor = getContentResolver().query(contentUri, proj!!, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        }
        cursor.close();
        return "";
    }

    override fun onDestroy() {
//        if (myAuthCallback != null) {
//            myAuthCallback?.removeHandle()
//            myAuthCallback = null
//        }
//        if (cancellationSignal != null) {
//            cancellationSignal!!.cancel()
//            cancellationSignal = null
//        }
        super.onDestroy()
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
        rootLayout!!.visibility = View.GONE
//        StatusBarCompat.changeToLightStatusBar(this)
//        StatusBarUtil.setColor(this, resources.getColor(R.color.mainColor), 0)
    }

    override fun initData() {
        KLog.i("SplashActivityAAAA：initData")
        if (BuildConfig.DEBUG) {
            SpUtil.putString(this, ConstantValue.fingerprintSetting, "0")
            SpUtil.putString(this, ConstantValue.screenshotsSetting, "0")
        }
        AppConfig.instance.isOpenSplashActivity = true
        ConstantValue.isGooglePlayServicesAvailable = SystemUtil.isGooglePlayServicesAvailable(this)
        ConstantValue.msgIndex = (System.currentTimeMillis() / 1000).toInt() + (Math.random() * 100).toInt();
        KLog.i("SplashActivityAAAA：initData0011")
        mPresenter.getPermission()
        KLog.i("SplashActivityAAAA：initData001122")


        KLog.i("SplashActivityAAAA：initData00112233")
        mPresenter.observeJump()
    }

    override fun setupActivityComponent() {
        DaggerSplashComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: SplashContract.SplashContractPresenter) {
        mPresenter = presenter as SplashPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}