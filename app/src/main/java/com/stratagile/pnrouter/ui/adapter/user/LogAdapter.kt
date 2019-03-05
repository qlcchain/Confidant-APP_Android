package com.stratagile.pnrouter.ui.adapter.user

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.RxEncodeTool

class LogAdapter(arrayList: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_item_log, arrayList) {
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tvLog, item)
    }

}