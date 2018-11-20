package com.stratagile.pnrouter.ui.adapter.user

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.RxEncodeTool

class ShareSelfAdapter(arrayList: ArrayList<ShareBean>) : BaseQuickAdapter<ShareBean, BaseViewHolder>(R.layout.layout_share_self_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: ShareBean?) {
        var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.name))
        helper!!.setText(R.id.name, nickNameSouce)
        var imageView = helper!!.getView<ImageView>(R.id.avatar)
        Glide.with(mContext)
                .load(mContext.resources.getIdentifier(item.avatar, "mipmap", mContext.packageName))
                .into(imageView)
    }

}