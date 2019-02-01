package com.stratagile.pnrouter.ui.activity.conversation

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RecentFile
import com.stratagile.pnrouter.db.RecentFileDao
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerFileListComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileListContract
import com.stratagile.pnrouter.ui.activity.conversation.module.FileListModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileListPresenter
import com.stratagile.pnrouter.ui.activity.file.FileDetailInformationActivity
import com.stratagile.pnrouter.ui.activity.file.PdfViewActivity
import com.stratagile.pnrouter.ui.adapter.conversation.FileListAdapter
import com.stratagile.pnrouter.utils.LocalFileUtils
import com.stratagile.pnrouter.utils.PopWindowUtil
import kotlinx.android.synthetic.main.fragment_file_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshList(recentFile: RecentFile) {
        fileListAdapter?.setNewData(AppConfig.instance.mDaoMaster!!.newSession().recentFileDao.loadAll().apply { this.reverse() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        var fileList = AppConfig.instance.mDaoMaster!!.newSession().recentFileDao.queryBuilder().where(RecentFileDao.Properties.UserSn.eq(ConstantValue.currentRouterSN)).list().apply { this.reverse() }
        fileListAdapter = FileListAdapter(fileList)
        recyclerView.adapter = fileListAdapter
        fileListAdapter!!.setOnItemClickListener { adapter, view, position ->

//            startActivity(Intent(activity, PdfViewActivity::class.java).putExtra("filePath", filePath))
        }
        fileListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.fileOpreate -> {
                    PopWindowUtil.showFileRecentPopWindow(activity!!, recyclerView, fileListAdapter!!.data[position], object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj : Any) {
                            KLog.i("" + position)
                            when(position) {
                                0 -> {
                                    AppConfig.instance.mDaoMaster!!.newSession().recentFileDao.delete(obj as RecentFile)
                                    EventBus.getDefault().post(obj)
                                }
                            }
                        }

                    })
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
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