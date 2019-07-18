package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.EmailInfoData

class EmaiAttachAdapter(arrayList: MutableList<EmailInfoData>) : BaseQuickAdapter<EmailInfoData, BaseViewHolder>(R.layout.email_picture_image_grid_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailInfoData?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailInfoData) {


    }

}