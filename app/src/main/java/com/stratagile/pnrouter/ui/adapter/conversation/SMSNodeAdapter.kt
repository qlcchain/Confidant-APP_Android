package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.CheckBox
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smailnet.eamil.Utils.AESCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.SendSMSData
import com.stratagile.pnrouter.utils.DateUtil
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.TimeUtil
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
        if(item.key !="")
        {
            try {
                var aesKey = LibsodiumUtil.DecryptShareKey(item.key,ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                var souceContData = AESCipher.aesDecryptString(item.cont, aesKey)
                body.setText(souceContData)
            }catch (e:Exception)
            {

            }

        }else{
            body.setText(item.cont)
        }

        var time = helper.getView<TextView>(R.id.time)
        time.setText( TimeUtil.getFileListTime1(item.time))
        var checkBox = helper.getView<CheckBox>(R.id.checkBox)
        checkBox.isChecked = item.isLastCheck
        var nodePic = helper.getView<TextView>(R.id.nodePic)
        nodePic.setText("("+item.num+")")
    }

}