package com.stratagile.pnrouter.ui.adapter.file

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.data.fileInfo.FileInfo
import com.stratagile.pnrouter.utils.RxConstTool
import com.stratagile.pnrouter.utils.RxDataTool
import java.math.BigDecimal

class FileInfosAdapter(data: List<FileInfo>) : BaseQuickAdapter<FileInfo, BaseViewHolder>(R.layout.layout_attach_select_file_list_item, data) {
    override fun convert(helper: BaseViewHolder, item: FileInfo) {
        helper.setText(R.id.tvFileName, item.name)
        if (item.isFile) {
            var fileName = ""
            helper.setText(R.id.tvFileName, item.name)
            fileName = item.name
            helper.setGone(R.id.tvFileSize, true)
            if (item.getmFile().length() < 1048576) {
                if (item.getmFile().length() < 1024) {
                    helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.getmFile().length(), RxConstTool.MemoryUnit.BYTE).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " Byte")
                } else {
                    helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.getmFile().length(), RxConstTool.MemoryUnit.KB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " KB")
                }
            } else {
                helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.getmFile().length(), RxConstTool.MemoryUnit.MB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " MB")
            }
            if (item.name.contains("jpg", true)) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
            } else if (fileName.contains("pdf")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.sheet_pdf))
            } else if (fileName.contains("mp4")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.video))
            } else if (fileName.contains("png")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
            } else if (fileName.contains("txt")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.txt))
            } else if (fileName.contains("ppt")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.sheet_ppt))
            } else if (fileName.contains("xls")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.sheet_excel))
            } else if (fileName.contains("doc")) {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.sheet_word))
            } else {
                helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.other))
            }
        } else {
            helper.setImageResource(R.id.ivAvatar, R.mipmap.sheet_file_directory)
            helper.setGone(R.id.tvFileSize, false)
        }
    }

    override fun convert(helper: BaseViewHolder, item: FileInfo, payloads: List<Any>) {
        KLog.i("")
    }
}