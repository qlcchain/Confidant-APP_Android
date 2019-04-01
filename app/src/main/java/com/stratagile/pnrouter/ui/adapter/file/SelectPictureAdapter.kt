package com.stratagile.pnrouter.ui.adapter.file

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.ShareBean

class SelectPictureAdapter(arrayList: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_select_router_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: String?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper!!.setText(R.id.name, item!!)
    }

}