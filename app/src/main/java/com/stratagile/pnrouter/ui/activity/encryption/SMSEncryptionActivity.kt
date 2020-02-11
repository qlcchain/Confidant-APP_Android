package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.SMSEntityDao
import com.stratagile.pnrouter.entity.BakAddrUserNumReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.GetBakContentStatReq
import com.stratagile.pnrouter.entity.JGetBakContentStatRsp
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionPresenter
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.picencry_sms_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/17 14:47:42
 */

class SMSEncryptionActivity : BaseActivity(), SMSEncryptionContract.View , PNRouterServiceMessageReceiver.GetBakContentStatCallback {
    override fun getBakContentStatCallback(jGetBakContentStatRsp: JGetBakContentStatRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        if(jGetBakContentStatRsp.params.retCode == 0)
        {
            runOnUiThread {
                nodeContacts.text = jGetBakContentStatRsp.params.num.toString();
            }
        }else{

        }
    }

    override fun getScanPermissionSuccess() {
        var count = FileUtil.getAllSmsCount(this@SMSEncryptionActivity)
        runOnUiThread {
            localContacts.text = count.toString();
        }
        var smsDataList = FileUtil.getAllSms(this@SMSEncryptionActivity)
        for(item in smsDataList)
        {
            var list = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().where(SMSEntityDao.Properties.UUID.eq(item.uuid)).list()
            if(list == null || list!!.size == 0)
            {
                AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.insert(item)
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionPresenter
    var needRefresh = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        needRefresh = false;
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_sms_list)
    }
    override fun initData() {
        title.text = getString(R.string.SMS)
        AppConfig.instance.messageReceiver?.getBakContentStatCallback = this
        localRoot.setOnClickListener {
            startActivity(Intent(this, SMSEncryptionListActivity::class.java))
            needRefresh = true
        }
        nodeRoot.setOnClickListener {
            startActivity(Intent(this, SMSEncryptionNodelListActivity::class.java))
            needRefresh = true
        }
        mPresenter.getScanPermission()
        getNodeData()
    }
    fun getNodeData()
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var GetBakContentStatReq = GetBakContentStatReq( 1,selfUserId!!)
        var sendData2 = BaseData(6, GetBakContentStatReq);
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData2)
        }else if (ConstantValue.isToxConnected) {
            var baseData2 = sendData2
            var baseDataJson2 = baseData2.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson2, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if(needRefresh)
        {
            needRefresh = false;
            getNodeData()
        }
    }
    override fun setupActivityComponent() {
       DaggerSMSEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionModule(SMSEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionContract.SMSEncryptionContractPresenter) {
            mPresenter = presenter as SMSEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.getBakContentStatCallback = null
        super.onDestroy()
    }
    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}