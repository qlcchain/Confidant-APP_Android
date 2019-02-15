package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.d
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.GetDiskTotalInfoReq
import com.stratagile.pnrouter.entity.JGetDiskTotalInfoRsp
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskManagementPresenter
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_disk_management.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 11:29:37
 */

class DiskManagementActivity : BaseActivity(), DiskManagementContract.View, PNRouterServiceMessageReceiver.GetDiskTotalInfoBack {
    override fun getDiskTotalInfoReq(JGetDiskTotalInfoRsp: JGetDiskTotalInfoRsp) {


        if(JGetDiskTotalInfoRsp.params.retCode == 0)
        {
            var usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("M","").toDouble() * 100
            var totalCapacity= JGetDiskTotalInfoRsp.params.totalCapacity.replace("G","").toDouble() *1024

            var precent = (usedCapacity / totalCapacity).toString()
            if(precent.length > 4)
            {
                precent = precent.substring(0,4)
            }
            runOnUiThread {
                progressBar.progress = (usedCapacity  / totalCapacity).toInt()
                UsedAndTotal.text = getString(R.string.Used_Sapce) + JGetDiskTotalInfoRsp.params.usedCapacity +" / "+JGetDiskTotalInfoRsp.params.totalCapacity +" ("+precent+"% )"
                storage.text =  getString(R.string.Used_Sapce) + JGetDiskTotalInfoRsp.params.totalCapacity
                var InfoBeanList = JGetDiskTotalInfoRsp.params.info
                var index = 0;
                for(infoBean in InfoBeanList)
                {
                    if(index == 0)
                    {
                        if(infoBean.status == 2)
                        {
                            temperature_0.text = infoBean.temperature.toString()
                            usagetime_0.text = infoBean.powerOn.toString()
                            DeviceModel_0.text = infoBean.device.toString()
                            SerialNumber_0.text = infoBean.serial.toString()
                            UserCapacity_0.text = infoBean.capacity.toString()
                            status_0.visibility = View.GONE
                            disk_a.setBackgroundResource(R.drawable.disk_normal_bg)
                            disk_a_name.setBackgroundColor(resources.getColor(R.color.color_A0CCF9))
                        }else if(infoBean.status == 1)
                        {
                            status_0.visibility = View.VISIBLE
                            status_0.setImageResource(R.mipmap.disk_not_config)
                            disk_a.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_a_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                        }else if(infoBean.status == 0)
                        {
                            status_0.setImageResource(R.mipmap.disk_not_detected)
                            status_0.visibility = View.VISIBLE
                            disk_a.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_a_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                        }

                    }else{
                        if(infoBean.status == 2)
                        {
                            temperature_1.text = infoBean.temperature.toString()
                            usagetime_1.text = infoBean.powerOn.toString()
                            DeviceModel_1.text = infoBean.device.toString()
                            SerialNumber_1.text = infoBean.serial.toString()
                            UserCapacity_1.text = infoBean.capacity.toString()
                            disk_b.setBackgroundResource(R.drawable.disk_normal_bg)
                            disk_b_name.setBackgroundColor(resources.getColor(R.color.color_A0CCF9))
                            status_1.visibility = View.GONE
                        }else if(infoBean.status == 1)
                        {
                            status_1.visibility = View.VISIBLE
                            status_1.setImageResource(R.mipmap.disk_not_config)
                            disk_b.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_b_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                        }else if(infoBean.status == 0)
                        {
                            status_1.setImageResource(R.mipmap.disk_not_detected)
                            status_1.visibility = View.VISIBLE
                            disk_b.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_b_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                        }

                    }
                    index ++;
                }
            }


        }else{
            runOnUiThread {
                toast(R.string.system_busy)
            }
        }

    }

    @Inject
    internal lateinit var mPresenter: DiskManagementPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_management)
    }
    override fun initData() {
        title.text = resources.getText(R.string.disk_management)
        disk_a.setOnClickListener {
            startActivity(Intent(this, DiskInformationActivity::class.java))
        }
        AppConfig.instance.messageReceiver?.getDiskTotalInfoBack = this
        var msgData = GetDiskTotalInfoReq()
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

    override fun setupActivityComponent() {
       DaggerDiskManagementComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskManagementModule(DiskManagementModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskManagementContract.DIsManagementContractPresenter) {
            mPresenter = presenter as DiskManagementPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver?.getDiskTotalInfoBack = null
    }
}