package com.stratagile.pnrouter.ui.activity.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CompoundButton
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
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.group.component.DaggerGroupInfoComponent
import com.stratagile.pnrouter.ui.activity.group.contract.GroupInfoContract
import com.stratagile.pnrouter.ui.activity.group.module.GroupInfoModule
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupInfoPresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendGroupDetailActivity
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberDecoration
import com.stratagile.pnrouter.ui.adapter.group.GroupUserAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_edit_nick_name.*
import kotlinx.android.synthetic.main.activity_group_info.*
import kotlinx.android.synthetic.main.fragment_my.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/20 11:44:58
 */

class GroupInfoActivity : BaseActivity(), GroupInfoContract.View, PNRouterServiceMessageReceiver.GroupDetailBack {
    override fun quitGroup(jGroupQuitRsp: JGroupQuitRsp) {
        //退出该群的返回
        if (jGroupQuitRsp.params.retCode == 0) {
            EventBus.getDefault().post(jGroupQuitRsp)
            finish()
        }
    }

    override fun groupConfig(jGroupConfigRsp: JGroupConfigRsp) {
        if (jGroupConfigRsp.params.retCode == 0) {
            when(jGroupConfigRsp.params.type) {
                //修改群名称
                1 -> {

                }
                //是否要验证加群
                2 -> {
                    if (approveInvitation.isChecked) {
                        groupEntity!!.verify = 1
                    } else {
                        groupEntity!!.verify = 0
                    }
                    EventBus.getDefault().post(groupEntity)
                }
                //踢出某个用户
                3 -> {
                    groupEntity!!.gName = String(RxEncodeTool.base64Decode(groupName.text.toString()))
                    EventBus.getDefault().post(groupEntity)
                }
                //修改群组别名
                241 -> {
                    groupEntity!!.remark = String(RxEncodeTool.base64Decode(tvGroupAlias.text.toString()))
                    EventBus.getDefault().post(groupEntity)
                }
            }
        }
    }

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

    val addGroupMember = 0
    val removeGroupMember = 1
    val editGroupName = 2
    val editGroupAlias = 3

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
        reduceUser.toxId = "0"
        addUser.toxId = "1"
    }

    var userId = ""
    override fun initData() {
        userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        groupEntity = intent.getParcelableExtra(EaseConstant.EXTRA_CHAT_GROUP)
        AppConfig.instance.messageReceiver?.groupDetailBack = this
        pullGourpUsersList()
        approveInvitation.isChecked = groupEntity!!.verify == 1
        approveInvitation.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    approveInvitation(1)
                } else {
                    approveInvitation(0)
                }
            }

        })
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
                groupName.setOnClickListener{
                    val intent = Intent(this, EditNickNameActivity::class.java)
                    intent.putExtra("flag", "Set Group Name")
                    intent.putExtra("alias", String(RxEncodeTool.base64Decode(groupEntity!!.gName)))
                    intent.putExtra("hint", "Set Group Name")
                    startActivityForResult(intent, editGroupName)
                }
            } else {
                llApproveInnvitationn.visibility = View.GONE
                tvIntroduce.visibility = View.GONE
                tvDismissGroup.visibility = View.GONE
                tvLeaveGroup.visibility = View.VISIBLE
                ivModify.visibility = View.GONE
            }
            groupName.text = String(RxEncodeTool.base64Decode(groupEntity!!.gName))
            if ("".equals(groupEntity!!.remark)) {
                tvGroupAlias.text = String(RxEncodeTool.base64Decode(groupEntity!!.gName))
            } else {
                tvGroupAlias.text = String(RxEncodeTool.base64Decode(groupEntity!!.remark))
            }
        }
        groupUserAdapter = GroupUserAdapter(userList)
        val gridLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.setNestedScrollingEnabled(false)
        gridLayoutManager.isSmoothScrollbarEnabled = true
        gridLayoutManager.isAutoMeasureEnabled = true
        recyclerView.setLayoutManager(gridLayoutManager)
        recyclerView.addItemDecoration(GroupMemberDecoration())
        recyclerView.adapter = groupUserAdapter
        tvDismissGroup.setOnClickListener()
        {
            dismissGroup()
        }
        tvLeaveGroup.setOnClickListener()
        {
            quitGroup()
        }
        llGroupAlias.setOnClickListener {
            val intent = Intent(this, EditNickNameActivity::class.java)
            intent.putExtra("flag", "Set Group Alias")
            if ("".equals(groupEntity!!.remark)) {
                intent.putExtra("alias", String(RxEncodeTool.base64Decode(groupEntity!!.gName)))
            } else {
                intent.putExtra("alias", String(RxEncodeTool.base64Decode(groupEntity!!.remark)))
            }
            intent.putExtra("hint", "Set Group Alias")
            startActivityForResult(intent, editGroupAlias)
        }
        groupUserAdapter!!.setOnItemClickListener { adapter, view, position ->
            when (groupUserAdapter!!.data[position].toxId) {
                "0" -> {
                    var list = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()
                    list.addAll(groupUserAdapter!!.data)
                    startActivityForResult(Intent(this@GroupInfoActivity, RemoveGroupDetailMemberActivity::class.java).putParcelableArrayListExtra("person", list), removeGroupMember)
                }
                "1" -> {
                    //添加好友
                    var list = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()
                    list.addAll(groupUserAdapter!!.data)
                    startActivityForResult(Intent(this@GroupInfoActivity, SelectFriendGroupDetailActivity::class.java).putParcelableArrayListExtra("person", list), addGroupMember)
                }
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == addGroupMember && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: ArrayList<UserEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var listString = ""
                    var keyString = ""
                    contactSelectedList.forEach {
                        listString += it.userId + ","
                        keyString += it.signPublicKey + ","
                    }
                    listString = listString.substring(0, listString.length - 1)
                    keyString = keyString.substring(0, keyString.length - 1)
                    addGroupMember(listString, keyString)
                }
            }
        } else if (requestCode == removeGroupMember && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: ArrayList<UserEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var useridList = arrayListOf<String>()
                    var listString = ""
                    contactSelectedList.forEach {
                        listString += it.userId + ","
                    }
                    listString = listString.substring(0, listString.length - 1)
                    removeGroupMember(listString)
                }
            }
        } else if (requestCode == editGroupName && resultCode == Activity.RESULT_OK) {
            var groupName1 = data!!.getStringExtra("alias")
            if (groupName1 != null) {
                groupName.text = groupName1
                modifyGroupName(groupName1)
            }
        } else if (requestCode == editGroupAlias && resultCode == Activity.RESULT_OK) {
            var groupName1 = data!!.getStringExtra("alias")
            if (groupName1 != null) {
                tvGroupAlias.text = groupName1
                modifyGroupAlias(groupName1)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     *
     */
    fun addGroupMember(addMembers : String, keyMembers : String) {
        KLog.i("要添加的群友为：" + addMembers)
        KLog.i("要添加的群友key为：" + keyMembers)
        val removeMemberReq = InviteGroupReq(userId, groupEntity!!.gId.toString(), addMembers, keyMembers)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, removeMemberReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, removeMemberReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 移除群成员，只有管理员有权限
     */
    fun removeGroupMember(removeMembers : String) {
        KLog.i("要删除的群友为：" + removeMembers)
        val removeMemberReq = RemoveGroupMemberReq(userId, groupEntity!!.gId.toString(), removeMembers)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, removeMemberReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, removeMemberReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 修改群名称，只有管理员有权限
     */
    fun modifyGroupName(name : String) {
        val groupQuitReq = ModifyGroupNameReq(userId, groupEntity!!.gId.toString(), String(RxEncodeTool.base64Encode(name)))
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupQuitReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, groupQuitReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 设置是否需要群管理员审核才能入群，只有管理员才有权限
     * 是否需要审核入群
    0：不需要
    1：必须
     */
    fun approveInvitation(needApprove : Int) {
        val groupQuitReq = SettingApproveInvitationReq(userId, groupEntity!!.gId.toString(), needApprove)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupQuitReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, groupQuitReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 修改群别名
     */
    fun modifyGroupAlias(alias : String) {
        val groupQuitReq = ModifyGroupAliasReq(userId, groupEntity!!.gId.toString(), String(RxEncodeTool.base64Encode(alias)))
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupQuitReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, groupQuitReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 修改群友别名
     */
    fun modifyMemberAlias() {

    }

    /**
     * 修改自己在群中显示的别名
     */
    fun modifySelfAliasInGroup() {

    }

    /**
     * 用户退出群聊
     */
    fun quitGroup() {
        val groupQuitReq = GroupQuitReq(userId, groupEntity!!.gId.toString(), groupEntity!!.gName)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupQuitReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, groupQuitReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    /**
     * 解散群聊，只有群主才有权限
     */
    fun dismissGroup() {

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