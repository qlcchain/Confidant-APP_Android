package com.stratagile.pnrouter.ui.activity.scan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.scan.component.DaggerScanQrCodeComponent
import com.stratagile.pnrouter.ui.activity.scan.contract.ScanQrCodeContract
import com.stratagile.pnrouter.ui.activity.scan.module.ScanQrCodeModule
import com.stratagile.pnrouter.ui.activity.scan.presenter.ScanQrCodePresenter
import com.stratagile.pnrouter.utils.RxPhotoTool
import com.stratagile.pnrouter.utils.RxPhotoTool.CROP_IMAGE
import com.stratagile.pnrouter.utils.RxPhotoTool.GET_IMAGE_FROM_PHONE
import com.stratagile.pnrouter.utils.SystemUtil
import kotlinx.android.synthetic.main.activity_scan_qr_code.*
import java.io.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: $description
 * @date 2018/09/11 15:29:14
 */

class ScanQrCodeActivity : BaseActivity(), ScanQrCodeContract.View, QRCodeView.Delegate {
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    @Inject
    internal lateinit var mPresenter: ScanQrCodePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_scan_qr_code)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        showView()
        mZXingView.setDelegate(this)
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
//        mZXingView.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
    }

    internal var galleryPackName: String = ""

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

    override fun onScanQRCodeSuccess(result: String?) {
        KLog.i("result:$result")
        if (result == null) {
            startPhotoZoom(tempUri)
        } else {
            if (isExpectResult(result)) {
                mZXingView.stopSpot()
                vibrate()
                val intent = Intent()
                if (result == null) {
                    intent.putExtra("result", "")
                } else {
                    if (isMacAddress(result)) {
                        var macAddress  = ""
                        for (i in 0..5) {
                            macAddress = macAddress + result.substring(i * 2, (i + 1) * 2) + ":"
                        }
                        macAddress = macAddress.subSequence(0, macAddress.length - 1).toString()
                        KLog.i("mac地址为：" + macAddress)
                        intent.putExtra("result", macAddress)
                    } else {
                        intent.putExtra("result", result)
                    }
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                mZXingView.startSpot()
            }
        }
    }

    fun isExpectResult(string: String) : Boolean {
        if (string.contains("type_")) {
            return true
        }
        if (isMacAddress(string)) {
            return true
        }
        return false
    }

    private fun isMacAddress(mac: String): Boolean {
        val patternMac = "^[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}+[a-fA-F0-9]{2}$"
        val pa = Pattern.compile(patternMac)
        val isMac = pa.matcher(mac).find()
        return isMac
    }

    override fun onScanQRCodeOpenCameraError() {
        KLog.i("打开相机出错")
    }

    override fun initData() {
        setTitle("Scan")
        galleryPackName = SystemUtil.getSystemPackagesName(this, "gallery")
        if ("" == galleryPackName) {
            galleryPackName = SystemUtil.getSystemPackagesName(this, "gallery3d")
        }
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

    var localImgMode = 0
    var tempUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mZXingView.startSpotAndShowRect() // 显示扫描框，并且延迟0.5秒后开始识别
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GET_IMAGE_FROM_PHONE) {
            KLog.i(data!!.data)
            tempUri = data!!.data
            val originalUri = data!!.data
            val resolver = contentResolver
            val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)
            mZXingView.decodeQRCode(getFileFromUri(originalUri, this)?.absolutePath)
            if (localImgMode == 0) {

            } else {

//                var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
//                KLog.i(list)
//                mZXingView.decodeQRCode(list!!.get(0).path)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == CROP_IMAGE) {
            val resolver = contentResolver
            // 照片的原始资源地址
            val originalUri = data!!.data
            try {
                val degree = PictureFileUtils.readPictureDegree(outputFile.path)
                KLog.i("图片的角度为：" + degree)
                // 使用ContentProvider通过URI获取原始图片
//                val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)
                val photo = decodeUriAsBitmap(outputFile)
//                mZXingView.decodeQRCode(photo)
                mZXingView.decodeQRCode("" + Environment.getExternalStorageDirectory() + "/temp.jpg")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun getFileFromContentUri(contentUri: Uri?, context: Context): File? {
        if (contentUri == null) {
            return null
        }
        var file: File? = null
        var filePath: String
        val fileName: String
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(contentUri, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
            fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]))
            cursor.close()
            if (!TextUtils.isEmpty(filePath)) {
                file = File(filePath)
            }
//            if (!file!!.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
//                filePath = getPathFromInputStreamUri(context, contentUri, fileName)
//            }
//            if (!TextUtils.isEmpty(filePath)) {
//                file = File(filePath)
//            }
        }
        return file
    }


    fun getFileFromUri(uri: Uri?, context: Context): File? {
        if (uri == null) {
            return null
        }
        when (uri.scheme) {
            "content" -> return getFileFromContentUri(uri, context)
            "file" -> return File(uri.path)
            else -> return null
        }
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    private fun getRealPathFromUri_AboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val wholeID = DocumentsContract.getDocumentId(uri)

        // 使用':'分割
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media._ID + "=?"
        val selectionArgs = arrayOf(id)

        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //
                projection, selection, selectionArgs, null)
        val columnIndex = cursor!!.getColumnIndex(projection[0])
        if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    //以bitmap返回格式解析uri
    private fun decodeUriAsBitmap(uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }

        return bitmap
    }

    private val IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg"
    internal var outputFile = Uri.parse(IMAGE_FILE_LOCATION)//The Uri to store the big bitmap

    fun startPhotoZoom(uri: Uri?) {
        KLog.i("图片的uri为：" + uri)
        try {
            var intent = Intent("com.android.camera.action.CROP")
            if (galleryPackName != "") {
                intent.setPackage(galleryPackName)
            }
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", true)
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
            intent.putExtra("return-data", false)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile)  //imageurl 文件输出的位置
            intent.putExtra("noFaceDetection", true)
            try {
                startActivityForResult(intent, CROP_IMAGE)
            } catch (e: ActivityNotFoundException) {
                intent = Intent("com.android.camera.action.CROP")
                intent.setDataAndType(uri, "image/*")
                intent.putExtra("crop", true)
                intent.putExtra("aspectX", 1)
                intent.putExtra("aspectY", 1)
                intent.putExtra("return-data", false)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile)  //imageurl 文件输出的位置
                intent.putExtra("noFaceDetection", true)
                startActivityForResult(intent, CROP_IMAGE)
            } finally {

            }
        } catch (e: Exception) {
            try {
                val stringWriter = StringWriter()
                e.printStackTrace(PrintWriter(stringWriter))
            } catch (el: Exception) {

            }

//            ToastUtil.displayShortToast(getString(R.string.loadPicError))
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
            var intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (galleryPackName != "") {
                intent.setPackage(galleryPackName)
            }
            //这里要传一个整形的常量RESULT_LOAD_IMAGE到startActivityForResult()方法。
            try {
                startActivityForResult(intent, GET_IMAGE_FROM_PHONE)
            } catch (e: ActivityNotFoundException) {
                intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GET_IMAGE_FROM_PHONE)
            } finally {

            }
//            if (localImgMode == 0) {
//
//            } else {
//                PictureSelector.create(this)
//                        .openGallery(PictureMimeType.ofImage())
//                        .maxSelectNum(100)
//                        .minSelectNum(1)
//                        .imageSpanCount(3)
//                        .selectionMode(PictureConfig.SINGLE)
//                        .previewImage(false)
//                        .previewVideo(false)
//                        .enablePreviewAudio(false)
//                        .isCamera(true)
//                        .imageFormat(PictureMimeType.PNG)
//                        .isZoomAnim(true)
//                        .sizeMultiplier(0.5f)
//                        .setOutputCameraPath("/CustomPath")
//                        .enableCrop(false)
//                        .compress(false)
//                        .glideOverride(160, 160)
//                        .hideBottomControls(false)
//                        .isGif(false)
//                        .openClickSound(false)
//                        .minimumCompressSize(100)
//                        .synOrAsy(true)
//                        .rotateEnabled(true)
//                        .scaleEnabled(true)
//                        .videoMaxSecond(60 * 60 * 3)
//                        .videoMinSecond(1)
//                        .isDragFrame(false)
//                        .forResult(GET_IMAGE_FROM_PHONE)
//            }
//            RxPhotoTool.openLocalImage(this)
        } else if (viewId == R.id.rl_title) {
            KLog.i("切换识别模式。。。" + localImgMode)
            if (localImgMode == 0) {
                localImgMode = 1
            } else {
                localImgMode = 0
            }
        }
    }

}