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
        var formName = ""
        var  from = item.from;
        var account = ""
        if(from.contains(item.account))
        {
            from = item.to;
            account = from.substring(from.indexOf("<") +1,from.length - 1)
            var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
            if(localEmailContacts.size != 0)
            {
                var localEmailContactsItem = localEmailContacts.get(0)
                formName = localEmailContactsItem.name
            }else{
                formName = from.substring(0,from.indexOf("<"))
            }
        }else{
            if(from.indexOf("<") >0)
            {
                formName = from.substring(0,from.indexOf("<"))
            }
        }

        var title = helper.getView<TextView>(R.id.title)
        title.setText(formName)
        var subject = helper.getView<TextView>(R.id.subject)
        subject.setText(item.subject)
        var message = helper.getView<TextView>(R.id.message)
        if(item.originalText != null && item.originalText != "")
        {
            var originalTextCun = StringUitl.getHtmlText(item.originalText)
            message.setText(originalTextCun)
        }else{
            message.setText(item.contentText)
        }
        var time = helper.getView<TextView>(R.id.time)
        time.setText( DateUtil.getTimestampString(DateUtil.getDate(item.date), AppConfig.instance))

        var unseen = helper.getView<TextView>(R.id.unseen)
        if(item.isSeen())
        {
            unseen.visibility = View.GONE
        }else{
            unseen.visibility = View.VISIBLE
        }
        var startPic = helper.getView<TextView>(R.id.startPic)
        if(item.isStar())
        {
            startPic.visibility = View.VISIBLE
        }else{
            startPic.visibility = View.GONE
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
        ivAvatar.setText(formName)
    }

}