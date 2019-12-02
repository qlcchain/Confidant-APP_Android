package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.LocalFileItemDao
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionlListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionlListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionlListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionlListPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.utils.DeleteUtils
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.encryption_file_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:22
 */

class PicEncryptionlListActivity : BaseActivity(), PicEncryptionlListContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionlListPresenter
    var picItemEncryptionAdapter: PicItemEncryptionAdapter? = null
    var folderInfo:LocalFileMenu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encryption_file_list)

    }
    override fun initData() {
        folderInfo = intent.getParcelableExtra("folderInfo")
        titleShow.text = folderInfo!!.fileName

        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).list()
        picItemEncryptionAdapter = PicItemEncryptionAdapter(picMenuList)
        recyclerView.adapter = picItemEncryptionAdapter
        /*picItemEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picItemEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }*/
        picItemEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem ->
                {
                   
                }

            }
        }
    }

    override fun setupActivityComponent() {
       DaggerPicEncryptionlListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .picEncryptionlListModule(PicEncryptionlListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionlListContract.PicEncryptionlListContractPresenter) {
            mPresenter = presenter as PicEncryptionlListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}