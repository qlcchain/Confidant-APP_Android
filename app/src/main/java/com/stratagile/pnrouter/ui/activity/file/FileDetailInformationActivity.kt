package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileDetailInformationComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileDetailInformationContract
import com.stratagile.pnrouter.ui.activity.file.module.FileDetailInformationModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileDetailInformationPresenter
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.NetUtils
import com.stratagile.pnrouter.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_file_detail_information.*
import kotlinx.android.synthetic.main.ease_chat_menu_item.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/23 17:49:28
 */

class FileDetailInformationActivity : BaseActivity(), FileDetailInformationContract.View {

    @Inject
    internal lateinit var mPresenter: FileDetailInformationPresenter

    lateinit var file : JPullFileListRsp.ParamsBean.PayloadBean
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_detail_information)
    }
    override fun initData() {
        title.text = "Details"
        file = intent.getParcelableExtra("file")
        var fileName1 = String(Base58.decode(file.fileName.substring(file.fileName.lastIndexOf("/") + 1)))
        fileName.text = fileName1
        fileType.text = fileName1.substring(fileName1.lastIndexOf(".") + 1)
        fileTime.text = TimeUtil.getFileListTime(file.timestamp.toLong())
        fileSize.text = NetUtils.parseSize(file.fileSize.toLong())
        fileSource.text = ""
//        shareSet.setOnClickListener {
//            startActivity(Intent(this, FileShareSetActivity::class.java))
//        }
    }

    override fun setupActivityComponent() {
       DaggerFileDetailInformationComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .fileDetailInformationModule(FileDetailInformationModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FileDetailInformationContract.FileDetailInformationContractPresenter) {
            mPresenter = presenter as FileDetailInformationPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}