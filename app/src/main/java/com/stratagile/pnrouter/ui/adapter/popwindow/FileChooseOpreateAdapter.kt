package com.stratagile.pnrouter.ui.adapter.popwindow

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.FileOpreateType

class FileChooseOpreateAdapter(arrayList: ArrayList<FileOpreateType>) : BaseQuickAdapter<FileOpreateType, BaseViewHolder>(R.layout.layout_file_opreate_item, arrayList) {
    override fun convert(helper: BaseViewHolder, item: FileOpreateType) {
        helper.setText(R.id.tvOpreateType, item.name)
        var imageView = helper.getView<ImageView>(R.id.ivAvatar)
        Glide.with(mContext)
                .load(mContext.resources.getIdentifier(item.icon, "mipmap", mContext.getPackageName()))
                .into(imageView)
    }

}