package com.stratagile.pnrouter.ui.adapter.router

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RouterEntity

class RouterListAdapter(arrayList: ArrayList<RouterEntity>) : BaseQuickAdapter<RouterEntity, BaseViewHolder>(R.layout.layout_router_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: RouterEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: RouterEntity) {
        if (helper.layoutPosition == 0) {
            helper.setVisible(R.id.divideLine, false)
        } else {
            helper.setVisible(R.id.divideLine, true)
        }
        helper.setText(R.id.routerName, item.routerName)
    }

}