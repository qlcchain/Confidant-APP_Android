package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.CheckBox
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.entity.SendSMSData
import com.stratagile.pnrouter.utils.DateUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import java.util.*

class SMSNodeAdapter(arrayList: MutableList<SendSMSData>) : BaseQuickAdapter<SendSMSData, BaseViewHolder>(R.layout.picencry_node_sms_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: SendSMSData?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: SendSMSData) {

        var title = helper.getView<TextView>(R.id.title)
        if(item.user != "")
        {
            var userSouce = String(RxEncodeTool.base64Decode(item.user))
            title.setText(userSouce)
        }else{
            var userSouce = String(RxEncodeTool.base64Decode(item.tel))
            title.setText(userSouce)
        }
        var body = helper.getView<TextView>(R.id.body)
        body.setText(item.cont)
        var time = helper.getView<TextView>(R.id.time)
        time.setText( DateUtil.getTimestampString(Date(item.time), AppConfig.instance))
        var checkBox = helper.getView<CheckBox>(R.id.checkBox)
        checkBox.isChecked = item.isLastCheck

    }

}