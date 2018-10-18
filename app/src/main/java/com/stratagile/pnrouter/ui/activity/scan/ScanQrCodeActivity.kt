package com.stratagile.pnrouter.ui.activity.scan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.scan.component.DaggerScanQrCodeComponent
import com.stratagile.pnrouter.ui.activity.scan.contract.ScanQrCodeContract
import com.stratagile.pnrouter.ui.activity.scan.module.ScanQrCodeModule
import com.stratagile.pnrouter.ui.activity.scan.presenter.ScanQrCodePresenter
import com.stratagile.pnrouter.utils.RxPhotoTool
import kotlinx.android.synthetic.main.activity_scan_qr_code.*
import java.io.IOException
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: $description
 * @date 2018/09/11 15:29:14
 */

class ScanQrCodeActivity : BaseActivity(), ScanQrCodeContract.View, QRCodeView.Delegate {

    @Inject
    internal lateinit var mPresenter: ScanQrCodePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_scan_qr_code)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mZXingView.setDelegate(this)
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
//        mZXingView.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
    }

    override fun onStart() {
        super.onStart()
//        mZXingView.startSpotDelay(150)
//        mZXingView.showScanRect()
        mZXingView.startSpotAndShowRect() // 显示扫描框，并且延迟0.5秒后开始识别
    }

    override fun onStop() {
        mZXingView.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    private fun vibrate() {
        var vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(50, 50)
        vibrator.vibrate(pattern, -1)
    }

    override fun onScanQRCodeSuccess(result: String) {
        KLog.i( "result:$result")
        initDialogResult(result)
        mZXingView.stopSpot()
        vibrate()
    }

    override fun onScanQRCodeOpenCameraError() {
        KLog.i( "打开相机出错")
    }
    override fun initData() {
        setTitle("Scan")
    }

    override fun setupActivityComponent() {
        DaggerScanQrCodeComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .scanQrCodeModule(ScanQrCodeModule(this))
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        mZXingView.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }
    override fun setPresenter(presenter: ScanQrCodeContract.ScanQrCodeContractPresenter) {
        mPresenter = presenter as ScanQrCodePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }


    //========================================打开本地图片识别二维码 end=================================

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mZXingView.startSpotAndShowRect() // 显示扫描框，并且延迟0.5秒后开始识别
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val resolver = contentResolver
            // 照片的原始资源地址
            val originalUri = data!!.data
            try {
                // 使用ContentProvider通过URI获取原始图片
                val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)
                mZXingView.decodeQRCode(photo)
                // 开始对图像资源解码
//                val rawResult = RxQrBarTool.decodeFromPhoto(photo)
//                if (rawResult != null) {
//                    if (mScanerListener == null) {
//                        initDialogResult(rawResult)
//                    } else {
//                        mScanerListener?.onSuccess("From to Picture", rawResult)
//                    }
//                } else {
//                    mScanerListener?.onFail("From to Picture", "图片识别失败")
//                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    //==============================================================================================解析结果 及 后续处理 end

    private fun initDialogResult(result: String) {
        val realContent = result
        val builder = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.dialog_layout, null)
        builder.setView(view)
        builder.setCancelable(true)
        val title = view.findViewById<View>(R.id.title) as TextView//设置标题
        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
        val btn_cancel = view.findViewById<View>(R.id.btn_left) as Button//取消按钮
        val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//确定按钮
        title.setText(R.string.result)
        tvContent.setText(realContent)
        //取消或确定按钮监听事件处l
        val dialog = builder.create()
        btn_cancel.text = getString(R.string.cancel).toLowerCase()
        btn_cancel.setOnClickListener {
            dialog.dismiss()
            mZXingView.startSpot() // 延迟0.5秒后开始识别
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//            handler?.sendEmptyMessage(com.vondear.rxtools.R.id.restart_preview)
        }
        btn_comfirm.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("result", realContent)
            setResult(RESULT_OK, intent)
            finish()
        }
        dialog.show()
    }

    var mFlashing = false
    private fun light() {
        if (mFlashing) {
            mFlashing = false
            // 开闪光灯
            mZXingView.closeFlashlight() // 打开闪光灯
        } else {
            mFlashing = true
            // 关闪光灯
            mZXingView.openFlashlight() // 打开闪光灯
        }

    }

    fun btn(view: View) {
        val viewId = view.id
        if (viewId == R.id.top_mask) {
            light()
        } else if (viewId == R.id.top_openpicture) {
            RxPhotoTool.openLocalImage(this)
        }
    }

}