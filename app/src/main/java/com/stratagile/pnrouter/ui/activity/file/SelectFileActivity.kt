package com.stratagile.pnrouter.ui.activity.file

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.chad.library.adapter.base.BaseQuickAdapter
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.PullFileListReq
import com.stratagile.pnrouter.ui.activity.file.component.DaggerSelectFileComponent
import com.stratagile.pnrouter.ui.activity.file.contract.SelectFileContract
import com.stratagile.pnrouter.ui.activity.file.module.SelectFileModule
import com.stratagile.pnrouter.ui.activity.file.presenter.SelectFilePresenter
import com.stratagile.pnrouter.ui.adapter.conversation.FileListChooseAdapter
import com.stratagile.pnrouter.ui.adapter.file.FileSelectAdapter
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_select_file.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/04/02 17:51:39
 */

class SelectFileActivity : BaseActivity(), SelectFileContract.View, PNRouterServiceMessageReceiver.FileChooseBack {
    override fun pullFileListRsp(pullFileListRsp: JPullFileListRsp) {
        if (currentPage == 0) {
            if (pullFileListRsp.params.payload != null && pullFileListRsp.params.payload.size != 0) {
                lastPayload = pullFileListRsp.params!!.payload.last()
            }
            runOnUiThread {
                fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload)
            }
        } else {
            runOnUiThread {
                fileListChooseAdapter?.loadMoreComplete()
                if (pullFileListRsp.params.fileNum == 0) {
                    lastPayload = null
                    KLog.i("全部数据加载完成。。")
                    fileListChooseAdapter?.loadMoreEnd(true)
                } else {
                    KLog.i("还有数据需要加载。。")
                    lastPayload = pullFileListRsp.params!!.payload.last()
                    fileListChooseAdapter!!.addData(pullFileListRsp.params!!.payload)
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: SelectFilePresenter
    var lastPayload : JPullFileListRsp.ParamsBean.PayloadBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_select_file)
    }

    var fileListChooseAdapter: FileSelectAdapter? = null

    override fun initData() {
        title.text = getString(R.string.select_a_file)
        localFiles.setOnClickListener {
            startActivityForResult(Intent(this, FileChooseActivity::class.java).putExtra("fileType", 2), 5)
        }
        fileListChooseAdapter = FileSelectAdapter(arrayListOf())
        fileListChooseAdapter?.setEnableLoadMore(true)
        fileListChooseAdapter?.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
            override fun onLoadMoreRequested() {
                recyclerView.postDelayed({
                    if (lastPayload == null || fileListChooseAdapter!!.data.size < 10) {
                        fileListChooseAdapter?.loadMoreEnd(true)
                        fileListChooseAdapter!!.loadMoreComplete()
                    } else {
                        currentPage = fileListChooseAdapter!!.data[fileListChooseAdapter!!.data.size - 1].msgId
                        pullFileList(fileListChooseAdapter!!.data[fileListChooseAdapter!!.data.size - 1].msgId)
                    }
                }, 500)
            }

        })
        fileListChooseAdapter?.setOnItemClickListener { adapter, view, position ->
            var data =  fileListChooseAdapter!!.getItem(position)
            var intent = Intent()
            intent.putExtra("fileData", data)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        AppConfig.instance.messageReceiver?.fileChooseBack = this
        pullFileList(0)
        recyclerView.adapter = fileListChooseAdapter
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.fileChooseBack = null
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra("path")
            var intent = Intent()
            intent.putExtra("path", filePath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    var currentPage = 0
    fun pullFileList(startId: Int) {
        currentPage = startId
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var pullFileListReq = PullFileListReq(selfUserId!!, startId, 10, 0, 0)
        var sendData = BaseData(5, pullFileListReq)
        if (ConstantValue.isWebsocketConnected) {
            Log.i("pullFriendList", "webosocket" + AppConfig.instance.getPNRouterServiceMessageSender())
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        } else if (ConstantValue.isToxConnected) {
            Log.i("pullFriendList", "tox")
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    override fun setupActivityComponent() {
        DaggerSelectFileComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectFileModule(SelectFileModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: SelectFileContract.SelectFileContractPresenter) {
        mPresenter = presenter as SelectFilePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}