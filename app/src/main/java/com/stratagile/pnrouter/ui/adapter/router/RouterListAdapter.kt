package com.stratagile.pnrouter.ui.adapter.router

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RouterEntity

class RouterListAdapter(arrayList: ArrayList<RouterEntity>) : BaseQuickAdapter<RouterEntity, BaseViewHolder>(R.layout.layout_router_list_item, arrayList) {
    var isCkeckMode = false
    var selectedItem = -1
    override fun convert(helper: BaseViewHolder?, item: RouterEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: RouterEntity) {
        if (isCkeckMode) {
            helper.setVisible(R.id.checkBox, true)
            helper.setVisible(R.id.ivSelect, false)
            helper.setChecked(R.id.checkBox, item.isMultChecked)
        } else {
            helper.setGone(R.id.checkBox, false)

            if (helper.layoutPosition == selectedItem) {
                helper.setVisible(R.id.ivSelect, true)
            } else {
                helper.setVisible(R.id.ivSelect, false)
            }
        }
        if (helper.layoutPosition == 0) {
            helper.setVisible(R.id.divideLine, false)
        } else {
            helper.setVisible(R.id.divideLine, true)
        }
        helper.setText(R.id.routerName, item.routerName)
    }

}