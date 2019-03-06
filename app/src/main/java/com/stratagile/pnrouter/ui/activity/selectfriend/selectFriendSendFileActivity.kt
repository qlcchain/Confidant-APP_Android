package com.stratagile.pnrouter.ui.activity.selectfriend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.LinearLayout
import butterknife.ButterKnife
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.chat.*
import com.message.Message
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
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

    }

    @Inject
    internal lateinit var mPresenter: selectFriendSendFilePresenter
    var fragment: ContactFragment? = null
    var  userEntity: UserEntity? = null
    var fromId:String? = null
    var msgId:Int? = null
    var fileName:String? = null
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
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_select_friend)
        tvTitle.text = getString(R.string.Contacts)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }
    override fun initData() {
        fragment = ContactFragment();
        val bundle = Bundle()
        bundle.putString(ConstantValue.selectFriend, "select")
        bundle.putString("fromId", fromId)
        bundle.putParcelable("message",message)
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
        if (contactSelectedList.size == 0) {
            toast(R.string.noSelected)
        } else {
            for (i in contactSelectedList) {
                var fileKey =  RxEncryptTool.generateAESKey()
                var my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                var friend = RxEncodeTool.base64Decode(i.signPublicKey)
                var FileKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, UserDataManger.curreantfriendUserData.getMiPublicKey()));
                var fileForwardReq = FileForwardReq(msgId!!,userId!!, i.userId!!, strBase58, RxEncodeTool.base64Encode2String(FileKey))
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
            }
            toast(R.string.hasbeensent)
            super.onBackPressed()
        }
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
}