package com.stratagile.pnrouter.ui.activity.selectfriend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.LinearLayout
import butterknife.ButterKnife
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.google.gson.Gson
import com.hyphenate.chat.*
import com.message.Message
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.AESCipher
import com.smailnet.eamil.Utils.AESToolsCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.main.ContactAndGroupFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerselectFriendSendFileComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendSendFileContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendSendFileModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendSendFilePresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_select_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: $description
 * @date 2019/03/06 15:41:57
 */

class selectFriendSendFileActivity : BaseActivity(), selectFriendSendFileContract.View, PNRouterServiceMessageReceiver.FileForwardBack {
    override fun fileForwardReq(jFileForwardRsp: JFileForwardRsp) {

        /* when(jFileForwardRsp.params.retCode)
         {
             0->
             {
                 runOnUiThread {
                     //toast(R.string.hasbeensent)
                     super.onBackPressed()
                 }
             }
             1->
             {
                 runOnUiThread {
                     //toast(R.string.User_ID_error)
                     super.onBackPressed()
                 }
             }
             2->
             {
                 runOnUiThread {
                     //toast(R.string.file_error)
                     super.onBackPressed()
                 }
             }
             3->
             {
                 runOnUiThread {
                     //toast(R.string.Goals_are_not_achievable)
                     super.onBackPressed()
                 }
             }
             4->
             {
                 runOnUiThread {
                     //toast(R.string.Other_mistakes)
                     super.onBackPressed()
                 }
             }

         }*/
    }

    @Inject
    internal lateinit var mPresenter: selectFriendSendFilePresenter
    var fragment: ContactAndGroupFragment? = null
    var  userEntity: UserEntity? = null
    var fromId:String? = null
    var msgId:Int? = null
    var fileName:String? = null
    var filePath:String? = null
    var fileKey:String? = null
    var fileType:Int? = null
    var fileInfo:String? = null
    var message: EMMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        ButterKnife.bind(this)
        setToorBar(false)
        fromId = intent.getStringExtra("fromId")
        msgId = intent.getIntExtra("msgId",0)
        fileName = intent.getStringExtra("fileName")
        filePath = intent.getStringExtra("filePath")
        fileInfo = intent.getStringExtra("fileInfo")
        fileKey = intent.getStringExtra("fileKey")
        fileType = intent.getIntExtra("fileType",0)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_select_friend)
        tvTitle.text = getString(R.string.Contacts)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }
    override fun initData() {
        fragment = ContactAndGroupFragment();
        val bundle = Bundle()
        EventBus.getDefault().register(this)
        bundle.putString(ConstantValue.selectFriend, "select")
        bundle.putString("fromId", fromId)
        bundle.putParcelable("message",message)
        AppConfig.instance.messageReceiver?.fileForwardBack = this
        fragment!!.setArguments(bundle)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragment!!
            }

            override fun getCount(): Int {
                return 1
            }
        }
        viewPager.offscreenPageLimit = 1
        llCancel.setOnClickListener {
            onBackPressed()
        }
        send.setOnClickListener {
            showSendDialog()
        }
        multiSelectBtn.setOnClickListener {

            fragment!!.selectOrCancelAll()
        }
        fragment!!.setRefreshEnable(false)
    }
    /**
     * 显示转发的弹窗
     */
    fun showSendDialog() {
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        val strBase58 = Base58.encode(fileName!!.toByteArray())
        var contactSelectedList: ArrayList<UserEntity> = fragment!!.getAllSelectedFriend()
        var groupSelectedList  = fragment!!.getAllSelectedGroup()
        if (contactSelectedList.size == 0 && groupSelectedList.size == 0) {
            toast(R.string.noSelected)
            return
        }
        var fileSouceKey = LibsodiumUtil.DecryptShareKey(fileKey!!,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
        for (i in contactSelectedList) {
            var FileKey = RxEncodeTool.base64Encode2String(LibsodiumUtil.EncryptShareKey(fileSouceKey+"0000000000000000", i.miPublicKey))
            if(fileInfo == null || fileInfo.equals(""))
            {
                fileInfo = ""
            }
            var fileForwardReq = FileForwardReq(msgId!!,userId!!, i.userId!!,filePath!!, strBase58,fileInfo!!, FileKey)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,fileForwardReq))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4,fileForwardReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            val gson = Gson()
            val Message = Message()
            Message.msgType = fileType!!
            Message.fileName = fileName
            Message.msg = ""
            Message.from = userId
            Message.to = i.userId
            Message.timeStamp = System.currentTimeMillis() / 1000
            Message.unReadCount = 0
            Message.chatType = EMMessage.ChatType.Chat
            val baseDataJson = gson.toJson(Message)
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.userId, baseDataJson)
        }
        for (i in groupSelectedList) {
            val aesKey = LibsodiumUtil.DecryptShareKey(i.userKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
            var FileKeyBase64 = RxEncodeTool.base64Encode2String(AESToolsCipher.aesEncryptBytes(fileSouceKey.toByteArray(), aesKey!!.toByteArray(charset("UTF-8"))));
            KLog.i("文件转发 发送："+FileKeyBase64)
            if(fileInfo == null || fileInfo.equals(""))
            {
                fileInfo = ""
            }
            var fileForwardReq = FileForwardReq(msgId!!,userId!!, i.gId!!,filePath!!, strBase58,fileInfo!!, FileKeyBase64)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,fileForwardReq))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4,fileForwardReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            val gson = Gson()
            val Message = Message()
            Message.msgType =  fileType!!
            Message.fileName = fileName
            Message.msg = ""
            Message.from = userId
            Message.to = i.gId
            Message.timeStamp = System.currentTimeMillis() / 1000
            Message.unReadCount = 0
            Message.chatType = EMMessage.ChatType.GroupChat
            val baseDataJson = gson.toJson(Message)
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.gId, baseDataJson)
        }
        finish()
    }
    override fun setupActivityComponent() {
        DaggerselectFriendSendFileComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectFriendSendFileModule(selectFriendSendFileModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: selectFriendSendFileContract.selectFriendSendFileContractPresenter) {
        mPresenter = presenter as selectFriendSendFilePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
            overridePendingTransition(0, R.anim.activity_translate_out_1)
        }
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
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.fileForwardBack = null
        super.onDestroy()
    }
}