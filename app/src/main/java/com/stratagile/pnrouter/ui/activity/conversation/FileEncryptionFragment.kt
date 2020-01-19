package com.stratagile.pnrouter.ui.activity.conversation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerFileEncryptionComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileEncryptionContract
import com.stratagile.pnrouter.ui.activity.conversation.module.FileEncryptionModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileEncryptionPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.google.common.reflect.Reflection.getPackageName
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.runOnUiThread
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BakAddrUserNumReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JBakAddrUserNumRsp
import com.stratagile.pnrouter.entity.JBakFileRsp
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.events.ForegroundCallBack
import com.stratagile.pnrouter.ui.activity.encryption.ContactsEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.WeiXinEncryptionActivity
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.ImportVCFUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.fragment_file_encryption.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2019/11/20 10:12:15
 */

class FileEncryptionFragment : BaseFragment(), FileEncryptionContract.View , PNRouterServiceMessageReceiver.BakAddrUserNumOutCallback{
    override fun getScanPermissionSuccess() {
        //var count = FileUtil.getContantsCount(this@FileEncryptionFragment.context)
        var count = FileUtil.getContactCount(this@FileEncryptionFragment.context)
        runOnUiThread {
            localContacts.text = count.toString();
        }
        var msgCount = FileUtil.getAllSmsCount(this@FileEncryptionFragment.context)
        runOnUiThread {
            localMessags.text = msgCount.toString();
        }
        var aa = FileUtil.getAllSms(this@FileEncryptionFragment.context)
        var bb = aa;
    }

    override fun bakAddrUserNum(jBakAddrUserNumRsp: JBakAddrUserNumRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        if(jBakAddrUserNumRsp.params.retCode == 0)
        {
            runOnUiThread {
                nodeContacts.text = jBakAddrUserNumRsp.params.num.toString();
            }
        }else{

        }
    }

    @Inject
    lateinit internal var mPresenter: FileEncryptionPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file_encryption, null);

        return view
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser)
        {
            mPresenter.getScanPermission()
            getNodeData()
        }
    }
    fun getNodeData()
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var filesListPullReq = BakAddrUserNumReq( selfUserId!!, 0)
        var sendData = BaseData(6, filesListPullReq);
        showProgressDialog();
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
    override fun setupFragmentComponent() {
        DaggerFileEncryptionComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileEncryptionModule(FileEncryptionModule(this))
                .build()
                .inject(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        AppConfig.instance.messageReceiver?.bakAddrUserNumOutCallback = this
        albumMenuRoot.setOnClickListener {
            var intent =  Intent(activity!!, PicEncryptionActivity::class.java)
            startActivity(intent);
        }
        wechatMenu.setOnClickListener {
            var intent =  Intent(activity!!, WeiXinEncryptionActivity::class.java)
            startActivity(intent);
        }
        contactsParent.setOnClickListener {
            var intent =  Intent(activity!!, ContactsEncryptionActivity::class.java)
            startActivity(intent);
        }
        messagesParent.setOnClickListener {
            var localIntent = Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", "com.stratagile.pnrouter", null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.stratagile.pnrouter", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName","com.stratagile.pnrouter");
            }
            startActivity(localIntent);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(foregroundCallBack: ForegroundCallBack) {
        if(foregroundCallBack.isForeground)
        {
            mPresenter.getScanPermission()
        }
    }
    override fun setPresenter(presenter: FileEncryptionContract.FileEncryptionContractPresenter) {
        mPresenter = presenter as FileEncryptionPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.bakAddrUserNumOutCallback = null;
        super.onDestroy()
    }
}