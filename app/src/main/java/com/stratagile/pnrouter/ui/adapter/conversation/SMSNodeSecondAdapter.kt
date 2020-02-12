package com.stratagile.pnrouter.ui.adapter.conversation

import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.stratagile.pnrouter.view.ImageButtonWithText
import java.util.*

class SMSNodeSecondAdapter(arrayList: MutableList<SendSMSData>) : BaseQuickAdapter<SendSMSData, BaseViewHolder>(R.layout.picencry_sent_rece_sms, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: SendSMSData?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: SendSMSData) {

        var checkBoxLeft = helper.getView<CheckBox>(R.id.checkBoxLeft)
        var checkBoxRight = helper.getView<CheckBox>(R.id.checkBoxRight)

        var bubbleRight = helper.getView<ConstraintLayout>(R.id.bubbleRight)
        val params = bubbleRight.getLayoutParams() as RelativeLayout.LayoutParams

        bubbleRight.setLayoutParams(params)
        if(item.send == 2)
        {
            if(item.isMultChecked)
            {
                checkBoxLeft.visibility = View.GONE
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                checkBoxRight.visibility = View.VISIBLE
            }else{
                checkBoxLeft.visibility = View.GONE
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                checkBoxRight.visibility = View.GONE
            }
            //helper.setVisible(R.id.checkBoxLeft,false)
            helper.setVisible(R.id.iv_userheadLeft,false)
            helper.setVisible(R.id.bubbleLeft,false)

            //helper.setVisible(R.id.checkBoxRight,true)
            //helper.setVisible(R.id.iv_userheadRight,true)
            helper.setVisible(R.id.bubbleRight,true)

            var userSouce = String(RxEncodeTool.base64Decode(item!!.user))
            if(userSouce =="")
            {
                userSouce = String(RxEncodeTool.base64Decode(item!!.tel))
            }
            var iv_userheadLeft = helper.getView<ImageButtonWithText>(R.id.iv_userheadLeft)
            iv_userheadLeft.setText(userSouce)
            var body = helper.getView<TextView>(R.id.tv_chatcontentRight)
            if(item.key !="")
            {
                var aesKey = LibsodiumUtil.DecryptShareKey(item.key,ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                var souceContData = AESCipher.aesDecryptString(item.cont, aesKey)
                body.setText(souceContData)
            }else{
                body.setText(item.cont)
            }
            var time = helper.getView<TextView>(R.id.timestampRight)
            time.setText( DateUtil.getTimestampString(Date(item.time), AppConfig.instance))
            var checkBox = helper.getView<CheckBox>(R.id.checkBoxRight)
            checkBox.isChecked = item.isLastCheck
        }else{
            if(item.isMultChecked)
            {
                checkBoxLeft.visibility = View.VISIBLE
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                checkBoxRight.visibility = View.GONE
            }else{
                checkBoxLeft.visibility = View.GONE
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                checkBoxRight.visibility = View.GONE
            }
            //helper.setVisible(R.id.checkBoxLeft,true)
           // helper.setVisible(R.id.iv_userheadLeft,true)
            helper.setVisible(R.id.bubbleLeft,true)

            //helper.setVisible(R.id.checkBoxRight,false)
            helper.setVisible(R.id.iv_userheadRight,false)
            helper.setVisible(R.id.bubbleRight,false)

            var userSouce = String(RxEncodeTool.base64Decode(item!!.user))
            if(userSouce =="")
            {
                userSouce = String(RxEncodeTool.base64Decode(item!!.tel))
            }
            var iv_userheadRight = helper.getView<ImageButtonWithText>(R.id.iv_userheadRight)
            iv_userheadRight.setText(userSouce)
            var body = helper.getView<TextView>(R.id.tv_chatcontentLeft)
            if(item.key !="")
            {
                var aesKey = LibsodiumUtil.DecryptShareKey(item.key,ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                var souceContData = AESCipher.aesDecryptString(item.cont, aesKey)
                body.setText(souceContData)
            }else{
                body.setText(item.cont)
            }
            var time = helper.getView<TextView>(R.id.timestampLeft)
            time.setText( DateUtil.getTimestampString(Date(item.time), AppConfig.instance))
            var checkBox = helper.getView<CheckBox>(R.id.checkBoxLeft)
            checkBox.isChecked = item.isLastCheck
        }
    }

}