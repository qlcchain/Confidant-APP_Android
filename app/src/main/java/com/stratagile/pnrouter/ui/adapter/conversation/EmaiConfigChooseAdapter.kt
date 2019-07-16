package com.stratagile.pnrouter.ui.adapter.conversation

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pawegio.kandroid.loadAnimation
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.EmailConfigEntity
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.MenuItemView
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ImageButtonWithText
import com.stratagile.pnrouter.view.SmoothCheckBox
import java.util.*

class EmaiConfigChooseAdapter(arrayList: MutableList<EmailConfigEntity>) : BaseQuickAdapter<EmailConfigEntity, BaseViewHolder>(R.layout.activity_menu_item, arrayList) {
    override fun convert(helper: BaseViewHolder?, item: EmailConfigEntity?, payloads: MutableList<Any>) {
        KLog.i("")
    }

    var isChooseMode = false
    override fun convert(helper: BaseViewHolder, item: EmailConfigEntity) {
        var menuItemView = helper.getView<MenuItemView>(R.id.itemContent)
        menuItemView.setText(item.account)

        var choose = helper.getView<TextView>(R.id.choose)
        if(item.isChoose )
        {
            choose.visibility = View.VISIBLE
        }else{
            choose.visibility = View.INVISIBLE
        }

    }

}