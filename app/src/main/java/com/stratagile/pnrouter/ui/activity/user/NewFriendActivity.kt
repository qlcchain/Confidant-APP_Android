package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import com.message.UserProvider
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.NewFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewFriendPresenter
import com.stratagile.pnrouter.ui.adapter.user.NewFriendListAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.fragment_contact.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 21:25:01
 */

class NewFriendActivity : BaseActivity(), NewFriendContract.View, UserProvider.AddFriendDelListener {

    override fun addFriendDealRsp(retCode: Int) {
        KLog.i("NewFriendActivity 收到好友处理的回调")
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(handleUser)
        runOnUiThread {
            closeProgressDialog()
            initData()
        }
    }


    @Inject
    internal lateinit var mPresenter: NewFriendPresenter

    var newFriendListAdapter: NewFriendListAdapter? = null

    var handleUser: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        UserProvider.getInstance().addFriendDelListener = null
    }

    override fun initView() {
        setContentView(R.layout.activity_new_friend)
        EventBus.getDefault().register(this)
        UserProvider.getInstance().addFriendDelListener = this
    }

    override fun initData() {
        title.text = getString(R.string.add_contact)
        //val transactionVpnRecordList = transactionRecordDao.queryBuilder().where(TransactionRecordDao.Properties.AssetName.eq(AppConfig.currentUseVpn.getVpnName()), TransactionRecordDao.Properties.IsMainNet.eq(isMainNet)).list()
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var userIDStr:String = "";
        for (i in list) {
            if (userIDStr.indexOf(i.userId+",") > -1) {
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
                continue
            }
            userIDStr = i.userId+","
        }
        list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var showlist = arrayListOf<UserEntity>()
        for (i in list) {
            if (i.friendStatus != 7) {
                showlist.add(i)
            }
        }
        newFriendListAdapter = NewFriendListAdapter(showlist)
        newFriendListAdapter?.setOnItemClickListener { adapter, view, position ->
            handleUser = newFriendListAdapter!!.getItem(position)
            var intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("user", newFriendListAdapter?.getItem(position))
            startActivityForResult(intent, 1)
        }
        newFriendListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tvAccept -> {
                    handleUser = newFriendListAdapter!!.getItem(position)
                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
                    var userId = SpUtil.getString(this, ConstantValue.userId, "")
//                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 0)
                    handleUser?.friendStatus = 0

//                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
                    val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
                    UserProvider.getInstance().accepteAddFriend(strBase64!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId)
                    showProgressDialog()
                }
                R.id.tvRefuse -> {
                    handleUser = newFriendListAdapter!!.getItem(position)
                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
                    var userId = SpUtil.getString(this, ConstantValue.userId, "")
                    val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
                    UserProvider.getInstance().refuseAddFriend(strBase64!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId)
//                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 1)
                    handleUser?.friendStatus = 5
//                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
//                    UserProvider.getInstance().refreshFriend()
                    showProgressDialog()
                }
            }
        }
        recyclerView.adapter = newFriendListAdapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        initData()
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