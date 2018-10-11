package com.stratagile.pnrouter.ui.activity.conversation

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerFileListComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileListContract
import com.stratagile.pnrouter.ui.activity.conversation.module.FileListModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileListPresenter
import com.stratagile.pnrouter.ui.activity.file.PdfViewActivity
import com.stratagile.pnrouter.ui.adapter.conversation.FileListAdapter
import kotlinx.android.synthetic.main.fragment_file_list.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2018/09/13 15:32:14
 */

class FileListFragment : BaseFragment(), FileListContract.View {

    @Inject
    lateinit internal var mPresenter: FileListPresenter

    var fileListAdapter : FileListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file_list, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var list = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        fileListAdapter = FileListAdapter(list)
        recyclerView.adapter = fileListAdapter
        fileListAdapter!!.setOnItemClickListener { adapter, view, position ->
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
            startActivity(Intent(activity, PdfViewActivity::class.java).putExtra("filePath", filePath))
        }
    }


    override fun setupFragmentComponent() {
        DaggerFileListComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileListModule(FileListModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileListContract.FileListContractPresenter) {
        mPresenter = presenter as FileListPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}