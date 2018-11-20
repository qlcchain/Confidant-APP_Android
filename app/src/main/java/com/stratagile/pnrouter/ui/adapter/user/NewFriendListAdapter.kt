package com.stratagile.pnrouter.ui.adapter.user

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class NewFriendListAdapter(arrayList: ArrayList<UserEntity>) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.layout_new_friend_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: UserEntity?) {
        var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.nickName))
        helper!!.setText(R.id.tvNickName, nickNameSouce)
        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
        imagebutton.setText(item!!.nickName)
        helper!!.addOnClickListener(R.id.tvRefuse)
        helper!!.addOnClickListener(R.id.tvAccept)
        when (item!!.friendStatus) {
            //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方
            0-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.agreed))
            }
            1-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.wait_for_verification))
            }
            2-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.rejected))
            }
            3-> {
                helper.setVisible(R.id.llOperate, true)
                helper.setGone(R.id.tvStatus, false)
                helper.setText(R.id.tvStatus, "")
            }
            4-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.deleted))
            }
            5-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.rejected))
            }
            6-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, mContext.getString(R.string.deleted))
            }
        }

        if ((helper.layoutPosition + 1) == mData.size) {
            helper.setGone(R.id.line, false)
        } else {
            helper.setGone(R.id.line, true)
        }
    }

}