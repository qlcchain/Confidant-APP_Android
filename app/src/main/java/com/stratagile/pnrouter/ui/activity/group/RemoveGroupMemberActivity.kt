package com.stratagile.pnrouter.ui.activity.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.MyFriend
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.group.component.DaggerRemoveGroupMemberComponent
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupMemberContract
import com.stratagile.pnrouter.ui.activity.group.module.RemoveGroupMemberModule
import com.stratagile.pnrouter.ui.activity.group.presenter.RemoveGroupMemberPresenter
import com.stratagile.pnrouter.ui.adapter.user.ContactAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_remove_group_member.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/14 10:20:11
 */

class RemoveGroupMemberActivity : BaseActivity(), RemoveGroupMemberContract.View {

    @Inject
    internal lateinit var mPresenter: RemoveGroupMemberPresenter
    var contactAdapter1: ContactAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_remove_group_member)
        setToorBar(false)
        tvTitle.text = "Remove Group Members"
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }

    fun getAllSelectedFriend(): ArrayList<UserEntity> {
        var contactList = arrayListOf<UserEntity>()
        for (i in 0 until contactAdapter1!!.data.size) {
            if (contactAdapter1!!.data.get(i).getItemType() == 0) {
                val userHead = contactAdapter1!!.data.get(i) as UserHead
                if (userHead.subItems == null || userHead.subItems.size == 0) {
                    if (!userHead.isChecked) {
                        contactList.add(userHead.userEntity)
                    }
                } else {
                    for (j in 0 until userHead.subItems.size) {
                        val userItem = userHead.subItems[j]
                        if (!userItem.isChecked) {
                            contactList.add(userItem.userEntity)
                        }
                    }
                }
            }
        }
        return contactList!!
    }
    override fun initData() {
        llCancel.setOnClickListener {
            onBackPressed()
        }
        send.setOnClickListener {
            var contactSelectedList: ArrayList<UserEntity> = getAllSelectedFriend()
            if (contactSelectedList.size == 0) {
                toast("can't remove all members")
            } else {
                var intent = Intent()
                intent.putParcelableArrayListExtra("person", contactSelectedList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        var list = intent.getParcelableArrayListExtra<UserEntity>("person")
        var toAddList = arrayListOf<UserEntity>()
        list.forEach {
            if ("1".equals(it.userId) || "0".equals(it.userId)) {

            } else {
                toAddList.add(it)
            }
        }

        val list1 = arrayListOf<MultiItemEntity>()
        var contactMapList = HashMap<String, MyFriend>()
        for (i in toAddList) {

            if (contactMapList.get(i.signPublicKey) == null) {
                var myFriend = MyFriend()
                myFriend.userKey = i.signPublicKey
                myFriend.userName = i.nickName
                myFriend.userEntity = i
                var temp = ArrayList<UserEntity>()
                temp.add(i)
                myFriend.routerItemList = temp;
                contactMapList.put(i.signPublicKey, myFriend)
            } else {
                var temp = contactMapList.get(i.signPublicKey)
                var contactNewList = temp!!.routerItemList
                contactNewList.add(i)
            }

        }

        var contactNewList = arrayListOf<MyFriend>()
        var contactNewListValues = contactMapList.values
        for (i in contactNewListValues) {
            contactNewList.add(i)
        }
        contactNewList.sortBy {
            String(RxEncodeTool.base64Decode(it.userName)).toLowerCase()
        }

        contactNewList.forEach {
            var userHead = UserHead()
            userHead.userName = it.userName
            userHead.userEntity = it.userEntity
            if (it.routerItemList.size > 1) {
                it.routerItemList?.forEach {
                    userHead.addSubItem(UserItem(it))
                }
            }
            list1.add(userHead)
        }

        contactAdapter1 = ContactAdapter(list1)
        contactAdapter1?.isCheckMode = true
        recyclerView.adapter = contactAdapter1!!
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectFriendChange(selectFriendChange: SelectFriendChange) {
        if (selectFriendChange.friendNum > 1) {
            selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.person) + "s"
        } else {
            selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.person)
        }
        if (selectFriendChange.friendNum == 0) {
            send.text = "Confirm"
        } else {
            send.text = "Confirm (" + selectFriendChange.friendNum + ")"
        }
    }

    override fun setupActivityComponent() {
       DaggerRemoveGroupMemberComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .removeGroupMemberModule(RemoveGroupMemberModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RemoveGroupMemberContract.RemoveGroupMemberContractPresenter) {
            mPresenter = presenter as RemoveGroupMemberPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}