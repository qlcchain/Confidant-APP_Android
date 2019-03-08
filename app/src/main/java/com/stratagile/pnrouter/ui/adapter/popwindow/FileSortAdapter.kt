package com.stratagile.pnrouter.ui.adapter.popwindow

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.entity.file.Arrange
import com.stratagile.pnrouter.entity.file.FileOpreateType

class FileSortAdapter(arrayList: ArrayList<Arrange>) : BaseQuickAdapter<Arrange, BaseViewHolder>(R.layout.layout_file_sort_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: Arrange?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    override fun convert(helper: BaseViewHolder, item: Arrange) {
        helper.setText(R.id.tvSortType, item.name)
        helper.setVisible(R.id.ivAvatar, item.isSelect)
    }

}