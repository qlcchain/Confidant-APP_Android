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
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hyphenate.easeui.EaseConstant
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.db.GroupEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.AddGroupChange
import com.stratagile.pnrouter.entity.events.FromChat
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.group.component.DaggerGroupChatsComponent
import com.stratagile.pnrouter.ui.activity.group.contract.GroupChatsContract
import com.stratagile.pnrouter.ui.activity.group.module.GroupChatsModule
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupChatsPresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.adapter.group.GroupAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_group_chats.*
import kotlinx.android.synthetic.main.ease_search_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/12 15:05:01
 */

class GroupChatsActivity : BaseActivity(), GroupChatsContract.View, PNRouterServiceMessageReceiver.GroupListPullBack {
    override fun groupListPull(jGroupListPullRsp: JGroupListPullRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        when(jGroupListPullRsp.params.retCode)
        {
            0->
            {
                groupEntityList = arrayListOf<GroupEntity>()
                for (item in jGroupListPullRsp.params.payload)
                {
                    var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(item.gId)).list()
                    if(groupList.size > 0)
                    {
                        var GroupLocal = groupList.get(0)
                        GroupLocal.userKey = item.userKey
                        GroupLocal.remark = item.remark
                        GroupLocal.gId = item.gId
                        GroupLocal.gAdmin = item.gAdmin
                        GroupLocal.gName = item.gName
                        GroupLocal.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.update(GroupLocal);
                    }else{
                        item.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.insert(item);
                    }
                    groupEntityList.add(item)
                }
                runOnUiThread {
                    GroupAdapter!!.setNewData(groupEntityList)
                }
            }
            else ->
            {
                runOnUiThread {
                    toast(getString(R.string.Other_mistakes))
                }

            }
        }
    }

    @Inject
    internal lateinit var mPresenter: GroupChatsPresenter
    var groupEntityList = arrayListOf<GroupEntity>()
    var handleGroup: GroupEntity? = null
    var GroupAdapter : GroupAdapter? = null

    var groupList = arrayListOf<GroupEntity>()

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

    override fun initView() {
        setContentView(R.layout.activity_group_chats)
        GroupAdapter = GroupAdapter(groupEntityList)
        recyclerView.adapter = GroupAdapter
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) {
                    search_clear.setVisibility(View.VISIBLE)
                } else {
                    search_clear.setVisibility(View.INVISIBLE)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                KLog.i("afterTextChanged " + s.toString())
                if ("".equals(s.toString())) {
                    GroupAdapter!!.setNewData(groupEntityList)
                } else {
                    GroupAdapter!!.setNewData(searchListByName(s.toString().toLowerCase()))
                }
            }
        })
        search_clear.setOnClickListener(View.OnClickListener {
            query.getText().clear()
        })
    }

    fun searchListByName(name: String): MutableList<GroupEntity> {
        var retList = mutableListOf<GroupEntity>()
        groupEntityList.forEach {
            var fileName = String(RxEncodeTool.base64Decode(it.gName)).toLowerCase()
            if (fileName.contains(name)) {
                retList.add(it)
            }
        }
        return retList
    }

    override fun initData() {
        EventBus.getDefault().register(this)
        title.text = getString(R.string.group_chat)
        AppConfig.instance.messageReceiver?.groupListPullBack = this
        refreshLayout.setOnRefreshListener {
            pullGourpList()
            KLog.i("拉取群组列表")
        }
        pullGourpList()
      /*  GroupAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            handleGroup = GroupAdapter!!.getItem(position)
            val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_USER_ID, handleGroup!!.gId.toString())
            UserDataManger.currentGroupData = handleGroup
            startActivity(intent)
        }*/
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun FriendAvatarChange(AddGroupChange: AddGroupChange) {
        pullGourpList()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun fromChat(fromChat: FromChat) {
       finish()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun groupInfoChange(groupEntity: GroupEntity) {
        pullGourpList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun groupQuit(jGroupQuitRsp: JGroupQuitRsp) {
        pullGourpList()
    }

    fun pullGourpList()
    {
        if (refreshLayout != null)
            refreshLayout.isRefreshing = false
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupListPullReq(userId!!, ConstantValue.currentRouterId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
    }
    override fun setupActivityComponent() {
        DaggerGroupChatsComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .groupChatsModule(GroupChatsModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: GroupChatsContract.GroupChatsContractPresenter) {
        mPresenter = presenter as GroupChatsPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.create_group, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_chat -> {
                var list = arrayListOf<GroupEntity>()
                startActivityForResult(Intent(this@GroupChatsActivity, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), 0)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: ArrayList<GroupEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var intent = Intent(this@GroupChatsActivity, CreateGroupActivity::class.java)
                    intent.putExtra("personList", contactSelectedList)
                    startActivity(intent)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.groupListPullBack = null
        super.onDestroy()
    }
}