package com.stratagile.pnrouter.ui.adapter.file

import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.TaskFile

class FileTaskLisytAdapter(data: MutableList<TaskFile>?) : BaseSectionQuickAdapter<TaskFile, BaseViewHolder>(R.layout.item_filetask_content, R.layout.item_filetask_head, data) {
    override fun convertHead(helper: BaseViewHolder, item: TaskFile) {
        helper.setText(R.id.tvHead, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: TaskFile) {
        var fileName =  item.t.path.substring(item.t.path.lastIndexOf("/")+1)
        helper.setText(R.id.tvFileName,fileName)
        var fileSize = (item.t.fileSize / 1024 / 1024).toString()
        helper.addOnClickListener(R.id.status)
        if (item.t.isComplete) {
            helper.setGone(R.id.progressBar, false)
            helper.setGone(R.id.status, false)
            helper.setGone(R.id.speed, false)
            if (item.t.isDownLoad) {
                helper.setText(R.id.filesize,"Download to: Local")
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.download_h))
            } else {
                helper.setText(R.id.filesize,"Upload to: Router")
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
        } else {
            helper.setGone(R.id.progressBar, true)
            helper.setGone(R.id.status, true)
            helper.setText(R.id.filesize,fileSize+"M")
            helper.setProgress(R.id.progressBar,item.t.segSeqResult,item.t.segSeqTotal)
            if (item.t.speed == 0 || item.t.segSeqTotal == 0) {
                helper.setText(R.id.speed, "0%")
            } else {
                helper.setText(R.id.speed, "" + (item.t.speed/item.t.segSeqTotal) + "%")
            }
            if (item.t.isDownLoad) {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.download_h))
            } else {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
            if (item.t.SendGgain) {
                helper.setGone(R.id.status, true)
                helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.start_n))
            } else {
                helper.setGone(R.id.status, true)
                helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.platform_n))
            }
        }
    }
}