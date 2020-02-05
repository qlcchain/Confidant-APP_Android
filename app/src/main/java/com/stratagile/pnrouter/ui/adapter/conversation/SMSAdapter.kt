package com.stratagile.pnrouter.ui.adapter.conversation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pawegio.kandroid.loadAnimation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.EmailContactsEntityDao
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.MenuItemView
import com.stratagile.pnrouter.entity.SMSEntity
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import kotlinx.android.synthetic.main.activity_qrcode.*
import java.util.*

class SMSAdapter(arrayList: MutableList<SMSEntity>) : BaseQuickAdapter<SMSEntity, BaseViewHolder>(R.layout.picencry_sms_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: SMSEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: SMSEntity) {
        var title = helper.getView<TextView>(R.id.title)
        title.setText(item.address)
        var body = helper.getView<TextView>(R.id.body)
        body.setText(item.body)
        var time = helper.getView<TextView>(R.id.time)
        //time.setText( DateUtil.getTimestampString(DateUtil.getDate(item.date), AppConfig.instance))
    }

}