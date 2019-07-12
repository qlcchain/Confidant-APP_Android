package com.stratagile.pnrouter.ui.adapter.login

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RouterEntity

class SelectRouterAdapter(arrayList: ArrayList<RouterEntity>) : BaseQuickAdapter<RouterEntity, BaseViewHolder>(R.layout.layout_select_router_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: RouterEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder?, item: RouterEntity?) {
        helper!!.setText(R.id.subject, item!!.routerName)
    }

}