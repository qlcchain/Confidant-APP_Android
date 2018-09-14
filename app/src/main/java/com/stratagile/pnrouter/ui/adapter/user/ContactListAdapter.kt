package com.stratagile.pnrouter.ui.adapter.user

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.view.ImageButtonWithText

class ContactListAdapter(arrayList: ArrayList<UserEntity>) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.layout_contact_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: UserEntity?) {
        helper!!.setText(R.id.tvNickName, item!!.nickName)
        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
        if (item!!.nickName != null) {
            imagebutton.setText(item?.nickName)
        }
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(item)
        helper!!.addOnClickListener(R.id.tvRefuse)
        helper!!.addOnClickListener(R.id.tvAccept)
    }

}