package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.GetDiskDetailInfoReq
import com.stratagile.pnrouter.entity.JGetDiskDetailInfoRsp
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskInformationComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskInformationContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskInformationModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskInformationPresenter
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.activity_disk_information.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 15:21:12
 */

class DiskInformationActivity : BaseActivity(), DiskInformationContract.View , PNRouterServiceMessageReceiver.GetDiskDetailInfoBack{
    override fun getDiskDetailInfoReq(JGetDiskDetailInfoRsp: JGetDiskDetailInfoRsp) {
        if(JGetDiskDetailInfoRsp.params.retCode == 0)
        {
            runOnUiThread {
                subject.text = JGetDiskDetailInfoRsp.params.name
                when(JGetDiskDetailInfoRsp.params.status)
                {
                    0 ->{
                        status.text = getString(R.string.notfound)
                    }
                    1 ->{
                        status.text = getString(R.string.notconfigured)
                    }
                    2 ->{
                        status.text = getString(R.string.configured)
                    }
                }
                ATAVersion.text = JGetDiskDetailInfoRsp.params.ataVersion
                Device.text = JGetDiskDetailInfoRsp.params.device
                Firmware.text = JGetDiskDetailInfoRsp.params.firmware
                FormFactor.text = JGetDiskDetailInfoRsp.params.formFactor
                LUWWNDeviceId.text = JGetDiskDetailInfoRsp.params.luwwnDeviceId
                ModelFamily.text = JGetDiskDetailInfoRsp.params.modelFamily
                RotationRate.text = JGetDiskDetailInfoRsp.params.rotationRate
                SATAVersion.text = JGetDiskDetailInfoRsp.params.sataVersion
                SMARTsupport.text = JGetDiskDetailInfoRsp.params.smarTsupport
                Serial.text =  JGetDiskDetailInfoRsp.params.serial
                Capacity.text = JGetDiskDetailInfoRsp.params.capacity
                SectorSizes.text = JGetDiskDetailInfoRsp.params.sectorSizes
            }
        }else{
            runOnUiThread {
                toast(R.string.system_busy)
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: DiskInformationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_information)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.getDiskDetailInfoBack = this
        var Slot = intent.getIntExtra("Slot",0)
        when (Slot)
        {
            0 ->{
                title.text = "Disk A"
            }
            1 ->{
                title.text = "Disk B"
            }
            2 ->{
                title.text = "Disk C"
            }
        }

        configurate_disk.setOnClickListener {
            startActivity(Intent(this, DiskConfigureActivity::class.java))
        }
        var msgData = GetDiskDetailInfoReq(Slot)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(3, msgData))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(3, msgData)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerDiskInformationComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskInformationModule(DiskInformationModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskInformationContract.DiskInformationContractPresenter) {
            mPresenter = presenter as DiskInformationPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver?.getDiskDetailInfoBack = null
    }
}