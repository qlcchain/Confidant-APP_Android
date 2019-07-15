package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.EmailInfoData

class EmaiInfoAdapter(arrayList: MutableList<EmailInfoData>) : BaseQuickAdapter<EmailInfoData, BaseViewHolder>(R.layout.email_info_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailInfoData?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailInfoData) {
        var detail_type = helper.getView<TextView>(R.id.detail_type)
        detail_type.setText(item.type)
        var name = helper.getView<TextView>(R.id.name)
        name.setText(item.from)
        var adress = helper.getView<TextView>(R.id.adress)
        adress.setText(item.adress)

    }

}