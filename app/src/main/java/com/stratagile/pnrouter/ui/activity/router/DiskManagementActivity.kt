package com.stratagile.pnrouter.ui.activity.router

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
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
import com.stratagile.pnrouter.entity.LogOutReq
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskManagementPresenter
import com.stratagile.pnrouter.utils.FileMangerDownloadUtils
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_disk_management.*
import org.w3c.dom.Text

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
            var usedCapacity = 0.0
            if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("M"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("M","").toDouble() * 100
            }else if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("G"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("G","").toDouble() * 1024 * 100
            }else  if(JGetDiskTotalInfoRsp.params.usedCapacity.contains("T"))
            {
                usedCapacity = JGetDiskTotalInfoRsp.params.usedCapacity.replace("T","").toDouble() * 1024 * 1024 * 100
            }
            var totalCapacity= 1.0
            if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("M"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("M","").toDouble()
            }else if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("G"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("G","").toDouble() * 1024
            }else if(JGetDiskTotalInfoRsp.params.totalCapacity.contains("T"))
            {
                totalCapacity = JGetDiskTotalInfoRsp.params.totalCapacity.replace("T","").toDouble() * 1024 * 1024
            }
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
                           /* temperature_0.text = infoBean.temperature.toString()
                            usagetime_0.text = infoBean.powerOn.toString()
                            DeviceModel_0.text = infoBean.device.toString()
                            SerialNumber_0.text = infoBean.serial.toString()
                            UserCapacity_0.text = infoBean.capacity.toString()*/
                            rightContent.text = infoBean.temperature.toString() +"\n"+infoBean.powerOn.toString() +"\n"+infoBean.device.toString()+"\n"+infoBean.serial.toString()+"\n"+infoBean.capacity.toString()
                            status_0.visibility = View.GONE
                            disk_a.setBackgroundResource(R.drawable.disk_normal_bg)
                            disk_a_name.setBackgroundColor(resources.getColor(R.color.color_A0CCF9))
                            disk_a.setOnClickListener {
                                val intent = Intent(this, DiskInformationActivity::class.java)
                                intent.putExtra("Slot", 0)
                                startActivity(intent)
                            }
                        }else if(infoBean.status == 1)
                        {
                            status_0.visibility = View.VISIBLE
                            status_0.setImageResource(R.mipmap.disk_not_config)
                            disk_a.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_a_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                            disk_b.setOnClickListener {
                                val intent = Intent(this, DiskConfigureActivity::class.java)
                                intent.putExtra("Slot", 0)
                                startActivity(intent)
                            }
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
                            /*temperature_1.text = infoBean.temperature.toString()
                            usagetime_1.text = infoBean.powerOn.toString()
                            DeviceModel_1.text = infoBean.device.toString()
                            SerialNumber_1.text = infoBean.serial.toString()
                            UserCapacity_1.text = infoBean.capacity.toString()*/
                            rightContent2.text = infoBean.temperature.toString() +"\n"+infoBean.powerOn.toString() +"\n"+infoBean.device.toString()+"\n"+infoBean.serial.toString()+"\n"+infoBean.capacity.toString()

                            disk_b.setBackgroundResource(R.drawable.disk_normal_bg)
                            disk_b_name.setBackgroundColor(resources.getColor(R.color.color_A0CCF9))
                            status_1.visibility = View.GONE
                            disk_b.setOnClickListener {
                                val intent = Intent(this, DiskInformationActivity::class.java)
                                intent.putExtra("Slot", 2)
                                startActivity(intent)
                            }
                        }else if(infoBean.status == 1)
                        {
                            status_1.visibility = View.VISIBLE
                            status_1.setImageResource(R.mipmap.disk_not_config)
                            disk_b.setBackgroundResource(R.drawable.disk_notconfigured_bg)
                            disk_b_name.setBackgroundColor(resources.getColor(R.color.color_BFBFBF))
                            disk_b.setOnClickListener {
//                                val intent = Intent(this, DiskConfigureActivity::class.java)
//                                intent.putExtra("Slot", 2)
//                                startActivity(intent)
                                showHasBeenFormatDialog()
                            }
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
        llRouterAlias.setOnClickListener {
            val intent = Intent(this, DiskConfigureActivity::class.java)
            intent.putExtra("Slot", 0)
            startActivity(intent)
        }
    }
    override fun initData() {
        title.text = resources.getText(R.string.disk_management)
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

    private fun showFormatDialog() {
        val view = View.inflate(this, R.layout.layout_format, null)
        val sweetAlertDialog = CommonDialog(this)
        val window = sweetAlertDialog.window
        window.setBackgroundDrawableResource(android.R.color.transparent)
        sweetAlertDialog.setView(view)
        sweetAlertDialog.show()
    }

    private fun showHasBeenFormatDialog() {
        val view = View.inflate(this, R.layout.layout_has_been_format, null)
        //取消或确定按钮监听事件处l
        val sweetAlertDialog = CommonDialog(this)
        val window = sweetAlertDialog.window
        var tvReboot = view.findViewById<TextView>(R.id.tvReboot)
        tvReboot.setOnClickListener {
            //todo
        }
        window.setBackgroundDrawableResource(android.R.color.transparent)
        sweetAlertDialog.setView(view)
        sweetAlertDialog.show()
    }

    private fun showRebootting() {
        val view = View.inflate(this, R.layout.layout_format, null)
        //取消或确定按钮监听事件处l
        val sweetAlertDialog = CommonDialog(this)
        var content = view.findViewById<TextView>(R.id.content)
        content.text = getString(R.string.rebooting)
        val window = sweetAlertDialog.window
        window.setBackgroundDrawableResource(android.R.color.transparent)
        sweetAlertDialog.setView(view)
        sweetAlertDialog.show()
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