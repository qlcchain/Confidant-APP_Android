package com.stratagile.pnrouter.ui.adapter.group

import android.content.Intent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hyphenate.easeui.EaseConstant
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class GroupAdapter (arrayList: ArrayList<GroupEntity>) : BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.item_group_layout, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: GroupEntity?, payloads: MutableList<Any>) {

    }

    override fun convert(helper: BaseViewHolder, item: GroupEntity) {
        val imageButtonWithText = helper.getView<ImageButtonWithText>(R.id.groupIcon)
        helper.setText(R.id.groupName,String(RxEncodeTool.base64Decode(item.gName)))
        imageButtonWithText.setImage(R.mipmap.group_head)
        helper.itemView.setOnClickListener {
            val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_USER_ID, item.gId.toString())
            UserDataManger.currentGroupData = item
            mContext.startActivity(intent)
        }
    }

}