package com.stratagile.pnrouter.ui.adapter.conversation

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pawegio.kandroid.loadAnimation
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.view.SmoothCheckBox
import java.util.*

class FileListChooseAdapter(arrayList: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_choose_file_list_item, arrayList) {
    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.addOnClickListener(R.id.fileOpreate)
        var checkBox = helper.getView<SmoothCheckBox>(R.id.checkBox)

        if(isChooseMode) {
            checkBox.visibility = View.VISIBLE
            checkBox.animation = mContext.loadAnimation(R.anim.file_select_in)
        } else {
            checkBox.visibility = View.GONE
        }
    }

}