package com.stratagile.pnrouter.ui.activity.group

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
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
import com.stratagile.pnrouter.ui.activity.group.component.DaggerGroupMembersComponent
import com.stratagile.pnrouter.ui.activity.group.contract.GroupMembersContract
import com.stratagile.pnrouter.ui.activity.group.module.GroupMembersModule
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupMembersPresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendGroupDetailActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_group_members.*
import kotlinx.android.synthetic.main.ease_search_bar.*

import javax.inject.Inject;
import kotlin.collections.ArrayList

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/22 15:19:37
 */

class GroupMembersActivity : BaseActivity(), GroupMembersContract.View, PNRouterServiceMessageReceiver.GroupMemberback {

    override fun groupUserPull(jGroupUserPullRsp: JGroupUserPullRsp) {
        KLog.i("拉群成员返回。。")
        runOnUiThread {
            if (jGroupUserPullRsp.params.retCode == 0) {
                contactList = jGroupUserPullRsp.params.payload as ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean>;
                if(from != null && from.equals("GroupInfoActivity"))
                {
                    groupMemberAdapter!!.setNewData(contactList)
                }else{
                    val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var contactListNew = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()
                    contactList.forEach {
                        if (!it.toxId.equals(userId)) {
                            contactListNew.add(it)
                        }
                    }
                    groupMemberAdapter!!.setNewData(contactListNew)
                }

            }
        }
    }

    /**
     * 邀请好友加群返回
     */
    override fun groupInvite(jGroupInviteDealRsp: JGroupInviteDealRsp) {
        pullGourpUsersList()
    }

    fun pullGourpUsersList() {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupUserPullReq(userId!!, ConstantValue.currentRouterId, groupEntity!!.gId.toString(), 100, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    @Inject
    internal lateinit var mPresenter: GroupMembersPresenter

    var from:String? = null
    var groupEntity : GroupEntity? = null
    var contactList = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()

    var groupMemberAdapter : GroupMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.color_2c2c2c))
                        view.isAllCaps = false
                        view.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.groupMemberback = null
        super.onDestroy()
    }

    override fun initView() {
        setContentView(R.layout.activity_group_members)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.groupMemberback = this
        title.text = "Group Members"
        groupEntity = intent.getParcelableExtra(EaseConstant.EXTRA_CHAT_GROUP)
        if(intent.hasExtra("from"))
        {
            from = intent.getStringExtra("from")
        }
        pullGourpUsersList()
        groupMemberAdapter = GroupMemberAdapter(arrayListOf())
        recyclerView.adapter = groupMemberAdapter
        refreshLayout.setOnRefreshListener {
            pullGourpUsersList()
            refreshLayout.isRefreshing = false
        }
        groupMemberAdapter?.setOnItemClickListener { adapter, view, position ->
            /**
             * Id : 22
             * Type : 2
             * Local : 1
             * ToxId : FF16BC404B8AD7787CA27C93F176A73CFA03C829E12974138BD22AE1E6F3494A6FE7C38C8C2E
             * Nickname : aHc4ODg=
             * Remarks :
             * UserKey : tWl8pN/7gCJ3LXO/7+D1s10qAYjeJKJdcItZH16RAy4=
             */
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if (userId.equals(groupMemberAdapter!!.data[position].toxId)) {
                return@setOnItemClickListener
            }
            var user = UserEntity()
            user.userId = groupMemberAdapter!!.data[position].toxId
            user.nickName = groupMemberAdapter!!.data[position].nickname
            user.signPublicKey = groupMemberAdapter!!.data[position].userKey
            val userEntities = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
            userEntities.forEach {
                if (it.userId.equals(groupMemberAdapter!!.data[position].toxId)) {
                    user = it
                }
            }
            if(from != null && from.equals("GroupInfoActivity"))
            {
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }else{
                val intent = Intent()
                intent.putExtra("user", user)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        if (groupEntity!!.gAdmin.equals(SpUtil.getString(AppConfig.instance, ConstantValue.userId, ""))) {

        }

        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fiter(s.toString(), contactList)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                getSelectedUser(contactAdapter1!!.data.MutableListToArrayList())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        search_clear.setOnClickListener(View.OnClickListener {

        })
    }

    fun fiter(key: String, contactList: ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean>) {
        if ("".equals(key)) {
            updateAdapterData(contactList)
        } else {
            var contactListTemp: ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean> = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()
            for (i in contactList) {
                var nickSouceName =  String(RxEncodeTool.base64Decode(i.nickname))
                if (nickSouceName.toLowerCase().contains(key)) {
                    contactListTemp.add(i)
                }
            }
            updateAdapterData(contactListTemp)
        }
    }
    fun updateAdapterData(list: ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean>) {
        groupMemberAdapter!!.setNewData(list)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(from != null && from.equals("GroupInfoActivity"))
        {
            menuInflater.inflate(R.menu.add_group_member, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addGroupMember -> {
                //添加好友
                var list = arrayListOf<JGroupUserPullRsp.ParamsBean.PayloadBean>()
                startActivityForResult(Intent(this, SelectFriendGroupDetailActivity::class.java).putParcelableArrayListExtra("person", groupMemberAdapter!!.data.MutableListToArrayList()), 1)
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var aesKey = LibsodiumUtil.DecryptShareKey(groupEntity!!.userKey)+"0000000000000000"
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: ArrayList<UserEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var listString = ""
                    var keyString = ""
                    contactSelectedList.forEach {
                        listString += it.userId + ","
                        var friendAesMi = RxEncodeTool.base64Encode2String(LibsodiumUtil.EncryptShareKey(aesKey, it.miPublicKey))
                        keyString += friendAesMi + ","
                    }
                    listString = listString.substring(0, listString.length - 1)
                    keyString = keyString.substring(0, keyString.length - 1)
                    addGroupMember(listString, keyString)
                }
            }
        }
    }

    /**
     *
     */
    fun addGroupMember(addMembers : String, keyMembers : String) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        KLog.i("要添加的群友为：" + addMembers)
        KLog.i("要添加的群友key为：" + keyMembers)
        val removeMemberReq = InviteGroupReq(userId!!, groupEntity!!.gId.toString(), addMembers, keyMembers)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, removeMemberReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, removeMemberReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }

    override fun setupActivityComponent() {
       DaggerGroupMembersComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .groupMembersModule(GroupMembersModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: GroupMembersContract.GroupMembersContractPresenter) {
            mPresenter = presenter as GroupMembersPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}