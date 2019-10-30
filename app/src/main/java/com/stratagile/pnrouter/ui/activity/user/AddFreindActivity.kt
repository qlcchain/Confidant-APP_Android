package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.AddFriendReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JAddFreindRsp
import com.stratagile.pnrouter.ui.activity.user.component.DaggerAddFreindComponent
import com.stratagile.pnrouter.ui.activity.user.contract.AddFreindContract
import com.stratagile.pnrouter.ui.activity.user.module.AddFreindModule
import com.stratagile.pnrouter.ui.activity.user.presenter.AddFreindPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_add_freind.*
import java.util.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 17:42:11
 */

class AddFreindActivity : BaseActivity(), AddFreindContract.View, PNRouterServiceMessageReceiver.AddfrendCallBack {
    override fun addFriendBack(addFriendRsp: JAddFreindRsp) {
        com.pawegio.kandroid.runOnUiThread {
            toast(addFriendRsp.baseDataToJson())
        }
        //newFriend!!.friendStatus = 1
        newFriendStatus!!.friendLocalStatus = 1

        if (hasUserInfo) {
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(newFriend)
            AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(newFriendStatus)
        } else {
            var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
            newFriend!!.routerUserId = selfUserId
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)
            AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
        }
        KLog.i(addFriendRsp.baseDataToJson())
    }

    var hasUserInfo = false
    @Inject
    internal lateinit var mPresenter: AddFreindPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var newFriend : UserEntity? = null
    var newFriendStatus : FriendEntity? = null
    override fun initView() {
        setContentView(R.layout.activity_add_freind)
    }
    override fun initData() {
        newFriend = UserEntity()
        newFriend!!.nickName = ""
        //newFriend!!.friendStatus = 1
        newFriend!!.userId = intent.getStringExtra("toUserId")
        newFriend!!.addFromMe = true
        newFriend!!.timestamp = Calendar.getInstance().timeInMillis
        newFriend!!.noteName = ""

        var selfId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        newFriendStatus = FriendEntity()
        newFriendStatus!!.userId = selfId;
        newFriendStatus!!.friendId = intent.getStringExtra("toUserId")
        newFriendStatus!!.friendLocalStatus = 1
        newFriendStatus!!.timestamp = Calendar.getInstance().timeInMillis

       /* var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (i.userId.equals(intent.getStringExtra("toUserId"))) {
                hasUserInfo = true
                if (i.friendStatus != 0) {
                    newFriend = i
                } else {
                    var intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("user", i)
                    startActivity(intent)
                    finish()
                    return
                }
                break
            }
        }*/
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(selfId) && intent.getStringExtra("toUserId").equals(j.friendId)) {
                hasUserInfo = true
                if (j.friendLocalStatus == 0) {
                    newFriendStatus = j
                } else {
                    var intent = Intent(this, UserInfoActivity::class.java)
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(j.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    intent.putExtra("user", it)
                    startActivity(intent)
                    finish()
                    return
                }
            }
        }

        AppConfig.instance.messageReceiver!!.addfrendCallBack = this
        userId.text = intent.getStringExtra("toUserId")
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        bbtAdd.setOnClickListener {
            val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
            var addFriendReq = AddFriendReq( selfUserId!!, strBase64!!, intent.getStringExtra("toUserId"),ConstantValue.publicRAS!!,"")
            var sendData = BaseData(addFriendReq);
            if(ConstantValue.encryptionType.equals( "1"))
            {
                addFriendReq = AddFriendReq( selfUserId!!, strBase64!!, intent.getStringExtra("toUserId"),ConstantValue.libsodiumpublicSignKey!!,"")
                sendData = BaseData(4,addFriendReq);
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            } else if (ConstantValue.isToxConnected) {
                var baseData = sendData
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.addfrendCallBack = null
    }

    override fun setupActivityComponent() {
       DaggerAddFreindComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .addFreindModule(AddFreindModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: AddFreindContract.AddFreindContractPresenter) {
            mPresenter = presenter as AddFreindPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}