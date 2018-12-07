package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
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
import im.tox.tox4j.core.enums.ToxMessageType
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
        newFriend!!.friendStatus = 1
        if (hasUserInfo) {
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(newFriend)
        } else {
            var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
            newFriend!!.routerUserId = selfUserId
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)
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
    override fun initView() {
        setContentView(R.layout.activity_add_freind)
    }
    override fun initData() {
        newFriend = UserEntity()
        newFriend!!.nickName = ""
        newFriend!!.friendStatus = 1
        newFriend!!.userId = intent.getStringExtra("toUserId")
        newFriend!!.addFromMe = true
        newFriend!!.timestamp = Calendar.getInstance().timeInMillis
        newFriend!!.noteName = ""
        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
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
        }
        AppConfig.instance.messageReceiver!!.addfrendCallBack = this
        userId.text = intent.getStringExtra("toUserId")
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        bbtAdd.setOnClickListener {
            val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
            var login = AddFriendReq( selfUserId!!, strBase64!!, intent.getStringExtra("toUserId"),ConstantValue.publicRAS,"")
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
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