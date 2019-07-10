package com.stratagile.pnrouter.ui.adapter.conversation

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

class EmailListChooseAdapter(arrayList: MutableList<JPullFileListRsp.ParamsBean.PayloadBean>) : BaseQuickAdapter<JPullFileListRsp.ParamsBean.PayloadBean, BaseViewHolder>(R.layout.layout_choose_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: JPullFileListRsp.ParamsBean.PayloadBean?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: JPullFileListRsp.ParamsBean.PayloadBean) {
        helper.addOnClickListener(R.id.fileOpreate)
        helper.addOnClickListener(R.id.sendFriend)
        //helper.addOnClickListener(R.id.ivDownload)
//        var checkBox = helper.getView<SmoothCheckBox>(R.id.checkBox)
        helper.setText(R.id.tvFileTime, TimeUtil.getFileListTime(item.timestamp.toLong()))
//        KLog.i("文件的名字为：" + item.fileName)
        var fileName = ""
        try {
            var subName = item.fileName
            fileName = String(Base58.decode(subName))
        }catch (e :Exception)
        {

        }
        helper.setText(R.id.tvFileName, fileName)
        helper.setText(R.id.tvFileSize, NetUtils.parseSize(item.fileSize.toLong()))
        var userAvatar = helper.getView<ImageButtonWithText>(R.id.userAvatar)

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
        //1-用户发出的文件
        //2-用户接收的文件
        //3-用户上传的文件
        helper.setText(R.id.friendName, "")
        //1 自己发的， 2 收到的， 3自己上传的
        when(item.fileFrom) {
            1 -> {
                helper.setImageDrawable(R.id.fileForm, mContext.resources.getDrawable(R.mipmap.documents_i_share))
                helper.setText(R.id.friendName, String(RxEncodeTool.base64Decode(item.sender)))
                userAvatar.setText(String(RxEncodeTool.base64Decode(item.sender)))
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.SenderKey))+".jpg"
                userAvatar.setImageFile(fileBase58Name)
                helper.setText(R.id.tvOpreateType, "File Sent")
            }
            2 -> {
                helper.setImageDrawable(R.id.fileForm, mContext.resources.getDrawable(R.mipmap.documents_received))
                helper.setText(R.id.friendName, String(RxEncodeTool.base64Decode(item.sender)))
                userAvatar.setText(String(RxEncodeTool.base64Decode(item.sender)))
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.SenderKey))+".jpg"
                userAvatar.setImageFile(fileBase58Name)
                helper.setText(R.id.tvOpreateType, "File Received")
            }
            3 -> {
                helper.setImageDrawable(R.id.fileForm, mContext.resources.getDrawable(R.mipmap.upload_h))
                helper.setText(R.id.friendName, SpUtil.getString(mContext, ConstantValue.username, ""))
                userAvatar.setText(String(RxEncodeTool.base64Decode(item.sender)))
                var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(item.SenderKey))+".jpg"
                userAvatar.setImageFile(fileBase58Name)
                helper.setText(R.id.tvOpreateType, "My File")
            }
        }
    }

}