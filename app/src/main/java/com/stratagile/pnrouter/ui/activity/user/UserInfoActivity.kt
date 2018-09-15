package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.socks.library.KLog
import com.hyphenate.easeui.EaseConstant
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerUserInfoComponent
import com.stratagile.pnrouter.ui.activity.user.contract.UserInfoContract
import com.stratagile.pnrouter.ui.activity.user.module.UserInfoModule
import com.stratagile.pnrouter.ui.activity.user.presenter.UserInfoPresenter
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_user_info.*
import org.greenrobot.eventbus.EventBus

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 22:03:00
 */

class UserInfoActivity : BaseActivity(), UserInfoContract.View, PNRouterServiceMessageReceiver.DelFriendCallBack, PNRouterServiceMessageReceiver.AddfrendCallBack {

    override fun addFriendBack(addFriendRsp: JAddFreindRsp) {
        userInfo!!.friendStatus = 1
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
        KLog.i(addFriendRsp.baseDataToJson())
        runOnUiThread {
//            toast(addFriendRsp.baseDataToJson())
            closeProgressDialog()
            initData()

        }
    }

    override fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp) {
        if (jDelFriendCmdRsp.params.retCode == 0) {
            userInfo!!.friendStatus = 6
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
            EventBus.getDefault().post(FriendChange())
        }
        runOnUiThread {
            closeProgressDialog()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.addfrendCallBack = null
        AppConfig.instance.messageReceiver!!.delFriendCallBack = null
    }

    @Inject
    internal lateinit var mPresenter: UserInfoPresenter

    var userInfo : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_user_info)
        AppConfig.instance.messageReceiver!!.delFriendCallBack = this
        AppConfig.instance.messageReceiver!!.addfrendCallBack = this
        userInfo = intent.getParcelableExtra("user")
    }
    override fun initData() {
        title.text = getString(R.string.adam_graspn)
        nickName.text = userInfo!!.nickName
        avatar.setText(userInfo!!.nickName)
        tvRefuse.setOnClickListener {
            if (userInfo!!.friendStatus == 0) {
                deleteFriend()
            } else if (userInfo!!.friendStatus == 3) {
                refuseFriend()
            }
        }

        tvAccept.setOnClickListener {
            if (userInfo!!.friendStatus == 0) {
                //send message
                /*var intent = Intent(this, ConversationActivity::class.java)
                intent.putExtra("user", userInfo!!)
                startActivity(intent)*/
                UserDataManger.curreantfriendUserData = userInfo
                startActivity(Intent(this@UserInfoActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userInfo?.userId))
            } else if (userInfo!!.friendStatus == 3) {
                acceptFriend()
            }
        }
        tvAddFriend.setOnClickListener {
            addFriend()
        }


        when (userInfo!!.friendStatus) {
            //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方, 7 什么都不是，等待发起加好友
            0-> {
                tvRefuse.text = getString(R.string.delete_contact)
                tvAccept.text = getString(R.string.send_a_message)
            }
            1-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.GONE
            }
            2-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
            }
            3-> {
                tvRefuse.text = getString(R.string.refuse)
                tvAccept.text = getString(R.string.accept)
            }
            4-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
            }
            5-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
            }
            6-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
            }
            7-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
            }
        }

    }

    /**
     * 删除好友
     */
    fun deleteFriend() {
        var userId = SpUtil.getString(this, ConstantValue.userId, "")
        var delFriendCmdReq = DelFriendCmdReq(userId!!, userInfo!!.userId)
        AppConfig.instance.messageSender!!.send(BaseData(delFriendCmdReq))
        showProgressDialog()
    }

    /**
     * 同意添加好友
     */
    fun acceptFriend() {

    }

    fun refuseFriend() {

    }

    fun addFriend() {
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        var login = AddFriendReq( selfUserId!!, nickName!!, userInfo!!.userId)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
        showProgressDialog()
    }

    override fun setupActivityComponent() {
       DaggerUserInfoComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .userInfoModule(UserInfoModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: UserInfoContract.UserInfoContractPresenter) {
            mPresenter = presenter as UserInfoPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}