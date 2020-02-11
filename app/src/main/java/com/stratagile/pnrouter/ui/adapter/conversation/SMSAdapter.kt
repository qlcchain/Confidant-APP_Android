package com.stratagile.pnrouter.ui.adapter.conversation

import android.view.View
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.SMSEntity
import com.stratagile.pnrouter.utils.DateUtil
import java.util.*

class SMSAdapter(arrayList: MutableList<SMSEntity>) : BaseQuickAdapter<SMSEntity, BaseViewHolder>(R.layout.picencry_sms_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: SMSEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: SMSEntity) {
        /* helper.addOnClickListener(R.id.smsRoot)
         helper.addOnClickListener(R.id.title)
         helper.addOnClickListener(R.id.body)
         helper.addOnClickListener(R.id.time)*/
        var avatar_container = helper.getView<RelativeLayout>(R.id.avatar_container)
        if(item.type == 1)
        {
            if(item.read == 0)
            {
                avatar_container.visibility = View.VISIBLE
            }else{
                avatar_container.visibility = View.INVISIBLE
            }
        }else{
            avatar_container.visibility = View.INVISIBLE
        }
        var title = helper.getView<TextView>(R.id.title)
        if(item.personName!=null && item.personName!= "")
        {
            title.setText(item.personName)
        }else{
            title.setText(item.address)
        }
        var body = helper.getView<TextView>(R.id.body)
        body.setText(item.body)
        var time = helper.getView<TextView>(R.id.time)
        time.setText( DateUtil.getTimestampString(Date(item.date), AppConfig.instance))
        var checkBox = helper.getView<CheckBox>(R.id.checkBox)
        checkBox.isChecked = item.isLastCheck
        if(item.isUpload())
        {
            helper.setVisible(R.id.nodePic,true)
        }else{
            helper.setVisible(R.id.nodePic,false)
        }
    }

}