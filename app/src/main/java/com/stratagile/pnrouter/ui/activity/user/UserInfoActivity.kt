package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.easeui.EaseConstant
import com.message.UserProvider
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.router.UserQRCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerUserInfoComponent
import com.stratagile.pnrouter.ui.activity.user.contract.UserInfoContract
import com.stratagile.pnrouter.ui.activity.user.module.UserInfoModule
import com.stratagile.pnrouter.ui.activity.user.presenter.UserInfoPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.EditBoxAlertDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.pnrouter.view.TempRouterAlertDialog
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 22:03:00
 */

class UserInfoActivity : BaseActivity(), UserInfoContract.View, UserProvider.FriendOperateListener {
    override fun delFriendRsp(retCode: Int) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var delFriendCmdRsp = DelFriendCmdRsp(0,userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendCmdRsp))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendCmdRsp)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

        runOnUiThread {
            //            toast(addFriendRsp.baseDataToJson())
            closeProgressDialog()
            initData()
            finish()

        }
    }

    override fun accepteFriendRsp(retCode: Int) {
        runOnUiThread {
            //            toast(addFriendRsp.baseDataToJson())
            closeProgressDialog()
            initData()
            finish()

        }
    }

    override fun refuseFriendRsp(retCode: Int) {
        runOnUiThread {
            //            toast(addFriendRsp.baseDataToJson())
            closeProgressDialog()
            initData()
            finish()

        }
    }

    override fun addFriendRsp(retCode: Int) {
        runOnUiThread {
            if (retCode == 0) {
                standaloneCoroutine.cancel()
                userInfo!!.friendStatus = 1
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
                closeProgressDialog()
                initData()
                finish()
            } else {
                toast(getString(R.string.fail))
            }

        }
    }
    override fun changeRemarksRsp(retCode: Int) {
        runOnUiThread {
            if (retCode == 0) {
                userInfo!!.remarks = remark
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
                closeProgressDialog()
                if(userInfo!!.remarks != null && !userInfo!!.remarks.equals("")) {
                    var remarks = String(RxEncodeTool.base64Decode(userInfo!!.remarks))
                    setNoteName.setRightTitleText(getString(R.string.Modify_Nickname))
                    setNoteName.setTitleText(remarks)
                } else {
                    setNoteName.setRightTitleText("")
                    setNoteName.setTitleText(getString(R.string.Add_Nickname))
                }
                toast(getString(R.string.success))
            } else {
                toast(getString(R.string.fail))
            }

        }
    }

//    override fun addFriendBack(addFriendRsp: JAddFreindRsp) {
//        standaloneCoroutine.cancel()
//        userInfo!!.friendStatus = 1
//        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
//        KLog.i(addFriendRsp.baseDataToJson())
//        runOnUiThread {
////            toast(addFriendRsp.baseDataToJson())
//            closeProgressDialog()
//            initData()
//            finish()
//
//        }
//    }
//
//    override fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp) {
//        if (jDelFriendCmdRsp.params.retCode == 0) {
//            userInfo!!.friendStatus = 6
//            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(userInfo)
//            EventBus.getDefault().post(FriendChange(userInfo!!.userId))
//        }
//        runOnUiThread {
//            closeProgressDialog()
//            finish()
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendRelationshhipChange(friendChange : FriendChange) {
        var friendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in friendList) {
            if (userInfo!!.userId.equals(i.userId)) {
                userInfo == i
                initData()
                closeProgressDialog()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
//        AppConfig.instance.messageReceiver!!.addfrendCallBack = null
//        AppConfig.instance.messageReceiver!!.delFriendCallBack = null
        UserProvider.getInstance().friendOperateListener = null
    }

    @Inject
    internal lateinit var mPresenter: UserInfoPresenter

    var userInfo : UserEntity? = null

    var opreateBack = false

    var remark = ""
    lateinit var standaloneCoroutine : Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_user_info)
        EventBus.getDefault().register(this)
//        AppConfig.instance.messageReceiver!!.delFriendCallBack = this
//        AppConfig.instance.messageReceiver!!.addfrendCallBack = this
        UserProvider.getInstance().friendOperateListener = this
        userInfo = intent.getParcelableExtra("user")
        if(userInfo != null  && userInfo!!.userId!=null)
        {
            val user = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(userInfo!!.userId)).list()
            if (user.size != 0) {
                userInfo!!.remarks = user.get(0).remarks
            }
        }

    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressNoCanelDialog("network reconnecting...")
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressNoCanelDialog("network reconnecting...")
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun initData() {
        var nickNameSouce = ""
        if(userInfo!!.nickName != null && !userInfo!!.nickName.equals("")) {
            nickNameSouce = String(RxEncodeTool.base64Decode(userInfo!!.nickName))
            title.text = nickNameSouce
        } else {
            title.text = getString(R.string.details)
        }
        if(userInfo!!.remarks != null && !userInfo!!.remarks.equals("")) {
            var remarks = String(RxEncodeTool.base64Decode(userInfo!!.remarks))
            setNoteName.setRightTitleText(getString(R.string.Modify_Nickname))
            setNoteName.setTitleText(remarks)
        } else {
            setNoteName.setRightTitleText("")
            setNoteName.setTitleText(getString(R.string.Add_Nickname))
        }
        nickName.text = nickNameSouce
        avatar.setText(nickNameSouce)
        tvRefuse.setOnClickListener {
            if (userInfo!!.friendStatus == 0) {
                showDialog()
            } else if (userInfo!!.friendStatus == 3) {
                refuseFriend()
            }
        }
        setNoteName.setOnClickListener {

            var remarks = String(RxEncodeTool.base64Decode(userInfo!!.remarks))
            EditBoxAlertDialog(this, EditBoxAlertDialog.BUTTON_NEUTRAL)
                    .setContentText(remarks)
                    .setConfirmClickListener {

                        var content = it;
                        remark = RxEncodeTool.base64Encode2String(content!!.toString().toByteArray())
                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var msgData = ChangeRemarksReq(userId!!, userInfo!!.userId,remark)
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                        }else if (ConstantValue.isToxConnected) {
                            var baseData = BaseData(2,msgData)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }
                        showProgressDialog("wait...")
                    }
                    .show()
        }
        SpUtil.putString(this, ConstantValue.userFriendname, String(RxEncodeTool.base64Decode(userInfo!!.nickName)))
        SpUtil.putString(this, ConstantValue.userFriendId,userInfo!!.userId)
        shareAppFreind.setOnClickListener {
            startActivity(Intent(this, QRFriendCodeActivity::class.java))
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

        company.setOnClickListener {
            //startActivity(Intent(this@UserInfoActivity, ConversationActivity::class.java).putExtra("user", userInfo))
        }


        when (userInfo!!.friendStatus) {
            //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方, 7 什么都不是，等待发起加好友
            0-> {
                tvRefuse.text = getString(R.string.delete_contact)
                tvAccept.text = getString(R.string.send_a_message)
            }
            1-> {
                llOperate.visibility = View.GONE
                tvAddFriend.visibility = View.VISIBLE
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

    fun showDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setContentText(getString(R.string.delete_contact_text))
                .setConfirmClickListener {
                    showProgressDialog()
                    closeProgressDialog()
                    deleteFriend()
                }
                .show()

    }

    /**
     * 删除好友
     */
    fun deleteFriend() {
        var userId = SpUtil.getString(this, ConstantValue.userId, "")
//        var delFriendCmdReq = DelFriendCmdReq(userId!!, userInfo!!.userId)
        UserProvider.getInstance().deleteFriend(userId!!, userInfo!!.userId)
//        AppConfig.instance.messageSender!!.send(BaseData(delFriendCmdReq))
        showProgressDialog()
    }

    /**
     * 同意添加好友
     */
    fun acceptFriend() {
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        var userId = SpUtil.getString(this, ConstantValue.userId, "")
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(userInfo!!.nickName!!.toByteArray())
        if(userInfo!!.publicKey != null)
        {
            var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, userInfo!!.nickName!!, userId!!, userInfo!!.userId, ConstantValue.publicRAS!!, userInfo!!.publicKey,0)
            userInfo?.friendStatus = 0
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(addFriendDealReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }

            showProgressDialog()
        }

    }

    fun refuseFriend() {
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        var userId = SpUtil.getString(this, ConstantValue.userId, "")
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(userInfo!!.nickName!!.toByteArray())
        if(userInfo!!.publicKey != null)
        {
            var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, userInfo!!.nickName!!, userId!!, userInfo!!.userId,ConstantValue.publicRAS!!, userInfo!!.publicKey, 1)
            userInfo?.friendStatus = 1
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(addFriendDealReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }

            showProgressDialog()
        }
    }

    fun addFriend() {
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        UserProvider.getInstance().addFriend(selfUserId!!, nickName!!, userInfo!!.userId)
//        var login = AddFriendReq( selfUserId!!, nickName!!, userInfo!!.userId)
//        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
        showProgressDialog()
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
            if (!opreateBack) {
                runOnUiThread {
                    closeProgressDialog()
                    toast("time out")
                }
            }
        }
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