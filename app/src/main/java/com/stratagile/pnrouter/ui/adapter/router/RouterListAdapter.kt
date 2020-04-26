package com.stratagile.pnrouter.ui.adapter.router

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.ImageButtonWithText

class RouterListAdapter(arrayList: ArrayList<RouterEntity>) : BaseQuickAdapter<RouterEntity, BaseViewHolder>(R.layout.layout_router_list_item, arrayList) {
    var isCkeckMode = false
    var selectedItem = -1
    override fun convert(helper: BaseViewHolder?, item: RouterEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: RouterEntity) {

        var userAvatar = helper.getView<ImageButtonWithText>(R.id.userAvatar)
        userAvatar.setText( item.routerName)
        if (isCkeckMode) {
            helper.setVisible(R.id.checkBox, true)
            helper.setVisible(R.id.ivSelect, false)
            helper.setChecked(R.id.checkBox, item.isMultChecked)
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(item.adminId != null && item.adminId.equals(userId) || item.routerId.equals(ConstantValue.currentRouterId))
            {
                helper.setEnabled(R.id.checkBox,false)
                helper.itemView.alpha= 0.5f
            }else{
                helper.setEnabled(R.id.checkBox,true)
                helper.itemView.alpha= 1.0f
            }
        } else {
            helper.itemView.visibility = View.VISIBLE
            helper.setGone(R.id.checkBox, false)

            if (helper.layoutPosition == selectedItem) {
                helper.setVisible(R.id.ivSelect, true)
            } else {
                helper.setVisible(R.id.ivSelect, false)
            }
        }
        helper.setVisible(R.id.divideLine, true)
       /* if (helper.layoutPosition == 0) {
            helper.setVisible(R.id.divideLine, false)
        } else {
            helper.setVisible(R.id.divideLine, true)
        }*/
        helper.setText(R.id.routerName, item.routerName)
    }

}