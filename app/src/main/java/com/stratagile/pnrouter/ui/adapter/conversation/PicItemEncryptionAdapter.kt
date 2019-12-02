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
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.PicMenu
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.DrawableEnTextView
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import java.util.*

class PicItemEncryptionAdapter(arrayList: MutableList<LocalFileItem>) : BaseQuickAdapter<LocalFileItem, BaseViewHolder>(R.layout.layout_encryption_file_list_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: LocalFileItem?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: LocalFileItem) {
        /*helper.addOnClickListener(R.id.menuItem)
        helper.addOnClickListener(R.id.btnDelete)
        helper.addOnClickListener(R.id.btnRename)
        var userAvatar = helper.getView<DrawableEnTextView>(R.id.menuItem)
        userAvatar.setTitleText(item.fileName)
        userAvatar.setRightTitleText(item.fileNum.toString() +" "+mContext.getString(R.string.file_))*/
    }

}