package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileManagerComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileManagerContract
import com.stratagile.pnrouter.ui.activity.file.module.FileManagerModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileManagerPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.FileListAdapter
import com.stratagile.pnrouter.ui.adapter.conversation.FileListChooseAdapter
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.utils.ShareUtil
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_file_manager.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/23 14:15:29
 */

class FileManagerActivity : BaseActivity(), FileManagerContract.View {

    @Inject
    internal lateinit var mPresenter: FileManagerPresenter

    var fileListChooseAdapter : FileListChooseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_manager)
        sort.setOnClickListener {
            PopWindowUtil.showFileSortWindow(this, sort, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {

                }

            })
        }
    }


    override fun initData() {
        title.text = "Documents received"
        var list = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        fileListChooseAdapter = FileListChooseAdapter(list)
        recyclerView.adapter = fileListChooseAdapter
        fileListChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
            var filePath = "" + Environment.getExternalStorageDirectory() + "/1/接口介绍.pdf"
            if (position == 0) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/test.txt"
            } else if (position == 1) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/tupian.jpg"
            } else if (position == 2) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/xxx.jpg"
            } else if (position == 3) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/vpn.xlsx"
            }
            startActivity(Intent(this, PdfViewActivity::class.java).putExtra("filePath", filePath))
        }

        fileListChooseAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.fileOpreate -> {
                    PopWindowUtil.showFileOpreatePopWindow(this@FileManagerActivity, recyclerView, fileListChooseAdapter!!.data[position], object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj : Any) {
                            KLog.i("" + position)
                            when(position) {
                                0 -> {

                                }
                                1 -> {

                                }
                                2 -> {

                                }
                                3 -> {
                                    startActivity(Intent(this@FileManagerActivity, FileDetailInformationActivity::class.java))
                                }
                                4 -> {

                                }
                                5 -> {

                                }
                            }
                        }

                    })
                }
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerFileManagerComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .fileManagerModule(FileManagerModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FileManagerContract.FileManagerContractPresenter) {
            mPresenter = presenter as FileManagerPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.chooseFile) {
            fileListChooseAdapter!!.isChooseMode = !fileListChooseAdapter!!.isChooseMode
            fileListChooseAdapter!!.notifyDataSetChanged()
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
        inflater.inflate(R.menu.choose_file, menu)
        return super.onCreateOptionsMenu(menu)
    }

}