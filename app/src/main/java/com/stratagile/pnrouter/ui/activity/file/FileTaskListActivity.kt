package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.entity.file.TaskFile
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileTaskListComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileTaskListContract
import com.stratagile.pnrouter.ui.activity.file.module.FileTaskListModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileTaskListPresenter
import com.stratagile.pnrouter.ui.adapter.file.FileTaskLisytAdapter
import kotlinx.android.synthetic.main.activity_file_task_list.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/25 16:21:04
 */

class FileTaskListActivity : BaseActivity(), FileTaskListContract.View {

    @Inject
    internal lateinit var mPresenter: FileTaskListPresenter

    lateinit var fileTaskLisytAdapter: FileTaskLisytAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_task_list)
    }

    lateinit var ongoingTaskHead : TaskFile
    lateinit var completeTaskHead : TaskFile
    override fun initData() {
        var listData = intent.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
        title.text = "Task List"
        var list = mutableListOf<TaskFile>()
        ongoingTaskHead = TaskFile(true, "111")
        completeTaskHead = TaskFile(true, "222")
        list.add(ongoingTaskHead)
        list.add(TaskFile(UpLoadFile("ccc", true, false, true)))
        list.add(TaskFile(UpLoadFile("ccc", true, false, false)))
        list.add(TaskFile(UpLoadFile("ccc", false, false, true)))
        list.add(TaskFile(UpLoadFile("ccc", false, false, false)))

        list.add(completeTaskHead)
        list.add(TaskFile(UpLoadFile("ccc", false, true)))
        list.add(TaskFile(UpLoadFile("ccc", false, true)))
        list.add(TaskFile(UpLoadFile("ccc", false, true)))
        fileTaskLisytAdapter = FileTaskLisytAdapter(list)
        reSetHeadTitle()
        recyclerView.adapter = fileTaskLisytAdapter
    }

    fun reSetHeadTitle() {
        var ongoing = 0
        var complete = 0
        fileTaskLisytAdapter.data.forEach {
            if (!it.isHeader) {
                if (it.t.isComplete) {
                    complete++
                } else {
                    ongoing++
                }
            }
        }
        ongoingTaskHead.header = "Ongoing (" + ongoing + ")"
        completeTaskHead.header = "Completed (" + complete + ")"
        fileTaskLisytAdapter.notifyDataSetChanged()
    }

    override fun setupActivityComponent() {
       DaggerFileTaskListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .fileTaskListModule(FileTaskListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FileTaskListContract.FileTaskListContractPresenter) {
            mPresenter = presenter as FileTaskListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}