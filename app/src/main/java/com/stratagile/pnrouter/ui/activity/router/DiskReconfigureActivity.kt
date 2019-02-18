package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.FormatDiskReq
import com.stratagile.pnrouter.entity.GetDiskDetailInfoReq
import com.stratagile.pnrouter.entity.JFormatDiskRsp
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskReconfigureComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskReconfigureContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskReconfigureModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskReconfigurePresenter
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_disk_information.*
import kotlinx.android.synthetic.main.activity_reconfigure.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/02/18 17:31:04
 */

class DiskReconfigureActivity : BaseActivity(), DiskReconfigureContract.View, PNRouterServiceMessageReceiver.FormatDiskBack {
    override fun formatDiskReq(jFormatDiskRsp: JFormatDiskRsp) {
        /*if(jFormatDiskRsp.params.retCode == 0)
        {

        }else{

        }*/
    }


    @Inject
    internal lateinit var mPresenter: DiskReconfigurePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_reconfigure)
    }
    override fun initData() {
        title.text = "Reconfigure"
        var mode = intent.getStringExtra("Mode")
        Confirm.setOnClickListener {
            if(!tipscheckbox.isChecked)
            {
                toast(R.string.erasechoose)
                return@setOnClickListener
            }else{
                var msgData = FormatDiskReq(mode)
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(3, msgData))
                } else if (ConstantValue.isToxConnected) {
                    var baseData = BaseData(3, msgData)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    } else {
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerDiskReconfigureComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskReconfigureModule(DiskReconfigureModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskReconfigureContract.DiskReconfigureContractPresenter) {
            mPresenter = presenter as DiskReconfigurePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}