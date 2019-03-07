package com.stratagile.pnrouter.ui.adapter.file

import android.view.View
import android.widget.CompoundButton
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.TaskFile
import com.stratagile.pnrouter.utils.NetUtils
import com.stratagile.pnrouter.utils.Base58
import java.math.BigDecimal

class FileTaskLisytAdapter(data: MutableList<TaskFile>?) : BaseSectionQuickAdapter<TaskFile, BaseViewHolder>(R.layout.item_filetask_content, R.layout.item_filetask_head, data) {
    override fun convertHead(helper: BaseViewHolder, item: TaskFile) {
        helper.setText(R.id.tvHead, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: TaskFile) {
        var fileOriginalName = ""
        var fileName =  item.t.fileKey
        if(item.t.status != 0)
        {
            helper.setGone(R.id.checkBox, true)
            helper.setChecked(R.id.checkBox,item.t.isCheck)
        }else{
            helper.setGone(R.id.checkBox, false)
            helper.setChecked(R.id.checkBox,false)
        }
        if(!item.t.isDownLoad)
        {
            fileOriginalName = fileName
            if(fileName.indexOf(".") < 0)
            {
                try {
                    fileOriginalName =  String(Base58.decode(fileName))
                } catch (e : Exception) {

                }
            }
            helper.setText(R.id.tvFileName,fileOriginalName)
        }else{
            fileOriginalName = fileName
            if(fileName.indexOf(".") < 0)
            {
                fileOriginalName =  String(Base58.decode(fileName))
            }
            helper.setText(R.id.tvFileName,fileOriginalName)
        }
        var fileSize = (item.t.fileSize / 1024 / 1024).toString()
        helper.addOnClickListener(R.id.status)
        helper.addOnClickListener(R.id.stopBtn)
        if (item.t.isComplete) {
            helper.setGone(R.id.progressBar, false)
            helper.setVisible(R.id.status, false)
            helper.setVisible(R.id.stopBtn, false)
            helper.setGone(R.id.speed, false)
            if (item.t.isDownLoad) {
                helper.setText(R.id.filesize,"Download to: Local")
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.download_h))
            } else {
                helper.setText(R.id.filesize,"Upload to: Router")
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
        } else {

            if (item.t.isStop) {
                helper.setVisible(R.id.status, true)
                helper.setVisible(R.id.stopBtn, false)
            } else {
                helper.setVisible(R.id.status, false)
                helper.setVisible(R.id.stopBtn, true)
            }
            helper.setGone(R.id.progressBar, true)
            helper.setText(R.id.filesize,NetUtils.parseSize(item.t.fileSize))
            helper.setProgress(R.id.progressBar,item.t.segSeqResult,item.t.segSeqTotal)
            KLog.i("" + item.t.segSeqResult)
            KLog.i("" + item.t.segSeqTotal)
            if (item.t.segSeqResult == 0 || item.t.segSeqTotal == 0) {
                helper.setText(R.id.speed, "0%")
            } else {
                var ddd = item.t.segSeqResult.toBigDecimal().divide(item.t.segSeqTotal.toBigDecimal(), 2, BigDecimal.ROUND_HALF_DOWN)
                helper.setText(R.id.speed, "" + ddd.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString()+ "%")
            }
            if (item.t.isDownLoad) {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.download_h))
            } else {
                helper.setImageDrawable(R.id.type, mContext.resources.getDrawable(R.mipmap.upload_h))
            }
            if (item.t.SendGgain) {
                if(item.t.status != 0)
                {
                    helper.setGone(R.id.status, false)
                }else{
                    if(item.t.isStop)
                    {
                        helper.setVisible(R.id.status, true)
                        helper.setVisible(R.id.stopBtn, false)
                    }else{
                        helper.setVisible(R.id.status, false)
                        helper.setVisible(R.id.stopBtn, true)
                    }
                }
                //helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.start_n))
            } else {
                helper.setVisible(R.id.status, false)
                //helper.setImageDrawable(R.id.status, mContext.resources.getDrawable(R.mipmap.platform_n))
            }
        }
        if (fileOriginalName.contains("jpg")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (fileOriginalName.contains("pdf")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.pdf))
        } else if (fileOriginalName.contains("mp4")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.video))
        } else if (fileOriginalName.contains("png")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (fileOriginalName.contains("txt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.txt))
        } else if (fileOriginalName.contains("ppt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.ppt))
        } else if (fileOriginalName.contains("xls")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.xls))
        } else if (fileOriginalName.contains("doc")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc))
        } else {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.other))
        }
    }
}