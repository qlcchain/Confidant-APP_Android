package com.stratagile.pnrouter.ui.adapter.user

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.GroupVerifyEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.ImageButtonWithText

class NewGroupMemberAdapter(arrayList: MutableList<GroupVerifyEntity>) : BaseQuickAdapter<GroupVerifyEntity, BaseViewHolder>(R.layout.layout_group_member_verify, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: GroupVerifyEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: GroupVerifyEntity) {
        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.avatar)
        //状态， 0 通过， 1 等待我同意， 2 我拒绝 3, 被移除群聊， 4 群解散
        when(item.verifyType) {
            0 -> {
                helper.setText(R.id.tvOpreate, "Allowed")
                helper.setBackgroundRes(R.id.tvOpreate, R.color.white)
                helper.setTextColor(R.id.tvOpreate, mContext.resources.getColor(R.color.color_b2b2b2))
                helper.setVisible(R.id.tvOpreate, true)

                helper.setText(R.id.desc, "Requested to join")
                var nickName = String(RxEncodeTool.base64Decode(item.toName))
                helper.setText(R.id.userName, nickName)
                imagebutton.setText(nickName)
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.userPubKey))+".jpg"
                imagebutton.setImageFile(fileBase58Name)
                helper.setText(R.id.groupName, String(RxEncodeTool.base64Decode(item.gname)))
                helper.setText(R.id.desc2, "Invited by")
                helper.setText(R.id.userName2, String(RxEncodeTool.base64Decode(item.fromName)))
            }
            1 -> {
                helper.setText(R.id.tvOpreate, "Accept")
                helper.setBackgroundRes(R.id.tvOpreate, R.drawable.btn_verify_group)
                helper.setTextColor(R.id.tvOpreate, mContext.resources.getColor(R.color.white))
                helper.setVisible(R.id.tvOpreate, true)
                helper.addOnClickListener(R.id.tvOpreate)
                helper.setText(R.id.desc, "Requested to join")
                var nickName = String(RxEncodeTool.base64Decode(item.toName))
                helper.setText(R.id.userName, nickName)
                imagebutton.setText(nickName)
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.userPubKey))+".jpg"
                imagebutton.setImageFile(fileBase58Name)
                helper.setText(R.id.groupName, String(RxEncodeTool.base64Decode(item.gname)))
                helper.setText(R.id.desc2, "Invited by")
                helper.setText(R.id.userName2, String(RxEncodeTool.base64Decode(item.fromName)))
            }
            2 -> {
                helper.setText(R.id.tvOpreate, "???")
                helper.setBackgroundRes(R.id.tvOpreate, R.color.white)
                helper.setTextColor(R.id.tvOpreate, mContext.resources.getColor(R.color.white))
                helper.setVisible(R.id.tvOpreate, true)

                helper.setText(R.id.desc, "Requested to join")
                var nickName = String(RxEncodeTool.base64Decode(item.toName))
                helper.setText(R.id.userName, nickName)
                imagebutton.setText(nickName)
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.userPubKey))+".jpg"
                imagebutton.setImageFile(fileBase58Name)
                helper.setText(R.id.groupName, String(RxEncodeTool.base64Decode(item.gname)))
                helper.setText(R.id.desc2, "Invited by")
                helper.setText(R.id.userName2, String(RxEncodeTool.base64Decode(item.fromName)))
            }
            3 -> {
                helper.setVisible(R.id.tvOpreate, false)

                helper.setText(R.id.desc, "You have been removed from the group")

            }
            4 -> {
                helper.setVisible(R.id.tvOpreate, false)

                helper.setText(R.id.desc, "This group has been dismissed")

            }
        }
    }

}