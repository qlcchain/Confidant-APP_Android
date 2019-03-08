package com.stratagile.pnrouter.ui.adapter.conversation

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.message.Conversation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.DateUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText
import kotlinx.android.synthetic.main.activity_qrcode.*
import java.util.*

class ConversationListAdapter(arrayList: ArrayList<Conversation>) : BaseQuickAdapter<Conversation, BaseViewHolder>(R.layout.layout_conversation_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: Conversation?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: Conversation) {
        if (item.unReadCount == 0) {
            helper.setVisible(R.id.tvNewMessageFlag, false)
        } else {
            helper.setVisible(R.id.tvNewMessageFlag, true)
            helper.setText(R.id.tvNewMessageFlag, item.unReadCount.toString())
        }
        var avatar = helper.getView<ImageButtonWithText>(R.id.ivAvatar)
        avatar.setText(item.userEntity?.nickName)
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.userEntity?.signPublicKey))+".jpg"
        avatar.setImageFile(fileBase58Name)
        helper.setText(R.id.tvNickName, item.userEntity?.nickName)
        helper.setText(R.id.tvLastMessage, item.lastMessage!!.msg)
        helper.setText(R.id.tvLastMessageTime, DateUtil.getTimestampString(Date(item.lastMessageTime)))
    }

}