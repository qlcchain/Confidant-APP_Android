package com.stratagile.pnrouter.ui.adapter.file

import android.view.View
import android.widget.ImageView
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
import java.util.*

class FileSelectAdapter(arrayList: MutableList<JPullFileListRsp.ParamsBean.PayloadBean>) : BaseQuickAdapter<JPullFileListRsp.ParamsBean.PayloadBean, BaseViewHolder>(R.layout.layout_select_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: JPullFileListRsp.ParamsBean.PayloadBean?, payloads: MutableList<Any>) {
        KLog.i("")
    }
    override fun convert(helper: BaseViewHolder, item: JPullFileListRsp.ParamsBean.PayloadBean) {
        var fileName = ""
        try {
            var subName = item.fileName.substring(item.fileName.lastIndexOf("/") + 1)
            fileName = String(Base58.decode(subName))
        }catch (e :Exception)
        {

        }
        helper.setText(R.id.tvFileName, fileName)
        helper.setText(R.id.tvFileSize, NetUtils.parseSize(item.fileSize.toLong()))
        if (fileName.contains("jpg")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (fileName.contains("pdf")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.pdf))
        } else if (fileName.contains("mp4")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.video))
        } else if (fileName.contains("png")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc_img))
        } else if (fileName.contains("txt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.txt))
        } else if (fileName.contains("ppt")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.ppt))
        } else if (fileName.contains("xls")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.xls))
        } else if (fileName.contains("doc")) {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.doc))
        } else {
            helper.setImageDrawable(R.id.ivAvatar, mContext.resources.getDrawable(R.mipmap.other))
        }
    }

}