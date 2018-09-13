package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.ShareBean

class ConversationListAdapter(arrayList: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_conversation_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: String?) {
    }

}