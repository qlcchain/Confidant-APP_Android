package com.stratagile.pnrouter.ui.adapter.group

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.JGroupUserPullRsp
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class GroupUserAdapter (arrayList: ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean>) : BaseQuickAdapter<JGroupUserPullRsp.ParamsBean.PayloadBean, BaseViewHolder>(R.layout.item_group_member_layout, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: JGroupUserPullRsp.ParamsBean.PayloadBean?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: JGroupUserPullRsp.ParamsBean.PayloadBean) {
        val imageButtonWithText = helper.getView<ImageButtonWithText>(R.id.memberView)

        if ("1".equals(item.toxId)) {
            imageButtonWithText.setImage(R.mipmap.add_groupcontacts)
        } else if ("0".equals(item.toxId)) {
            imageButtonWithText.setImage(R.mipmap.delete_contacts)
        } else {
            val nickNameSouce = String(RxEncodeTool.base64Decode(item.nickname))
            if (nickNameSouce != null) {
                val avatarPath = Base58.encode(RxEncodeTool.base64Decode(item.userKey)) + ".jpg"
                imageButtonWithText.setImageFileInChat(avatarPath, nickNameSouce)
            }
        }
    }

}