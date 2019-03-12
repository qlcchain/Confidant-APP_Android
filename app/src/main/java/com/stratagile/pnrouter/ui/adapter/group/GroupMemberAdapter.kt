package com.stratagile.pnrouter.ui.adapter.group

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.view.ImageButtonWithText

class GroupMemberAdapter (arrayList: ArrayList<UserEntity>) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.item_group_member_layout, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: UserEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: UserEntity) {
        val imageButtonWithText = helper.getView<ImageButtonWithText>(R.id.memberView)

        if ("1".equals(item.userId)) {
            imageButtonWithText.setImage(R.mipmap.add_contacts)
        }
    }

}