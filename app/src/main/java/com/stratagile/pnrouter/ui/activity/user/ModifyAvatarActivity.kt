package com.stratagile.pnrouter.ui.activity.user

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.AllFileStatus
import com.stratagile.pnrouter.entity.events.ResetAvatar
import com.stratagile.pnrouter.ui.activity.user.component.DaggerModifyAvatarComponent
import com.stratagile.pnrouter.ui.activity.user.contract.ModifyAvatarContract
import com.stratagile.pnrouter.ui.activity.user.module.ModifyAvatarModule
import com.stratagile.pnrouter.ui.activity.user.presenter.ModifyAvatarPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxStatusEvent
import events.UpdataAvatrrEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_modify_avatar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/12 18:33:54
 */

class ModifyAvatarActivity : BaseActivity(), ModifyAvatarContract.View, PNRouterServiceMessageReceiver.UploadAvatarBack {
    override fun uploadAvatarReq(jUploadAvatarRsp: JUploadAvatarRsp) {
        runOnUiThread()
        {
            closeProgressDialog()
        }
        when (jUploadAvatarRsp.params.retCode)
        {
            0 ->
            {

                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
                var filePath  = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + "__Avatar.jpg"
                var files_dir = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
                FileUtil.copySdcardFile(filePath, files_dir)
                runOnUiThread {
                    toast(getString(R.string.Avatar_Update_Successful))
                    EventBus.getDefault().post(ResetAvatar())
                    finish()
                }
            }
            1 ->
            {
                runOnUiThread {
                    toast(getString(R.string.User_ID_error))
                }
            }
            2 ->
            {
                runOnUiThread {
                    toast(getString(R.string.file_error))
                }
            }
            3 ->
            {
                runOnUiThread {
                    toast(getString(R.string.file_hasnot_changed))
                }
            }
            else ->
            {
                runOnUiThread {
                    toast(getString(R.string.Other_mistakes))
                }
            }
        }

    }

    @Inject
    internal lateinit var mPresenter: ModifyAvatarPresenter

    private var bitmap: Bitmap? = null
    private val IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg"
    internal var inputUri = Uri.parse(IMAGE_FILE_LOCATION)//The Uri to store the big bitmap
    internal var outputFile = Uri.parse(IMAGE_FILE_LOCATION)//The Uri to store the big bitmap
    internal var galleryPackName: String = ""
    var options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.HIGH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_modify_avatar)
    }
    override fun initData() {
        EventBus.getDefault().register(this)
        title.text = "Profile Picture"
        AppConfig.instance.messageReceiver?.uploadAvatarBack = this
        galleryPackName = SystemUtil.getSystemPackagesName(this, "gallery")
        if ("" == galleryPackName) {
            galleryPackName = SystemUtil.getSystemPackagesName(this, "gallery3d")
        }
        if (!SpUtil.getString(this, ConstantValue.selfImageName, "").equals("")) {

        }
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))+".jpg"
        val lastFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/Avatar/" + fileBase58Name, "")
        if (lastFile.exists()) {
            Glide.with(this)
                    .load(lastFile)
                    .apply(options)
                    .into(ivPicture)
        }
        val strCamera = ""
        val packages = this.packageManager
                .getInstalledPackages(0)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
            val tempFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/Avatar/"+fileBase58Name+"__Avatar.jpg")
            inputUri = RxFileTool.getUriForFile(this, tempFile)
            outputFile = Uri.fromFile(tempFile)
        }
        bt_skip.setOnClickListener {
            finish()
        }
        ivPicture.setOnClickListener {
            var intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (galleryPackName != "") {
                intent.setPackage(galleryPackName)
            }
            //这里要传一个整形的常量RESULT_LOAD_IMAGE到startActivityForResult()方法。
            try {
                startActivityForResult(intent, 0)
            } catch (e: ActivityNotFoundException) {
                intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 0)
            } finally {

            }
        }
        bt_save.setOnClickListener {
            if (bitmap != null) {
                saveBitmap(bitmap!!)
            }
        }
    }

    override fun setupActivityComponent() {
        DaggerModifyAvatarComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .modifyAvatarModule(ModifyAvatarModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: ModifyAvatarContract.ModifyAvatarContractPresenter) {
        mPresenter = presenter as ModifyAvatarPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //我们需要判断requestCode是否是我们之前传给startActivityForResult()方法的RESULT_LOAD_IMAGE，并且返回的数据不能为空
        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
            startPhotoZoom(data.data)
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            bitmap = decodeUriAsBitmap(inputUri)
            //            bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(imageFile.getPath()));
            Glide.with(this)
                    .load(bitmap)
                    .apply(options)
                    .into(ivPicture)
        }
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

    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     * @return
     */
    fun saveBitmap(mBitmap: Bitmap) {
        showProgressDialog()
        Thread(Runnable {
            try {
                val dataFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath, "")
                if (!dataFile.exists()) {
                    dataFile.mkdir()
                }
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
                var filePath  =dataFile.path + "/Avatar/" + fileBase58Name + "__Avatar.jpg"
                val filePic: File = File(filePath)
                //SpUtil.putString(this, ConstantValue.selfImageName, filePic.name)
                if (!filePic.exists()) {
                    filePic.parentFile.mkdirs()
                    filePic.createNewFile()
                }
               /*var result = FileUtil.saveAvatarBitmap(mBitmap,filePath,100)
                MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "", "");*/
                //sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
                  // 最大图片大小 100KB
                val maxSize = 50
                val fos = FileOutputStream(filePic)
                val baos = ByteArrayOutputStream()
                var options = 50
                // 获取尺寸压缩倍数
                val ratio = getRatioSize(mBitmap.width, mBitmap.height)
                KLog.i("获取尺寸压缩倍数$ratio")
                // 压缩Bitmap到对应尺寸
                val result = mBitmap
                /*val canvas = Canvas(result)
                val rect = Rect(0, 0, mBitmap.width / ratio, mBitmap.height / ratio)
                canvas.drawBitmap(mBitmap, null, rect, null)*/
                result.compress(Bitmap.CompressFormat.JPEG, options, baos)
                // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                //            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
                // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                while (baos.toByteArray().size / 1024 > maxSize) {
                    if (options < 20) {
                        break
                    }
                    // 重置baos即清空baos
                    baos.reset()
                    // 每次都减少10
                    options -= 10
                    // 这里压缩options%，把压缩后的数据存放到baos中
                    result.compress(Bitmap.CompressFormat.JPEG, options, baos)
                }
                baos.writeTo(fos)
                fos.flush()
                fos.close()
                KLog.i(filePic.name)
                EventBus.getDefault().post(UpdataAvatrrEvent(filePath,false))
//                mPresenter.upLoadImg()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }).start()

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdataAvatrrEvent(UpdataAvatrrEvent: UpdataAvatrrEvent) {
        if(!UpdataAvatrrEvent.isComplete)
        {
            FileMangerUtil.sendAvatarFile(UpdataAvatrrEvent.filePath,"", false)
        }else{
            runOnUiThread {
                closeProgressDialog()
                toast(getString(R.string.save_success))
            }
        }

    }
    fun getRatioSize(bitWidth: Int, bitHeight: Int): Int {
        // 图片最大分辨率
        val imageHeight = 540
        val imageWidth = 540
        // 缩放比
        var ratio = 1
        KLog.i(bitWidth)
        KLog.i(bitHeight)
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth
        } else if (bitWidth <= bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1
        return ratio
    }

    fun startPhotoZoom(uri: Uri?) {
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
                startActivityForResult(intent, 1)
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
                startActivityForResult(intent, 1)
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

    fun upLoadAvatar(filePath : String) {

    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.uploadAvatarBack = null
        super.onDestroy()
    }
}