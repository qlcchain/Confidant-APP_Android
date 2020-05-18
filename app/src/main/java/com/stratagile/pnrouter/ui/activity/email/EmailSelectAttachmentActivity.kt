package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.Toast
import com.hyphenate.easeui.model.EaseCompat
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailSelectAttachmentComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSelectAttachmentContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailSelectAttachmentModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailSelectAttachmentPresenter
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.utils.UIUtils
import com.stratagile.pnrouter.utils.filepick.FilePickerBuilder
import com.stratagile.pnrouter.utils.filepick.FilePickerConst
import com.stratagile.pnrouter.utils.filepick.PickerManager
import com.stratagile.pnrouter.utils.filepick.sort.SortingTypes
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.activity_email_select_attachment.*
import kotlinx.android.synthetic.main.activity_select_circle.llCancel
import kotlinx.android.synthetic.main.activity_select_circle.statusBar
import kotlinx.android.synthetic.main.activity_select_circle.tvTitle
import kotlinx.android.synthetic.main.fragment_file_encryption.*
import java.io.File
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2020/05/13 15:04:52
 */

class EmailSelectAttachmentActivity : BaseActivity(), EmailSelectAttachmentContract.View {

    @Inject
    internal lateinit var mPresenter: EmailSelectAttachmentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        statusBarColor = R.color.headmainColor
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_email_select_attachment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FILE) {
                data!!.putExtra("offsetRequestCode", 0)
                setResult(Activity.RESULT_OK, data)
                finish()
            } else if (requestCode == REQUEST_CODE_LOCAL){
                data!!.putExtra("offsetRequestCode", 1)
                setResult(Activity.RESULT_OK, data)
                finish()
            } else if (requestCode == REQUEST_CODE_VIDEO){
                data!!.putExtra("offsetRequestCode", 2)
                setResult(Activity.RESULT_OK, data)
                finish()
            } else if (requestCode == 111){
                data!!.putExtra("offsetRequestCode", 0)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }
    override fun initData() {
        val llp2 = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp2)
        tvTitle.text = getString(R.string.app_name)
        llCancel.setOnClickListener {
            onBackPressed()
        }
        typePic.setOnClickListener {
            selectPicFromLocal()
        }
        typeVedio.setOnClickListener {
            selectVideoFromLocal()
        }
        typeWord.setOnClickListener {
            val docs = arrayOf("doc", "docx")
            PickerManager.reset()
            FilePickerBuilder.Companion.instance
                    .setMaxCount(9)
                    .addFileSupport(FilePickerConst.DOC, docs)
                    .sortDocumentsBy(SortingTypes.name)
                    .enableDocSupport(true)
                    .pickFile(this)
        }
        typeOther.setOnClickListener {
            val docs = arrayOf("json", "txt", "html", "apk", "md")
            PickerManager.reset()
            FilePickerBuilder.Companion.instance
                    .setMaxCount(9)
                    .addFileSupport(FilePickerConst.OTHER, docs)
                    .sortDocumentsBy(SortingTypes.name)
                    .enableDocSupport(true)
                    .pickFile(this)
        }
        typePpt.setOnClickListener {
            PickerManager.reset()
            val ppts = arrayOf("ppt", "pptx")
            FilePickerBuilder.Companion.instance
                    .setMaxCount(9)
                    .addFileSupport(FilePickerConst.PPT, ppts)
                    .sortDocumentsBy(SortingTypes.name)
                    .enableDocSupport(true)
                    .pickFile(this)
        }
        typeExcel.setOnClickListener {
            PickerManager.reset()
            val excels = arrayOf("xls", "xlsx")
            FilePickerBuilder.Companion.instance
                    .setMaxCount(9)
                    .addFileSupport(FilePickerConst.XLS, excels)
                    .sortDocumentsBy(SortingTypes.name)
                    .enableDocSupport(true)
                    .pickFile(this)
        }
        localFoldersPath.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", ""), 111)
        }
        localDownloadPath.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/Download"), 111)
        }
        appMessenger.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/Download"), 111)
        }
        appTwitter.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/Download"), 111)
        }
        appSnapchat.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/Download"), 111)
        }
        appWhatsApp.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/Download"), 111)
        }
        appWeChat.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2).putExtra("prePath", "/tencent/MicroMsg/Download"), 111)
        }
    }

    private val permission = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                selectPicFromCamera()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")

            }
        }
    }
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6
    protected var cameraFile: File? = null
    /**
     * capture new image
     */
    protected fun selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show()
            return
        }
        cameraFile = File(PathUtils.getInstance().tempPath, (System.currentTimeMillis() / 1000).toString() + ".jpg")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/PicAndVideoTemp", (System.currentTimeMillis() / 1000).toString() + ".jpg")
        }

        try {
            cameraFile!!.getParentFile().mkdirs()
            val uri = EaseCompat.getUriForFile(this, cameraFile)
            startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri),
                    REQUEST_CODE_CAMERA)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.Permissionerror, Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * select local image
     * //todo
     */
    protected fun selectPicFromLocal() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .forResult(REQUEST_CODE_LOCAL)
    }
    /**
     * select local image
     * //todo
     */
    protected fun selectVideoFromLocal() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .forResult(REQUEST_CODE_VIDEO)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.activity_translate_out_1)
    }


    override fun setupActivityComponent() {
       DaggerEmailSelectAttachmentComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .emailSelectAttachmentModule(EmailSelectAttachmentModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: EmailSelectAttachmentContract.EmailSelectAttachmentContractPresenter) {
            mPresenter = presenter as EmailSelectAttachmentPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}