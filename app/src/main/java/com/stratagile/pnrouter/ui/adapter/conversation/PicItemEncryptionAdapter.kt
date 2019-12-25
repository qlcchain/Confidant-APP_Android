package com.stratagile.pnrouter.ui.adapter.conversation

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hyphenate.easeui.utils.EaseImageUtils
import com.pawegio.kandroid.loadAnimation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.PicMenu
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.DrawableEnTextView
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import java.io.File
import java.util.*

class PicItemEncryptionAdapter(arrayList: MutableList<LocalFileItem>) : BaseQuickAdapter<LocalFileItem, BaseViewHolder>(R.layout.layout_encryption_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: LocalFileItem?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: LocalFileItem) {
        helper.addOnClickListener(R.id.itemTypeIcon)
        helper.addOnClickListener(R.id.itemInfo)
        helper.addOnClickListener(R.id.opMenu)
        helper.setText(R.id.tvFileName,item.fileName)
        var timeStr = DateUtil.getTimestampString(Date(item.creatTime), AppConfig.instance)
        if(item.fileSize >= 1024 * 1024)
        {
            helper.setText(R.id.tvFileSize,timeStr +" "+(item.fileSize / (1024 *1024)).toFloat().toString() +"M")
        }else{
            helper.setText(R.id.tvFileSize,timeStr +" "+(item.fileSize / 1024).toFloat().toString() +"K")
        }
        helper.setVisible(R.id.nodeTips,item.isUpLoad)
        var itemTypeIcon = helper.getView<ImageView>(R.id.itemTypeIcon)

        var namePre = item.filePath.substring(0,item.filePath.lastIndexOf("/")+1)
        var name= item.filePath.substring(item.filePath.lastIndexOf("/")+1,item.filePath.length)
        var thumbPath = ""
        if(item.fileType == 1)
        {
            thumbPath = namePre + "th"+ name
        }else if(item.fileType == 4)
        {
            thumbPath = namePre + "th"+ name.replace("mp4","jpg")
        }
        var thumFile = File(thumbPath)
        if(thumFile.exists())
        {
            if(item.fileType == 4)
            {
                helper.setVisible(R.id.chatting_status_btn,true)
            }else
            {
                helper.setVisible(R.id.chatting_status_btn,false)
            }
            itemTypeIcon.setImageURI(Uri.fromFile(thumFile))
        }else{
            helper.setVisible(R.id.chatting_status_btn,false)
            if(item.fileType == 1)
            {
                itemTypeIcon.setImageResource(R.mipmap.jpg)
            }
            else if(item.fileType == 4)
            {
                itemTypeIcon.setImageResource(R.mipmap.mp4)
            }

        }
    }

}