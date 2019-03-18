package com.stratagile.pnrouter.ui.activity.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.pawegio.kandroid.e
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.group.component.DaggerCreateGroupComponent
import com.stratagile.pnrouter.ui.activity.group.contract.CreateGroupContract
import com.stratagile.pnrouter.ui.activity.group.module.CreateGroupModule
import com.stratagile.pnrouter.ui.activity.group.presenter.CreateGroupPresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberAdapter
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberDecoration
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_select_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/12 15:29:49
 */

class CreateGroupActivity : BaseActivity(), CreateGroupContract.View {

    @Inject
    internal lateinit var mPresenter: CreateGroupPresenter

    var groupMemberAdapter : GroupMemberAdapter? = null

    lateinit var addUser : UserEntity
    lateinit var reduceUser : UserEntity
    var userList = arrayListOf<UserEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_create_group)
        addUser = UserEntity()
        reduceUser = UserEntity()
        reduceUser.userId = "0"
        addUser.userId = "1"
        userList.add(addUser)
        groupMemberAdapter = GroupMemberAdapter(userList)
        recyclerView.adapter = groupMemberAdapter
        val gridLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.setNestedScrollingEnabled(false)
        gridLayoutManager.isSmoothScrollbarEnabled = true
        gridLayoutManager.isAutoMeasureEnabled = true
        recyclerView.setLayoutManager(gridLayoutManager)
        recyclerView.addItemDecoration(GroupMemberDecoration())
        groupMemberAdapter?.setOnItemClickListener { adapter, view, position ->
            if ("1".equals(groupMemberAdapter!!.data[position].userId)) {
                //添加好友
                var list = arrayListOf<UserEntity>()
                list.addAll(groupMemberAdapter!!.data)
                startActivityForResult(Intent(this@CreateGroupActivity, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), 0)
            } else if("0".equals(groupMemberAdapter!!.data[position].userId)) {
                var list = arrayListOf<UserEntity>()
                list.addAll(groupMemberAdapter!!.data)
                startActivityForResult(Intent(this@CreateGroupActivity, RemoveGroupMemberActivity::class.java).putParcelableArrayListExtra("person", list), 0)
            } else {

            }
        }
        if (getGroupPeople() == 0) {
            select_people_number.text = ""
        } else {
            select_people_number.text = "" + getGroupPeople() + " people"
        }
        createGroup.setOnClickListener{
            if(groupName.text.toString().equals(""))
            {
                toast(R.string.Name_cannot_be_empty)
                return@setOnClickListener
            }
            if(groupMemberAdapter!!.data.size <= 1)
            {
                toast(R.string.At_least_one_good_friend)
                return@setOnClickListener
            }
        }
    }

    fun getGroupPeople() : Int{
        var count = 0
        groupMemberAdapter!!.data.forEach {
            if ("0".equals(it.userId) || "1".equals(it.userId)) {

            } else {
                count++
            }
        }
        return count
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: ArrayList<UserEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    contactSelectedList.add(addUser)
                    contactSelectedList.add(reduceUser)
                    groupMemberAdapter?.setNewData(contactSelectedList)
                    recyclerView.smoothScrollToPosition(groupMemberAdapter!!.data.size)
                }
            }
        }
        if (getGroupPeople() == 0) {
            select_people_number.text = ""
        } else {
            select_people_number.text = "" + getGroupPeople() + " people"
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun initData() {
        title.text = "Create a Group"
    }

    override fun setupActivityComponent() {
       DaggerCreateGroupComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .createGroupModule(CreateGroupModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: CreateGroupContract.CreateGroupContractPresenter) {
            mPresenter = presenter as CreateGroupPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}