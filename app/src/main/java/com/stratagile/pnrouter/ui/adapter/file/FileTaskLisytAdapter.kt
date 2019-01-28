package com.stratagile.pnrouter.ui.adapter.file

import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.TaskFile

class FileTaskLisytAdapter(data: MutableList<TaskFile>?) : BaseSectionQuickAdapter<TaskFile, BaseViewHolder>(R.layout.item_filetask_content, R.layout.item_filetask_head, data) {
    override fun convertHead(helper: BaseViewHolder, item: TaskFile) {
        KLog.i("ddd")
        helper.setText(R.id.tvHead, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: TaskFile) {
        KLog.i("ddxxxx")
        helper.setText(R.id.tvFileName,item.t.name)
        if (item.t.isComplete) {
            helper.setGone(R.id.progressBar, false)
            helper.setGone(R.id.status, false)
        } else {
            helper.setGone(R.id.progressBar, true)
            helper.setGone(R.id.status, true)
            if (item.t.isDownLoad) {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.download_h))
            } else {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
            if (item.t.isStop) {
                helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.start_n))
            } else {
                helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.platform_n))
            }
        }
    }
}