package com.stratagile.pnrouter.ui.adapter.email

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pawegio.kandroid.loadAnimation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import kotlinx.android.synthetic.main.activity_share_file.*
import java.math.BigDecimal
import java.util.*

class EmailFileSelectAdapter(arrayList: MutableList<AttachmentFileEntity>) : BaseQuickAdapter<AttachmentFileEntity, BaseViewHolder>(R.layout.layout_attach_select_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder, item: AttachmentFileEntity, payloads: MutableList<Any>) {
        KLog.i("")
//        var checkBox = helper.getView<SmoothCheckBox>(R.id.checkBox)
//        checkBox.setChecked(item.isSelect, true)
    }
    override fun convert(helper: BaseViewHolder, item: AttachmentFileEntity) {
        var fileName = ""
        helper.setText(R.id.tvFileName, item.name)
        fileName = item.name
        var tvFileSize = helper.getView<TextView>(R.id.tvFileSize)
        if (item.size < 1048576) {
            if (item.size < 1024) {
                helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.size, RxConstTool.MemoryUnit.BYTE).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " Byte")
            } else {
                helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.size, RxConstTool.MemoryUnit.KB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " KB")
            }
        } else {
            helper.setText(R.id.tvFileSize, RxDataTool.byte2Size(item.size, RxConstTool.MemoryUnit.MB).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " KB")
        }
        tvFileSize.text = tvFileSize.text.toString() + " " + TimeUtil.getCreateTime(item.modifyTime.toLong())
        KLog.i(item.modifyTime)
        if (item.type.contains("jpg", true)) {
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
    }

}