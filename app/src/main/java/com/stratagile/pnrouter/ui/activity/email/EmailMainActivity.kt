package com.stratagile.pnrouter.ui.activity.email

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.smailnet.eamil.Callback.GetReceiveCallback
import com.smailnet.eamil.Callback.GetSendCallback
import com.smailnet.eamil.EmailMessage
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.EmailSendClient
import com.smailnet.eamil.Utils.ConstUtli
import com.smailnet.islands.Islands
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailMainComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailMainContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailMainModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailMainPresenter
import kotlinx.android.synthetic.main.email_main_activity.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/02 15:22:53
 */

class EmailMainActivity : BaseActivity(), EmailMainContract.View {

    @Inject
    internal lateinit var mPresenter: EmailMainPresenter
    var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_main_activity)
    }
    override fun initData() {
        addattch.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 1)
        }
        send.setOnClickListener {
            sendMessage()
        }
        receive.setOnClickListener {
            /**
             * 获取邮件
             */
            Islands.circularProgress(this)
                    .setCancelable(false)
                    .setMessage("同步中...")
                    .show()
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .popReceiveAsyn(this@EmailMainActivity, object : GetReceiveCallback {
                                    override fun gainSuccess(messageList: List<EmailMessage>, count: Int) {
                                        progressDialog.dismiss()
                                        Toast.makeText(this@EmailMainActivity, "邮件总数：" + count + " 标题：" + messageList[0].from, Toast.LENGTH_SHORT).show()
                                        Log.i("oversee", "邮件总数：" + count + " 标题：" + messageList[0].from)
                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        Toast.makeText(this@EmailMainActivity, "POP3邮件收取失败", Toast.LENGTH_SHORT).show()
                                        Log.e("oversee", "错误日志：$errorMsg")
                                    }
                                })
                    }
        }
        receive2.setOnClickListener {
            Islands.circularProgress(this)
                    .setCancelable(false)
                    .setMessage("同步中...")
                    .show()
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .imapReceiveAsyn(this@EmailMainActivity, object : GetReceiveCallback {
                                    override fun gainSuccess(messageList: List<EmailMessage>, count: Int) {
                                        progressDialog.dismiss()
                                        Toast.makeText(this@EmailMainActivity, "邮件总数：" + count + " 标题：" + messageList[0].from, Toast.LENGTH_SHORT).show()
                                        Log.i("oversee", "邮件总数：" + count + " 标题：" + messageList[0].subject)
                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        Toast.makeText(this@EmailMainActivity, "IMAP邮件收取失败", Toast.LENGTH_SHORT).show()
                                        Log.e("oversee", "错误日志：$errorMsg")
                                    }
                                },"INBOX")
                    }
        }
    }

    override fun setupActivityComponent() {
       DaggerEmailMainComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .emailMainModule(EmailMainModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: EmailMainContract.EmailMainContractPresenter) {
            mPresenter = presenter as EmailMainPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val uri = data!!.getData()
                if ("file".equals(uri!!.scheme, ignoreCase = true)) {//使用第三方应用打开
                    path = uri.path
                    //Toast.makeText(this,path+"11111",Toast.LENGTH_SHORT).show();
                    return
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = getPath(this, uri)
                    //Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    path = getRealPathFromURI(uri)
                    //Toast.makeText(EmailMainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
                }
                ConstUtli.attchPath = path
                attchText.setText(path)
                Toast.makeText(this, "文件路径：$path", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /**
     * 发送邮件
     */
    private fun sendMessage() {
        val emailSendClient = EmailSendClient(AppConfig.instance.emailConfig())
        emailSendClient
                .setTo(address_editText.getText().toString())                //收件人的邮箱地址
                .setNickname("测试")                                    //发件人昵称
                .setSubject(title_editText.getText().toString())             //邮件标题
                .setContent(text_editText.getText().toString())              //邮件正文
                .sendAsyn(this, object : GetSendCallback {
                    override fun sendSuccess() {
                        Toast.makeText(this@EmailMainActivity, "邮件已发送", Toast.LENGTH_SHORT).show()
                    }

                    override fun sendFailure(errorMsg: String) {
                        Islands.ordinaryDialog(this@EmailMainActivity)
                                .setText(null, "邮件发送失败 ：$errorMsg")
                                .setButton("关闭", null, null)
                                .click().show()
                    }
                })
    }

    fun getRealPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (null != cursor && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }
}