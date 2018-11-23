package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.user.component.DaggerQRCodeComponent
import com.stratagile.pnrouter.ui.activity.user.contract.QRCodeContract
import com.stratagile.pnrouter.ui.activity.user.module.QRCodeModule
import com.stratagile.pnrouter.ui.activity.user.presenter.QRCodePresenter
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_qrcode.*

import javax.inject.Inject;
import android.graphics.Bitmap
import android.os.Environment
import com.pawegio.kandroid.longToast
import com.socks.library.KLog
import com.stratagile.pnrouter.utils.ThreadUtil
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/11 14:01:23
 */

class QRCodeActivity : BaseActivity(), QRCodeContract.View, View.OnClickListener{
    override fun onClick(v: View?) {
        when(v!!.id) {

        }
    }

    @Inject
    internal lateinit var mPresenter: QRCodePresenter

    lateinit var createEnglishQRCode : ThreadUtil.Companion.CreateEnglishQRCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_qrcode)
    }
    override fun initData() {
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        if ("".equals(nickName)) {
            title.text = getString(R.string.details)
        } else {
            title.text = nickName
        }
        tvShare.setOnClickListener { PopWindowUtil.showSharePopWindow(this, tvShare) }
        tvUserName.text = SpUtil.getString(this, ConstantValue.username, "")
        var userId = FileUtil.getLocalUserData("userid")
        ivAvatar.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        ivAvatar.setImageFile(SpUtil.getString(this, ConstantValue.selfImageName, "")!!)
        createEnglishQRCode = ThreadUtil.Companion.CreateEnglishQRCode(userId, ivQrCode)
        createEnglishQRCode.execute()
        tvSaveToPhone.setOnClickListener {
            saveQrCodeToPhone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        createEnglishQRCode.cancel(true)
    }

    fun saveQrCodeToPhone() {
        showProgressDialog()
        thread {
            val dView = cardView
            dView.isDrawingCacheEnabled = true
            dView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(dView.drawingCache)
            if (bitmap != null) {
                try {
                    // 获取内置SD卡路径
                    val sdCardPath = Environment.getExternalStorageDirectory().getPath() + "/Router"
                    // 图片文件路径
                    var username = SpUtil.getString(this, ConstantValue.username, "")
                    val filePath = sdCardPath + File.separator + username + ".png"
                    val file = File(filePath)
                    val os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    runOnUiThread {
                        closeProgressDialog()
                        longToast(getString(R.string.save_to_phone_success) + "\n" + filePath)
                    }
                    KLog.i("存储完成")
                } catch (e: Exception) {
                }

            }
        }
    }

    override fun setupActivityComponent() {
       DaggerQRCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .qRCodeModule(QRCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: QRCodeContract.QRCodeContractPresenter) {
            mPresenter = presenter as QRCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            PopWindowUtil.showSharePopWindow(this, tvShare)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share_self, menu)
        return super.onCreateOptionsMenu(menu)
    }

}