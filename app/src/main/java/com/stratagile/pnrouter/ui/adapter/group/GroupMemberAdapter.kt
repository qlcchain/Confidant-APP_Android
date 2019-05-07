package com.stratagile.pnrouter.ui.adapter.group

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.JGroupUserPullRsp
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class GroupMemberAdapter(arrayList: ArrayList<JGroupUserPullRsp.ParamsBean.PayloadBean>) : BaseQuickAdapter<JGroupUserPullRsp.ParamsBean.PayloadBean, BaseViewHolder>(R.layout.item_group_members_layout, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: JGroupUserPullRsp.ParamsBean.PayloadBean?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: JGroupUserPullRsp.ParamsBean.PayloadBean) {
        val imageButtonWithText = helper.getView<ImageButtonWithText>(R.id.ivAvatar)
        val imagebutton = helper.getView<ImageButtonWithText>(R.id.ivAvatar)
        val avatarPath = Base58.encode(RxEncodeTool.base64Decode(item.userKey)) + ".jpg"
        var nickName = String(RxEncodeTool.base64Decode(item.nickname))
        if(item.remarks != null && !item.remarks.equals(""))
        {
            nickName = String(RxEncodeTool.base64Decode(item.remarks))
        }else{
            var userEntity: UserEntity? = null
            val userList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(item.toxId)).list()
            if (userList.size != 0) {
                userEntity = userList[0]
                if(userEntity.remarks != null && !userEntity.remarks.equals(""))
                {
                    nickName = String(RxEncodeTool.base64Decode(userEntity.remarks))
                }
            }
        }
        imagebutton.setImageFileInChat(avatarPath, nickName)
        helper.setText(R.id.tvNickName, nickName)
    }

}