package com.stratagile.pnrouter.ui.activity.scan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.SurfaceHolder
import android.view.View
import android.widget.*
import com.google.zxing.Result
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.scan.component.DaggerScanQrCodeComponent
import com.stratagile.pnrouter.ui.activity.scan.contract.ScanQrCodeContract
import com.stratagile.pnrouter.ui.activity.scan.module.ScanQrCodeModule
import com.stratagile.pnrouter.ui.activity.scan.presenter.ScanQrCodePresenter
import com.stratagile.pnrouter.ui.activity.scan.qrcode.CaptureActivityHandler
import com.vondear.rxtools.RxAnimationTool
import com.vondear.rxtools.RxBeepTool
import com.vondear.rxtools.RxPhotoTool
import com.vondear.rxtools.RxQrBarTool
import com.vondear.rxtools.interfaces.OnRxScanerListener
import com.vondear.rxtools.module.scaner.CameraManager
import com.vondear.rxtools.module.scaner.decoding.InactivityTimer
import com.vondear.rxtools.view.dialog.RxDialogSure
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: $description
 * @date 2018/09/11 15:29:14
 */

class ScanQrCodeActivity : BaseActivity(), ScanQrCodeContract.View {

    @Inject
    internal lateinit var mPresenter: ScanQrCodePresenter
    private var mScanerListener: OnRxScanerListener? = null//扫描结果监听
    private var inactivityTimer: InactivityTimer? = null
    private var handler: CaptureActivityHandler? = null//扫描处理
    private var mContainer: RelativeLayout? = null//整体根布局
    private var mCropLayout: RelativeLayout? = null//扫描框根布局
    private var mCropWidth = 0//扫描边界的宽度
    private var mCropHeight = 0//扫描边界的高度
    private var hasSurface: Boolean = false//是否有预览
    private val vibrate = true//扫描成功后是否震动
    private var mFlashing = true//闪光灯开启状态
    private var mLlScanHelp: LinearLayout? = null//生成二维码 & 条形码 布局
    private var mIvLight: ImageView? = null//闪光灯 按钮
    private val rxDialogSure: RxDialogSure? = null//扫描结果显示框

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_scan_qr_code)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
        DaggerScanQrCodeComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .scanQrCodeModule(ScanQrCodeModule(this))
                .build()
                .inject(this)
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

    private fun initScanerAnimation() {
        val mQrLineView = findViewById(R.id.capture_scan_line) as ImageView
        RxAnimationTool.ScaleUpDowm(mQrLineView)
    }

    fun getCropWidth(): Int {
        return mCropWidth
    }

    fun setCropWidth(cropWidth: Int) {
        mCropWidth = cropWidth
        CameraManager.FRAME_WIDTH = mCropWidth

    }

    fun getCropHeight(): Int {
        return mCropHeight
    }

    fun setCropHeight(cropHeight: Int) {
        this.mCropHeight = cropHeight
        CameraManager.FRAME_HEIGHT = mCropHeight
    }

    fun btn(view: View) {
        val viewId = view.id
        if (viewId == com.vondear.rxtools.R.id.top_mask) {
            light()
        } else if (viewId == com.vondear.rxtools.R.id.top_back) {
            finish()
        } else if (viewId == com.vondear.rxtools.R.id.top_openpicture) {
            RxPhotoTool.openLocalImage(this)
        }
    }

    private fun light() {
        if (mFlashing) {
            mFlashing = false
            // 开闪光灯
            CameraManager.get().openLight()
        } else {
            mFlashing = true
            // 关闪光灯
            CameraManager.get().offLight()
        }

    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            val point = CameraManager.get().getCameraResolution()
            val width = AtomicInteger(point.y)
            val height = AtomicInteger(point.x)
            val cropWidth = mCropLayout?.getWidth()
            val cropHeight = mCropLayout?.getHeight()
            KLog.i(cropWidth)
            KLog.i(cropHeight)
            setCropWidth(cropWidth!!)
            setCropHeight(cropHeight!!)
        } catch (ioe: IOException) {
            return
        } catch (ioe: RuntimeException) {
            return
        }

        if (handler == null) {
            handler = CaptureActivityHandler(this)
        }
    }
    //========================================打开本地图片识别二维码 end=================================

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val resolver = contentResolver
            // 照片的原始资源地址
            val originalUri = data.data
            try {
                // 使用ContentProvider通过URI获取原始图片
                val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)

                // 开始对图像资源解码
                val rawResult = RxQrBarTool.decodeFromPhoto(photo)
                if (rawResult != null) {
                    if (mScanerListener == null) {
                        initDialogResult(rawResult)
                    } else {
                        mScanerListener?.onSuccess("From to Picture", rawResult)
                    }
                } else {
                    mScanerListener?.onFail("From to Picture", "图片识别失败")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    //==============================================================================================解析结果 及 后续处理 end

    private fun initDialogResult(result: Result) {
        val realContent = result.getText()
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
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            handler?.sendEmptyMessage(com.vondear.rxtools.R.id.restart_preview)
        }
        btn_comfirm.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("result", realContent)
            setResult(RESULT_OK, intent)
            onBackPressed()
        }
        dialog.show()
    }

    fun handleDecode(result: Result) {
        inactivityTimer?.onActivity()
        RxBeepTool.playBeep(this, vibrate)//扫描成功之后的振动与声音提示

        val result1 = result.getText()
        //KLog.i("二维码/条形码 扫描结果", result1)
        if (mScanerListener == null) {
            //            RxToast.success(result1);
            initDialogResult(result)
        } else {
            mScanerListener?.onSuccess("From to Camera", result)
        }
    }

    fun getHandler(): Handler? {
        return handler
    }
}