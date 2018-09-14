package com.stratagile.pnrouter.ui.adapter.user

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.view.ImageButtonWithText

class NewFriendListAdapter(arrayList: ArrayList<UserEntity>) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.layout_new_friend_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: UserEntity?) {
        helper!!.setText(R.id.tvNickName, item!!.nickName)
        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
        imagebutton.setText(item!!.nickName)
        helper!!.addOnClickListener(R.id.tvRefuse)
        helper!!.addOnClickListener(R.id.tvAccept)
        when (item!!.friendStatus) {
            //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方
            0-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "已同意")
            }
            1-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "等待验证")
            }
            2-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "对方拒绝")
            }
            3-> {
                helper.setVisible(R.id.llOperate, true)
                helper.setGone(R.id.tvStatus, false)
                helper.setText(R.id.tvStatus, "")
            }
            4-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "对方删除")
            }
            5-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "已拒绝")
            }
            6-> {
                helper.setGone(R.id.llOperate, false)
                helper.setVisible(R.id.tvStatus, true)
                helper.setText(R.id.tvStatus, "我删除")
            }
        }
    }

}