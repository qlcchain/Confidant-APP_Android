package com.stratagile.pnrouter.ui.adapter.user

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.ImageButtonWithText

class NewFriendListAdapter(arrayList: ArrayList<UserEntity>) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.layout_new_friend_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: UserEntity?) {
        var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.nickName))
        helper!!.setText(R.id.tvNickName, nickNameSouce)
        KLog.i(item.toString())
        if(item!!.validationInfo != null)
        {
            var validationInfo = String(RxEncodeTool.base64Decode(item!!.validationInfo))
            helper!!.setText(R.id.validation, validationInfo)
        }
        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
        imagebutton.setText(item!!.nickName)
        helper!!.addOnClickListener(R.id.tvRefuse)
        helper!!.addOnClickListener(R.id.tvAccept)
        var it = FriendEntity()
        it.friendLocalStatus = 7
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(userId),FriendEntityDao.Properties.FriendId.eq(item.userId)).list()
        if (localFriendStatusList.size > 0)
            it = localFriendStatusList.get(0)
        when (it.friendLocalStatus) {
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