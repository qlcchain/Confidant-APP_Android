package com.stratagile.pnrouter.ui.activity.user

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewUserComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewUserContract
import com.stratagile.pnrouter.ui.activity.user.module.NewUserModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewUserPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.message.UserProvider
import com.pawegio.kandroid.runOnUiThread
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.events.SetBadge
import com.stratagile.pnrouter.ui.adapter.user.NewFriendListAdapter
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.layout_fragment_recyclerview.*
import org.greenrobot.eventbus.EventBus

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/03/21 19:41:25
 */

class NewUserFragment : BaseFragment(), NewUserContract.View, UserProvider.AddFriendDelListener {

    override fun addFriendDealRsp(retCode: Int) {
        KLog.i("NewFriendActivity 收到好友处理的回调")
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(handleUser)

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)&& handleUser!!.userId.equals(j.friendId)) {
                j.friendLocalStatus = friendStatus
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
            }
        }
        runOnUiThread {
            closeProgressDialog()
            initData()
        }
    }

    @Inject
    lateinit internal var mPresenter: NewUserPresenter

    var newFriendListAdapter: NewFriendListAdapter? = null
    var handleUser: UserEntity? = null
    var friendStatus = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        UserProvider.getInstance().addFriendDelListener = this
        var view = inflater.inflate(R.layout.layout_fragment_recyclerview, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun setupFragmentComponent() {
        DaggerNewUserComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .newUserModule(NewUserModule(this))
                .build()
                .inject(this)
    }

    fun initData() {
        EventBus.getDefault().post(SetBadge())
        //val transactionVpnRecordList = transactionRecordDao.queryBuilder().where(TransactionRecordDao.Properties.AssetName.eq(AppConfig.currentUseVpn.getVpnName()), TransactionRecordDao.Properties.IsMainNet.eq(isMainNet)).list()
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var userIDStr:String = "";
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in list) {
            if (userIDStr.indexOf(i.userId+",") > -1) {
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
                continue
            }
            userIDStr = i.userId+","
        }
        list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var showlist = arrayListOf<UserEntity>()
        /* for (i in list) {
             if (i.routerUserId !=null && i.routerUserId.equals(selfUserId)) {
                 if (i.friendStatus != 7) {
                     showlist.add(i)
                 }
             }

         }*/
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                var hasSame = false
                for (i in showlist) {
                    if (i.userId.equals(j.friendId)) {
                        hasSame = true
                        KLog.i("同一个好友至少有两条好友状态 " + j.friendId)
                        LogUtil.addLog("同一个好友至少有两条好友状态 " + j.friendId)
                    }
                }
                if (j.friendLocalStatus != 7 && !hasSame) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(j.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    showlist.add(it)
                }
            }
        }
        newFriendListAdapter = NewFriendListAdapter(showlist)
        newFriendListAdapter?.setOnItemClickListener { adapter, view, position ->
            handleUser = newFriendListAdapter!!.getItem(position)
            var intent = Intent(activity!!, UserInfoActivity::class.java)
            intent.putExtra("user", newFriendListAdapter?.getItem(position))
            startActivityForResult(intent, 1)
        }
        newFriendListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tvAccept -> {
                    handleUser = newFriendListAdapter!!.getItem(position)
                    var nickName = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
//                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 0)
                    friendStatus = 0

//                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
                    if(newFriendListAdapter!!.getItem(position)!!.signPublicKey != null)
                    {
                        UserProvider.getInstance().accepteAddFriend(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, newFriendListAdapter!!.getItem(position)!!.signPublicKey)
                        showProgressDialog()
                    }
                }
                R.id.tvRefuse -> {
                    handleUser = newFriendListAdapter!!.getItem(position)
                    var nickName = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    UserProvider.getInstance().refuseAddFriend(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, "")
//                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 1)
                    friendStatus = 5
//                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
//                    UserProvider.getInstance().refreshFriend()
                    showProgressDialog()
                }
            }
        }
        recyclerView.adapter = newFriendListAdapter
    }

    override fun setPresenter(presenter: NewUserContract.NewUserContractPresenter) {
        mPresenter = presenter as NewUserPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun onDestroy() {
        UserProvider.getInstance().addFriendDelListener = null
        super.onDestroy()

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}