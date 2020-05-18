package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.hyphenate.chat.EMMessage
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.email.EmailChooseActivity
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerShareFileComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ShareFileContract
import com.stratagile.pnrouter.ui.activity.main.module.ShareFileModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ShareFilePresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity
import com.stratagile.pnrouter.utils.RxConstTool
import com.stratagile.pnrouter.utils.RxDataTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_select_circle.llCancel
import kotlinx.android.synthetic.main.activity_select_circle.statusBar
import kotlinx.android.synthetic.main.activity_select_circle.tvTitle
import kotlinx.android.synthetic.main.activity_share_file.*
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2020/05/12 14:06:39
 */

class ShareFileActivity : BaseActivity(), ShareFileContract.View {

    @Inject
    internal lateinit var mPresenter: ShareFilePresenter

    var filePath = ""
    var mineType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_share_file)
    }
    override fun initData() {
        val llp2 = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp2)
        tvTitle.text = getString(R.string.app_name)
        llCancel.setOnClickListener {
            onBackPressed()
        }
        if (intent.action == Intent.ACTION_SEND) {

        } else {
            filePath = intent.getStringExtra("filePath")
            mineType = intent.getStringExtra("mineType")
            var file = File(filePath)
            if (file.exists() && file.isFile) {
                tvFileName.text = getFileNameNoEx(file.name)
                if (file.length() < 1048576) {
                    tvFileSize.text = getExtensionName(file.name) + " " + RxDataTool.byte2Size(file.length(), RxConstTool.MemoryUnit.KB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " KB"
                } else {
                    tvFileSize.text = getExtensionName(file.name) + " " + RxDataTool.byte2Size(file.length(), RxConstTool.MemoryUnit.MB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " MB"
                }
                when(getExtensionName(file.name).toLowerCase()) {
                    "pdf" -> {
                        ivFileType.setImageResource(R.mipmap.pdf)
                    }
                    "doc" -> {
                        ivFileType.setImageResource(R.mipmap.doc)
                    }
                    "docx" -> {
                        ivFileType.setImageResource(R.mipmap.doc)
                    }
                    "xls" -> {
                        ivFileType.setImageResource(R.mipmap.xls)
                    }
                    "ppt" -> {
                        ivFileType.setImageResource(R.mipmap.ppt)
                    }
                    "txt" -> {
                        ivFileType.setImageResource(R.mipmap.txt)
                    }
                    else -> {
                        ivFileType.setImageResource(R.mipmap.other)
                    }
                }
            }
            sendContact.setOnClickListener {
                var message: EMMessage = EMMessage.createFileSendMessage(filePath, "123456")
                val intent = Intent(this, selectFriendActivity::class.java)
                intent.putExtra("fromId", "")
                intent.putExtra("filePath", filePath)
                startActivityForResult(intent, 0)
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out)
            }
            addToEmail.setOnClickListener {
                var localemailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
                if (localemailConfigEntityList.size == 0) {
                    startActivity(Intent(this, EmailChooseActivity::class.java))
                    return@setOnClickListener
                }
                var intent = Intent(this, EmailSendActivity::class.java)
                intent.putExtra("flag", 3)
                intent.putExtra("filePath", filePath)
                intent.putExtra("menu", "")
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.activity_translate_out_1)
    }

    /*
    * Java文件操作 获取文件扩展名
    * */
    fun getExtensionName(filename: String): String {
        if (filename != null && filename.length > 0) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length - 1) {
                return filename.substring(dot + 1)
            }
        }
        return filename
    }

    /*
    * Java文件操作 获取不带扩展名的文件名
    * */
    fun getFileNameNoEx(filename: String): String {
        if (filename != null && filename.length > 0) {
            val dot = filename.lastIndexOf('.')
            if (dot > -1 && dot < filename.length) {
                return filename.substring(0, dot)
            }
        }
        return filename
    }

    override fun setupActivityComponent() {
       DaggerShareFileComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .shareFileModule(ShareFileModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ShareFileContract.ShareFileContractPresenter) {
            mPresenter = presenter as ShareFilePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}