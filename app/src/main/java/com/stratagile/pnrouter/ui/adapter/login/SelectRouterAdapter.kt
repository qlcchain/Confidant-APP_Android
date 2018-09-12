package com.stratagile.pnrouter.ui.adapter.login

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.ShareBean

class SelectRouterAdapter(arrayList: ArrayList<RouterEntity>) : BaseQuickAdapter<RouterEntity, BaseViewHolder>(R.layout.layout_select_router_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: RouterEntity?) {
        helper!!.setText(R.id.name, item!!.routerName)
    }

}