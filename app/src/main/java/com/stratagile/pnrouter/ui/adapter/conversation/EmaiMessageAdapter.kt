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
        var menu = item.menu
        if(menu.contains("Sent") || menu.contains("已发") || menu.contains("Drafts")|| menu.contains("草稿"))
        {
            from = item.to;
            if(from.contains(","))
            {
                var formList = from.split(",")
                for(item in formList){

                    account += item.substring(item.indexOf("<") +1,item.length - 1) +","
                    var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(item)).list()
                    if(localEmailContacts.size != 0)
                    {
                        var localEmailContactsItem = localEmailContacts.get(0)
                        formName += localEmailContactsItem.name +","
                    }else{
                        if(item.indexOf("<") >=0)
                        {
                            var name = item.substring(0,item.indexOf("<")).trim().replace("\"","").replace("\"","")
                            formName += name +","
                        }else{
                            formName += item.substring(0,item.indexOf("@")) +","
                        }

                    }
                }
                if(account.contains(","))
                {
                    account = account.substring(0,account.lastIndexOf(","))
                }
                if(formName.contains(","))
                {
                    formName = formName.substring(0,formName.lastIndexOf(","))
                }
            }else{
                account = from.substring(from.indexOf("<") +1,from.length - 1)
                var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                if(localEmailContacts.size != 0)
                {
                    var localEmailContactsItem = localEmailContacts.get(0)
                    formName = localEmailContactsItem.name
                }else{
                    if(from.indexOf("<") >=0)
                    {
                        formName = from.substring(0,from.indexOf("<"))
                    }else{
                        formName = from.substring(0,from.indexOf("@"))
                    }

                }
            }

        }else{
            if(from.indexOf("<") >=0)
            {
                formName = from.substring(0,from.indexOf("<"))
            }else{
                formName = from.substring(0,from.indexOf("@"))
            }
        }

        var title = helper.getView<TextView>(R.id.title)
        formName = formName.replace("\"","")
        formName = formName.replace("\"","")
        title.setText(formName)
        var subject = helper.getView<TextView>(R.id.subject)
        subject.setText(item.subject)
        var message = helper.getView<TextView>(R.id.message)
        if(item.originalText != null && item.originalText != "")
        {
            var originalTextCun = StringUitl.StripHT(item.originalText)
            /*var originalTextCunNew = originalTextCun
            var endIndex = originalTextCunNew.indexOf(" ")
            if(endIndex < 0)
            {
                endIndex = originalTextCunNew.length
            }
            originalTextCunNew =  originalTextCunNew.substring(0,endIndex)*/
            message.setText(originalTextCun.trim())
        }else{
            message.setText(item.contentText.trim())
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
        var lockPic = helper.getView<TextView>(R.id.lockPic)
        if(item.originalText != null && item.originalText != "")
        {
            lockPic.visibility = View.VISIBLE
        }else{
            if(item.content.contains("newconfidantpass"))
            {
                lockPic.visibility = View.VISIBLE
            }else
            {
                lockPic.visibility = View.GONE
            }

        }
        var attach = helper.getView<TextView>(R.id.attach)
        /*if(item.attachmentCount >0)
        {
            attach.visibility = View.VISIBLE
            attach.text = item.attachmentCount.toString();
        }else{
            attach.visibility = View.GONE
        }*/
        if(item.isContainerAttachment())
        {
            attach.visibility = View.VISIBLE
            attach.text = "";
        }else{
            attach.visibility = View.GONE
        }
        var ivAvatar = helper.getView<ImageButtonWithText>(R.id.avatar)
        ivAvatar.setText(formName)
    }

}