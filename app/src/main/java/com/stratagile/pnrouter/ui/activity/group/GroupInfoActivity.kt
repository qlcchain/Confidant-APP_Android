package com.stratagile.pnrouter.ui.activity.group

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.hyphenate.easeui.EaseConstant
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.GroupListPullReq
import com.stratagile.pnrouter.entity.GroupUserPullReq
import com.stratagile.pnrouter.entity.JGroupUserPullRsp
import com.stratagile.pnrouter.ui.activity.group.component.DaggerGroupInfoComponent
import com.stratagile.pnrouter.ui.activity.group.contract.GroupInfoContract
import com.stratagile.pnrouter.ui.activity.group.module.GroupInfoModule
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupInfoPresenter
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberDecoration
import com.stratagile.pnrouter.ui.adapter.group.GroupUserAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_group_info.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/20 11:44:58
 */

class GroupInfoActivity : BaseActivity(), GroupInfoContract.View, PNRouterServiceMessageReceiver.GroupDetailBack {
    override fun groupUserPull(jGroupUserPullRsp: JGroupUserPullRsp) {
        KLog.i("拉群成员返回。。")
        runOnUiThread {
            userList.clear()
            userList.addAll(jGroupUserPullRsp.params.payload)
            userList.add(addUser)
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if (groupEntity!!.gAdmin.equals(userId)) {
                userList.add(reduceUser)
            }
            groupUserAdapter?.notifyDataSetChanged()
        }
    }

    @Inject
    internal lateinit var mPresenter: GroupInfoPresenter

    var groupEntity: GroupEntity? = null

    var groupUserAdapter : GroupUserAdapter? = null

    lateinit var addUser: JGroupUserPullRsp.ParamsBean.PayloadBean
    lateinit var reduceUser: JGroupUserPullRsp.ParamsBean.PayloadBean
    var userList = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_group_info)
        addUser = JGroupUserPullRsp.ParamsBean.PayloadBean()
        reduceUser = JGroupUserPullRsp.ParamsBean.PayloadBean()
        reduceUser.nickname = "0"
        addUser.nickname = "1"
    }

    override fun initData() {
        groupEntity = intent.getParcelableExtra(EaseConstant.EXTRA_CHAT_GROUP)
        AppConfig.instance.messageReceiver?.groupDetailBack = this
        pullGourpUsersList()
        title.text = "Group Details"
        if (groupEntity != null) {
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if (groupEntity!!.gAdmin.equals(userId)) {
                //是群主
                llApproveInnvitationn.visibility = View.VISIBLE
                tvIntroduce.visibility = View.VISIBLE
                tvDismissGroup.visibility = View.VISIBLE
                tvLeaveGroup.visibility = View.GONE
                ivModify.visibility = View.VISIBLE
            } else {
                llApproveInnvitationn.visibility = View.GONE
                tvIntroduce.visibility = View.GONE
                tvDismissGroup.visibility = View.GONE
                tvLeaveGroup.visibility = View.VISIBLE
                ivModify.visibility = View.GONE
            }
            groupName.text = String(RxEncodeTool.base64Decode(groupEntity!!.gName))
        }
        groupUserAdapter = GroupUserAdapter(userList)
        val gridLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.setNestedScrollingEnabled(false)
        gridLayoutManager.isSmoothScrollbarEnabled = true
        gridLayoutManager.isAutoMeasureEnabled = true
        recyclerView.setLayoutManager(gridLayoutManager)
        recyclerView.addItemDecoration(GroupMemberDecoration())
        recyclerView.adapter = groupUserAdapter

    }

    fun pullGourpUsersList() {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupUserPullReq(userId!!, ConstantValue.currentRouterId, groupEntity!!.gId.toString(), 6, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    override fun setupActivityComponent() {
        DaggerGroupInfoComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .groupInfoModule(GroupInfoModule(this))
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.groupDetailBack = null
        super.onDestroy()
    }

    override fun setPresenter(presenter: GroupInfoContract.GroupInfoContractPresenter) {
        mPresenter = presenter as GroupInfoPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}