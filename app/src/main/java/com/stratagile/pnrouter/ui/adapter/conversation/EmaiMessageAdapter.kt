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
import com.stratagile.pnrouter.db.EmailMessageEntity
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.MenuItemView
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import kotlinx.android.synthetic.main.activity_qrcode.*
import java.util.*

class EmaiMessageAdapter(arrayList: MutableList<EmailMessageEntity>) : BaseQuickAdapter<EmailMessageEntity, BaseViewHolder>(R.layout.email_row_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailMessageEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailMessageEntity) {
        var  from = item.from;
        if(from.indexOf("<") >0)
        {
            from = from.substring(0,from.indexOf("<"))
        }
        var title = helper.getView<TextView>(R.id.title)
        title.setText(from)
        var subject = helper.getView<TextView>(R.id.subject)
        subject.setText(item.subject)
        var message = helper.getView<TextView>(R.id.message)
        message.setText(item.contentText)
        var time = helper.getView<TextView>(R.id.time)
        time.setText( DateUtil.getTimestampString(DateUtil.getDate(item.date), AppConfig.instance))

        var unseen = helper.getView<TextView>(R.id.unseen)
        if(item.isSeen())
        {
            unseen.visibility = View.VISIBLE
        }else{
            unseen.visibility = View.GONE
        }
        var attach = helper.getView<TextView>(R.id.attach)
        if(item.attachmentCount >0)
        {
            attach.visibility = View.VISIBLE
            attach.text = item.attachmentCount.toString();
        }else{
            attach.visibility = View.GONE
        }
        var ivAvatar = helper.getView<ImageButtonWithText>(R.id.avatar)
        ivAvatar.setText(from)
    }

}