package com.stratagile.pnrouter.ui.adapter.conversation

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.db.EmailAttachEntity
import com.stratagile.pnrouter.entity.EmailInfoData
import com.stratagile.pnrouter.R.id.view
import android.R.attr.path
import android.net.Uri
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import java.io.File


class EmaiAttachAdapter(arrayList: MutableList<EmailAttachEntity>) : BaseQuickAdapter<EmailAttachEntity, BaseViewHolder>(R.layout.email_picture_image_grid_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailAttachEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailAttachEntity) {
        var iv_picture = helper.getView<ImageView>(R.id.iv_picture)
        var iv_add = helper.getView<ImageView>(R.id.iv_add)

        var deleteBtn = helper.getView<TextView>(R.id.deleteBtn)
        var deleteBtnParent = helper.getView<LinearLayout>(R.id.deleteBtnParent)
        var pic_size = helper.getView<TextView>(R.id.pic_size)
        if(item.canDelete)
        {
            deleteBtnParent.visibility = View.VISIBLE
            helper.addOnClickListener(R.id.deleteBtn)
        }else{
            deleteBtnParent.visibility = View.GONE
        }
        var fileRoot = helper.getView<LinearLayout>(R.id.fileRoot)
        if(item.isHasData)
        {
            iv_picture.visibility = View.VISIBLE
            iv_add.visibility = View.GONE
            pic_size.visibility = View.VISIBLE
            val file = File(item.localPath)
            var sizeLong = (file.length().toFloat() / 1000).toFloat();
            var size = String.format("%.2f",sizeLong)
            val fileName = item.name
            var iv_file = helper.getView<ImageView>(R.id.iv_file)

            if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                val uri = Uri.fromFile(file)
                iv_picture.setImageURI(uri)
                iv_picture.visibility = View.VISIBLE
                fileRoot.visibility = View.GONE
                pic_size.visibility = View.VISIBLE
                pic_size.setText(size.toString()  +" KB")
            }else{
                iv_picture.visibility = View.GONE
                fileRoot.visibility = View.VISIBLE
                pic_size.visibility = View.GONE
                var file_name = helper.getView<TextView>(R.id.file_name)
                var file_size = helper.getView<TextView>(R.id.file_size)
                file_name.text = fileName
                file_size.setText(size.toString()  +" KB")
                if (fileName.contains("pdf")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.pdf))
                } else if (fileName.contains("mp4")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.video))
                }  else if (fileName.contains("txt")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.txt))
                } else if (fileName.contains("ppt")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.ppt))
                } else if (fileName.contains("xls")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.xls))
                } else if (fileName.contains("doc")) {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.doc))
                } else {
                    iv_file.setImageDrawable( mContext.getResources().getDrawable(R.mipmap.other))
                }
            }
        }else{
            iv_picture.visibility = View.GONE
            iv_add.visibility = View.VISIBLE
            fileRoot.visibility = View.GONE
            helper.addOnClickListener(R.id.iv_add)
            pic_size.visibility = View.GONE

        }
    }

}