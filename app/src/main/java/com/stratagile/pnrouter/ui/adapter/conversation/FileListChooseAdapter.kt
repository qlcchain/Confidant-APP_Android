package com.stratagile.pnrouter.ui.adapter.conversation

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pawegio.kandroid.loadAnimation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.TimeUtil
import com.stratagile.pnrouter.view.SmoothCheckBox
import java.util.*

class FileListChooseAdapter(arrayList: ArrayList<JPullFileListRsp.ParamsBean.PayloadBean>) : BaseQuickAdapter<JPullFileListRsp.ParamsBean.PayloadBean, BaseViewHolder>(R.layout.layout_choose_file_list_item, arrayList) {
    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: JPullFileListRsp.ParamsBean.PayloadBean) {
        helper.addOnClickListener(R.id.fileOpreate)
        var checkBox = helper.getView<SmoothCheckBox>(R.id.checkBox)
        helper.setText(R.id.tvFileTime, TimeUtil.getFileListTime(item.timestamp.toLong()))
        if(isChooseMode) {
            checkBox.visibility = View.VISIBLE
            checkBox.animation = mContext.loadAnimation(R.anim.file_select_in)
        } else {
            checkBox.visibility = View.GONE
            KLog.i(item.fileName.substring(item.fileName.lastIndexOf("/") + 1))
            helper.setText(R.id.tvFileName, String(Base58.decode(item.fileName.substring(item.fileName.lastIndexOf("/") + 1))))
        }
    }

}