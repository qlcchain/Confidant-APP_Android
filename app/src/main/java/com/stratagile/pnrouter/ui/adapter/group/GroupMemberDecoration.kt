package com.stratagile.pnrouter.ui.adapter.group

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.stratagile.pnrouter.R
import kotlinx.android.synthetic.main.layout_format.view.*

class GroupMemberDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter!!.itemCount
        if (position < count - 1) {
            outRect.left = 0
            outRect.right = parent.context.resources.getDimension(R.dimen.x17).toInt()
        } else {
            outRect.left = 0
            outRect.right = 0
        }

    }
}