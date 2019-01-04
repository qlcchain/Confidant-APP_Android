package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.message.UserProvider
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.R.id.sendRequestBtn

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.AddFriendReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.user.component.DaggerSendAddFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.SendAddFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.SendAddFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.SendAddFriendPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_sendaddfriend.*
import kotlinx.android.synthetic.main.fragment_my.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/01/02 11:19:43
 */

class SendAddFriendActivity : BaseActivity(), SendAddFriendContract.View, UserProvider.FriendOperateListener {
    override fun addFriendRsp(retCode: Int) {
        runOnUiThread {
            if (retCode == 0) {

                runOnUiThread {
                    closeProgressDialog()
                    toast(getString(R.string.success))
                }

                finish()
            } else {
                runOnUiThread {
                    toast(getString(R.string.fail))
                }

            }

        }
    }
    override fun delFriendRsp(retCode: Int) {

    }

    override fun accepteFriendRsp(retCode: Int) {

    }

    override fun refuseFriendRsp(retCode: Int) {

    }

    override fun changeRemarksRsp(retCode: Int) {

    }

    @Inject
    internal lateinit var mPresenter: SendAddFriendPresenter
    lateinit var userEntity: UserEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_sendaddfriend)
    }
    override fun initData() {
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        UserProvider.getInstance().friendOperateListener = this
        title.text = getString(R.string.FriendRequest)
        validation.setText("I'm "+ nickName)
        userEntity = intent.getParcelableExtra("user")
        sendNickName.setText(String(RxEncodeTool.base64Decode(userEntity.nickName)))
        EventBus.getDefault().register(this)
        sendRequestBtn.setOnClickListener {
            if (sendNickName.text.toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if(validation.text.toString().length >35)
            {
                toast(getString(R.string.needs35))
                return@setOnClickListener
            }
            showProgressDialog("waiting...")
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            var msg= RxEncodeTool.base64Encode2String(validation.text.toString().toByteArray())

            val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
            var login = AddFriendReq( selfUserId!!, strBase64, userEntity.userId,ConstantValue.publicRAS!!,msg)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
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
    override fun setupActivityComponent() {
       DaggerSendAddFriendComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sendAddFriendModule(SendAddFriendModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SendAddFriendContract.SendAddFriendContractPresenter) {
            mPresenter = presenter as SendAddFriendPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        UserProvider.getInstance().friendOperateListener = null
    }

    fun getText(buttonText: CharSequence?):String {
//        KLog.i(buttonText)
        val strings = buttonText.toString().toUpperCase().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringArrayList = Arrays.asList(*strings)
        val itTemp = stringArrayList.iterator()
        val realList = ArrayList<String>()
        while (itTemp.hasNext()) {
            val next = itTemp.next() as String
            if ("" != next) {
                realList.add(next)
            }
        }
        var showText = ""
        for (i in realList.indices) {
            if (i < 2) {
                showText += realList[i].substring(0, 1)
            }
        }
         return showText
    }
}
