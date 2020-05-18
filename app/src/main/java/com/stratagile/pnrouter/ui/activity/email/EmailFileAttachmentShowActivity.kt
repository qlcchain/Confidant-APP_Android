package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.ContentUris
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailFileAttachmentShowComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailFileAttachmentShowContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailFileAttachmentShowModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailFileAttachmentShowPresenter
import com.stratagile.pnrouter.ui.adapter.email.AttachmentFileEntity
import com.stratagile.pnrouter.ui.adapter.email.EmailFileSelectAdapter
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.filepick.*
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.PDF
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.PPT
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.DOC
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.OTHER
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.XLS
import com.stratagile.pnrouter.utils.filepick.FilePickerConst.TXT
import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType
import kotlinx.android.synthetic.main.activity_email_file_attachment_show.*
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2020/05/14 10:45:17
 */

class EmailFileAttachmentShowActivity : BaseActivity(), EmailFileAttachmentShowContract.View {

    @Inject
    internal lateinit var mPresenter: EmailFileAttachmentShowPresenter
    lateinit var emailFileSelectAdapter : EmailFileSelectAdapter

    var selectFileeType = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarColor = R.color.headmainColor
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_email_file_attachment_show)
    }

    override fun initData() {
        registerContentObserver()
        selectFileeType = intent.getStringExtra(FilePickerConst.EXTRA_FILE_TYPE)
        title.text = selectFileeType
        emailFileSelectAdapter = EmailFileSelectAdapter(arrayListOf())
        var selection = ""
        when (selectFileeType) {
            PDF -> {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'" + ")"
            }
            PPT -> {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.ppt'" + ")"
            }
            TXT -> {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt'" + ")"
            }
            DOC -> {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" + ")"
            }
            XLS -> {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.xlsx'" + ")"
            }
            OTHER -> {
                //"json", "txt", "apk", "md"
                selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.json'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.html'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'" +
                        " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.md'" + ")"
            }
        }
        var files = queryDocs(selection, PickerManager.getFileTypes(), PickerManager.sortingType.comparator)
        var fileList = mutableListOf<AttachmentFileEntity>()
        files.forEach {
            it.value.forEach { docment ->
                var attachmentFileEntity = AttachmentFileEntity()
                attachmentFileEntity.isFile = true
                attachmentFileEntity.isSelect = false
                attachmentFileEntity.modifyTime = docment.date
                attachmentFileEntity.size = docment.size!!.toLong()
                attachmentFileEntity.path = docment.path
                attachmentFileEntity.name = docment.name
                attachmentFileEntity.type = it.key.title
                fileList.add(attachmentFileEntity)
            }
        }
        KLog.i("文件的数量为:" + fileList.size)
        emailFileSelectAdapter.setNewData(fileList.sortedBy { it.modifyTime.toLong() }.asReversed())
        recyclerView.adapter = emailFileSelectAdapter
        emailFileSelectAdapter.setOnItemClickListener { adapter, view, position ->
            var resultIntent = Intent()
            resultIntent.putExtra("path", emailFileSelectAdapter.data[position].path)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
//            emailFileSelectAdapter.data[position].isSelect = !emailFileSelectAdapter.data[position].isSelect
//            emailFileSelectAdapter.notifyItemChanged(position, "")
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.select_attachment_confirm, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.confirm -> {
//                if (emailFileSelectAdapter.data.filter { it.isSelect }.size > 0) {
//                    var resultIntent = Intent()
//                    resultIntent.putExtra("path", emailFileSelectAdapter.data.filter { it.isSelect }[0].path)
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()
//                }
//            }
//            else -> {
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private var contentObserver: ContentObserver? = null

    private val _lvDataChanged = MutableLiveData<Boolean>()
    val lvDataChanged: LiveData<Boolean>
        get() = _lvDataChanged

    private fun registerContentObserver() {
        if (contentObserver == null) {
            contentObserver = contentResolver.registerObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                _lvDataChanged.value = true
            }
        }
    }

    override fun setupActivityComponent() {
        DaggerEmailFileAttachmentShowComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailFileAttachmentShowModule(EmailFileAttachmentShowModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: EmailFileAttachmentShowContract.EmailFileAttachmentShowContractPresenter) {
        mPresenter = presenter as EmailFileAttachmentShowPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    fun queryDocs(selection: String, fileType: List<FileType>, comparator: Comparator<Document>): HashMap<FileType, List<Document>> {
        var data = HashMap<FileType, List<Document>>()
//        selection = (MediaStore.Files.FileColumns.MEDIA_TYPE
//                + "!="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
//                + " AND "
//                + MediaStore.Files.FileColumns.MEDIA_TYPE
//                + "!="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

//            selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.xls'" +
//                    " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" +
//                    " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" +
//                    " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'" +
//                    " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.xlsx'" +
//                    " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.rar'" + ")";

        val DOC_PROJECTION = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.TITLE)
        val cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), DOC_PROJECTION, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC")
//            val cursor = getApplication<Application>().contentResolver.query(MediaStore.Files.getContentUri("external"), null, null, null, null)

        if (cursor != null) {
            data = createDocumentType(fileType, comparator, getDocumentFromCursor(cursor))
            cursor.close()
        }
        data.forEach {
            KLog.i(it.key.title)
            it.value.forEach {
                KLog.i(it.name + it.mimeType)
            }
        }
        return data
    }

    private fun createDocumentType(fileTypes: List<FileType>, comparator: Comparator<Document>?, documents: MutableList<Document>): HashMap<FileType, List<Document>> {
        val documentMap = HashMap<FileType, List<Document>>()

        fileTypes.forEach {fileType ->
            val documentListFilteredByType = documents.filter { document -> FilePickerUtils.contains(fileType.extensions, document.mimeType) }

            comparator?.let {
                documentListFilteredByType.sortedWith(comparator)
            }
            documentMap[fileType] = documentListFilteredByType
        }
        return documentMap
    }

    private fun getDocumentFromCursor(data: Cursor): MutableList<Document> {
        val documents = mutableListOf<Document>()
        while (data.moveToNext()) {

            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            val date = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
            var title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
            var displayName = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
            if (displayName == null) {
                displayName = title
            }
            KLog.i(displayName)
            KLog.i(date)
            KLog.i(path)
            if (path != null) {
                var types = PickerManager.getFileTypes()
                val fileType = getFileType(PickerManager.getFileTypes(), path)
                val file = File(path)
                val contentUri = ContentUris.withAppendedId(
                        MediaStore.Files.getContentUri("external"),
                        imageId
                )
                if (fileType != null && !file.isDirectory && file.exists()) {

                    val document = Document(imageId, displayName, path)
                    document.fileType = fileType

                    val mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
                        document.mimeType = mimeType
                    } else {
                        document.mimeType = ""
                    }

                    document.size = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                    document.date = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    if (!documents.contains(document)) documents.add(document)
                }
            }
        }

        return documents
    }

    private fun getFileType(types: ArrayList<FileType>, path: String): FileType? {
        for (index in types.indices) {
            for (string in types[index].extensions) {
                if (path.endsWith(string)) return types[index]
            }
        }
        return null
    }

}