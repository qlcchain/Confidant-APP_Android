package com.stratagile.pnrouter.ui.activity.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.JGroupUserPullRsp
import com.stratagile.pnrouter.entity.MyFriend
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.group.component.DaggerRemoveGroupDetailMemberComponent
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupDetailMemberContract
import com.stratagile.pnrouter.ui.activity.group.module.RemoveGroupDetailMemberModule
import com.stratagile.pnrouter.ui.activity.group.presenter.RemoveGroupDetailMemberPresenter
import com.stratagile.pnrouter.ui.adapter.user.ContactAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_remove_group_member.*
import kotlinx.android.synthetic.main.ease_search_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description 删除群成员，在群已经建成之后的情况
 * @date 2019/03/21 10:15:05
 */

class RemoveGroupDetailMemberActivity : BaseActivity(), RemoveGroupDetailMemberContract.View {

    @Inject
    internal lateinit var mPresenter: RemoveGroupDetailMemberPresenter
    var contactAdapter1: ContactAdapter? = null
    var allUserList = arrayListOf<MultiItemEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_remove_group_member)
        EventBus.getDefault().register(this)
        setToorBar(false)
        tvTitle.text = "Remove Group Members"
    }

    fun getAllSelectedFriend(): ArrayList<UserEntity> {
        var contactList = arrayListOf<UserEntity>()
        for (i in 0 until contactAdapter1!!.data.size) {
            if (contactAdapter1!!.data.get(i).getItemType() == 0) {
                val userHead = contactAdapter1!!.data.get(i) as UserHead
                if (userHead.subItems == null || userHead.subItems.size == 0) {
                    if (userHead.isChecked) {
                        contactList.add(userHead.userEntity)
                    }
                } else {
                    for (j in 0 until userHead.subItems.size) {
                        val userItem = userHead.subItems[j]
                        if (userItem.isChecked) {
                            contactList.add(userItem.userEntity)
                        }
                    }
                }
            }
        }
        return contactList!!
    }

    fun updateAdapterData(list: ArrayList<UserEntity>) {
        var contactMapList = HashMap<String, MyFriend>()
        for (i in list) {

            if (contactMapList.get(i.signPublicKey) == null) {
                var myFriend = MyFriend()
                myFriend.userKey = i.signPublicKey
                myFriend.userName = i.nickName
                myFriend.remarks = i.remarks
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
            it.userName
        }
        val list1 = arrayListOf<MultiItemEntity>()
        var isIn = false
        contactNewList.forEach {
            var userHead = UserHead()
            userHead.userName = it.userName
            userHead.remarks = it.remarks
            userHead.userEntity = it.userEntity
            userHead.isShowRouteName = false
            if (it.routerItemList.size > 1) {
                it.routerItemList?.forEach {
                    userHead.addSubItem(UserItem(it))
                }
            }
            list1.add(userHead)
        }
        contactAdapter1!!.setNewData(list1)
    }


    fun fiter(key: String, contactList: ArrayList<UserEntity>) {
        var contactListTemp: ArrayList<UserEntity> = arrayListOf<UserEntity>()
        for (i in contactList) {
            if (String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase().contains(key)) {
                contactListTemp.add(i)
            }
        }
        updateAdapterData(contactListTemp)
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
        var list = intent.getParcelableArrayListExtra<JGroupUserPullRsp.ParamsBean.PayloadBean>("person")
        var toAddList = arrayListOf<UserEntity>()
        list.forEach {
            if ("1".equals(it.toxId) || "0".equals(it.toxId) || SpUtil.getString(this, ConstantValue.userId, "").equals(it.toxId)) {

            } else {
                var user = UserEntity()
                user.userId = it.toxId
                user.signPublicKey = it.userKey
                user.nickName = it.nickname
                toAddList.add(user)
            }
        }

        val list1 = arrayListOf<MultiItemEntity>()
        var contactMapList = HashMap<String, MyFriend>()
        for (i in toAddList) {

            if (contactMapList.get(i.signPublicKey) == null) {
                var myFriend = MyFriend()
                myFriend.userKey = i.signPublicKey
                myFriend.userName = i.nickName
                myFriend.remarks = i.remarks
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
            userHead.remarks = it.remarks
            userHead.userEntity = it.userEntity
            if (it.routerItemList.size > 1) {
                it.routerItemList?.forEach {
                    userHead.addSubItem(UserItem(it))
                }
            }
            list1.add(userHead)
        }

        contactAdapter1 = ContactAdapter(list1)
        allUserList = list1
        contactAdapter1?.isCheckMode = true
        recyclerView.adapter = contactAdapter1!!

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
                    contactAdapter1!!.setNewData(allUserList)
                } else {
                    fiter(s.toString(), toAddList)
                }
            }
        })
        search_clear.setOnClickListener(View.OnClickListener {
            query.getText().clear()
        })
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
       DaggerRemoveGroupDetailMemberComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .removeGroupDetailMemberModule(RemoveGroupDetailMemberModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RemoveGroupDetailMemberContract.RemoveGroupDetailMemberContractPresenter) {
            mPresenter = presenter as RemoveGroupDetailMemberPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}