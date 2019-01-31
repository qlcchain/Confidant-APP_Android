package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.RecentFile
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.TimeUtil

class FileListAdapter(arrayList: MutableList<RecentFile>) : BaseQuickAdapter<RecentFile, BaseViewHolder>(R.layout.layout_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder, item: RecentFile) {
        helper.addOnClickListener(R.id.fileOpreate)
        helper.setText(R.id.tvFileTime, TimeUtil.getFileListTime1(item.timeStamp))
        helper.setText(R.id.tvFileName, item.fileName)
        helper.setText(R.id.friendName, item.friendName)
        when(item.opreateType) {
            0 -> {
                helper.setImageDrawable(R.id.fileOpreateType, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
            1 -> {
                helper.setImageDrawable(R.id.fileOpreateType, mContext.resources.getDrawable(R.mipmap.download_1))
            }
            2 -> {
                helper.setImageDrawable(R.id.fileOpreateType, mContext.resources.getDrawable(R.mipmap.delete_1))
            }
        }
        if (item.fileName.contains("jpg")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (item.fileName.contains("pdf")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.pdf))
        } else if (item.fileName.contains("mp4")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.video))
        } else if (item.fileName.contains("png")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (item.fileName.contains("txt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.txt))
        } else if (item.fileName.contains("ppt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.ppt))
        } else if (item.fileName.contains("xls")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.xls))
        } else if (item.fileName.contains("doc")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc))
        } else {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.other))
        }
    }

}