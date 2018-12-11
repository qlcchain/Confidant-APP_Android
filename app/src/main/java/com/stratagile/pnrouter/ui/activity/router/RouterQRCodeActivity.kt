package com.stratagile.pnrouter.ui.activity.router

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.pawegio.kandroid.i
import com.pawegio.kandroid.longToast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.RouterCodeData
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterQRCodeComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterQRCodeModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterQRCodePresenter
import com.stratagile.pnrouter.utils.AESCipher
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.utils.ThreadUtil
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.activity_router_qrcode.*
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/09/26 11:53:16
 */

class RouterQRCodeActivity : BaseActivity(), RouterQRCodeContract.View {

    @Inject
    internal lateinit var mPresenter: RouterQRCodePresenter
    lateinit var routerEntity: RouterEntity
    //lateinit var createEnglishQRCode : ThreadUtil.Companion.CreateEnglishQRCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_qrcode)
    }
    override fun initData() {
        title.text = resources.getString(R.string.qr_code_business_card)
        routerEntity = intent.getParcelableExtra("router")
        tvRouterName.text = routerEntity.routerName
        tvShare2.setOnClickListener { PopWindowUtil.showSharePopWindow(this, tvShare2) }
       /* createEnglishQRCode = ThreadUtil.Companion.CreateEnglishQRCode(routerEntity.routerId, ivQrCode2)
        createEnglishQRCode.execute()*/
        var routerCodeData: RouterCodeData = RouterCodeData();
        routerCodeData.id = "010001".toByteArray()
        routerCodeData.routerId = routerEntity.routerId.toByteArray()
        routerCodeData.userSn = routerEntity.userSn.toByteArray()
        var routerCodeDataByte = routerCodeData.toByteArray();
        var base64Str = AESCipher.aesEncryptBytesToBase64(routerCodeDataByte,"welcometoqlc0101".toByteArray())
        Thread(Runnable() {
            run() {

                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode(base64Str, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor))
                runOnUiThread {
                    ivQrCode2.setImageBitmap(bitmap)
                }

            }
        }).start()
        tvSaveToPhone2.setOnClickListener {
            saveQrCodeToPhone()
        }
    }

    override fun setupActivityComponent() {
       DaggerRouterQRCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerQRCodeModule(RouterQRCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterQRCodeContract.RouterQRCodeContractPresenter) {
            mPresenter = presenter as RouterQRCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    fun saveQrCodeToPhone() {
        showProgressDialog()
        thread {
            val dView = cardView2
            dView.isDrawingCacheEnabled = true
            dView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(dView.drawingCache)
            if (bitmap != null) {
                try {
                    // 获取内置SD卡路径
                    val sdCardPath = Environment.getExternalStorageDirectory().getPath() + "/Router"
                    // 图片文件路径
                    val filePath = sdCardPath + File.separator + routerEntity.routerName + ".png"
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            PopWindowUtil.showSharePopWindow(this, tvShare2)
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