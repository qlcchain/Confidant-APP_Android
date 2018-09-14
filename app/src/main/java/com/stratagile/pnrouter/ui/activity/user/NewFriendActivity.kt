package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.AddFriendDealReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JAddFriendDealRsp
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.NewFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewFriendPresenter
import com.stratagile.pnrouter.ui.adapter.user.NewFriendListAdapter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.fragment_contact.*
import org.greenrobot.eventbus.EventBus

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 21:25:01
 */

class NewFriendActivity : BaseActivity(), NewFriendContract.View, PNRouterServiceMessageReceiver.AddFriendDealCallBack{

    override fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp) {
        if (jAddFriendDealRsp.params.retCode == 0) {
            handleUser?.friendStatus = 0
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(handleUser)
            EventBus.getDefault().post(FriendChange())
        }
        runOnUiThread {
            closeProgressDialog()
            initData()
        }
    }


    @Inject
    internal lateinit var mPresenter: NewFriendPresenter

    var newFriendListAdapter : NewFriendListAdapter? = null

    var handleUser : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.addFriendDealCallBack = null
    }

    override fun initView() {
        setContentView(R.layout.activity_new_friend)
        AppConfig.instance.messageReceiver!!.addFriendDealCallBack = this
    }
    override fun initData() {
        title.text = "New Friend"
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var showlist = arrayListOf<UserEntity>()
        for (i in list) {
//            if (!i.isFriend) {
//                showlist.add(i)
//            }
            showlist.add(i)
        }
        newFriendListAdapter = NewFriendListAdapter(showlist)
        newFriendListAdapter?.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("user", newFriendListAdapter?.getItem(position))
            startActivityForResult(intent, 1)
        }
        newFriendListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.tvAccept-> {
                    handleUser = newFriendListAdapter!!.getItem(position)
                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
                    var userId = SpUtil.getString(this, ConstantValue.userId, "")
                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 0)
                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
                    showProgressDialog()
                }
            }
        }
        recyclerView.adapter = newFriendListAdapter
    }

    override fun setupActivityComponent() {
       DaggerNewFriendComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .newFriendModule(NewFriendModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: NewFriendContract.NewFriendContractPresenter) {
            mPresenter = presenter as NewFriendPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}