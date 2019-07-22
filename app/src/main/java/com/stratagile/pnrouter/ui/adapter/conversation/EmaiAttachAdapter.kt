package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.EmailAttachEntity
import com.stratagile.pnrouter.entity.EmailInfoData
import com.stratagile.pnrouter.R.id.view
import android.R.attr.path
import android.net.Uri
import java.io.File


class EmaiAttachAdapter(arrayList: MutableList<EmailAttachEntity>) : BaseQuickAdapter<EmailAttachEntity, BaseViewHolder>(R.layout.email_picture_image_grid_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailAttachEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailAttachEntity) {
       /* var iv_picture = helper.getView<ImageView>(R.id.iv_picture)
        val file = File(item.localPath)
        val uri = Uri.fromFile(file)
        iv_picture.setImageURI(uri)*/

    }

}