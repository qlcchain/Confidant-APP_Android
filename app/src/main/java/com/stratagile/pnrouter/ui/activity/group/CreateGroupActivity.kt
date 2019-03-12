package com.stratagile.pnrouter.ui.activity.group

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.ui.activity.group.component.DaggerCreateGroupComponent
import com.stratagile.pnrouter.ui.activity.group.contract.CreateGroupContract
import com.stratagile.pnrouter.ui.activity.group.module.CreateGroupModule
import com.stratagile.pnrouter.ui.activity.group.presenter.CreateGroupPresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberAdapter
import com.stratagile.pnrouter.ui.adapter.group.GroupMemberDecoration
import kotlinx.android.synthetic.main.activity_create_group.*

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
        addUser.userId = "1"
        userList.add(addUser)
        userList.add(addUser)
        userList.add(addUser)
        userList.add(addUser)
        userList.add(addUser)
        userList.add(addUser)
        userList.add(addUser)
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
                startActivity(Intent(this@CreateGroupActivity, SelectFriendCreateGroupActivity::class.java))
            }
        }
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