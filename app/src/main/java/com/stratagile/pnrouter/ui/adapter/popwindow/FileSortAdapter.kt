package com.stratagile.pnrouter.ui.adapter.popwindow

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.FileOpreateType

class FileSortAdapter(arrayList: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_file_sort_item, arrayList) {
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tvSortType, item)
    }

}