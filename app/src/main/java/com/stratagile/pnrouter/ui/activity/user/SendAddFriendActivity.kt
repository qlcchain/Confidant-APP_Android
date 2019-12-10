package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import com.message.UserProvider
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.AddFriendReq
import com.stratagile.pnrouter.entity.AddFriendReq2
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.ui.activity.user.component.DaggerSendAddFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.SendAddFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.SendAddFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.SendAddFriendPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_sendaddfriend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

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
                var freindStatusData = FriendEntity()
                freindStatusData.friendLocalStatus = 7
                val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(userId), FriendEntityDao.Properties.FriendId.eq(userEntity!!.userId)).list()
                if (localFriendStatusList.size > 0)
                {
                    freindStatusData = localFriendStatusList[0]
                    freindStatusData.friendLocalStatus = 1
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(freindStatusData)
                }
                runOnUiThread {
                    closeProgressDialog()
                    toast(getString(R.string.success))
                }

                finish()
            } else if (retCode == 2){
                runOnUiThread {
                    closeProgressDialog()
                    toast(getString(R.string.Alreadyagoodfriend))
                }
                finish()
            }else{
                runOnUiThread {
                    closeProgressDialog()
                    toast(getString(R.string.fail))
                }
                finish()
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
    var typeData = "";

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
        typeData= intent.getStringExtra("typeData")
        sendNickName.setText(String(RxEncodeTool.base64Decode(userEntity.nickName)))
        EventBus.getDefault().register(this)
        sendRequestBtn.setOnClickListener {
            if (sendNickName.text.trim().toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if(validation.text.toString().trim().length >35)
            {
                toast(getString(R.string.needs35))
                return@setOnClickListener
            }
            showProgressDialog("waiting...")
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            var msg= RxEncodeTool.base64Encode2String(validation.text.toString().trim().toByteArray())

            val strBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())

            var sendData = BaseData();
            if(typeData.equals("type_0"))
            {
                var addFriendReq = AddFriendReq( selfUserId!!, strBase64, userEntity.userId,ConstantValue.publicRAS!!,msg)
                sendData = BaseData(4,addFriendReq);
            }else{
                var addFriendReq2 = AddFriendReq2( selfUserId!!, strBase64,userEntity.signPublicKey!!,"",userEntity.routeId,msg)
                sendData = BaseData(6,addFriendReq2);
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            }else if (ConstantValue.isToxConnected) {
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
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
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
