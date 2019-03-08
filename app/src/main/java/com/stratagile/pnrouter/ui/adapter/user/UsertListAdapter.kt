package com.stratagile.pnrouter.ui.adapter.user

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.JPullUserRsp
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class UsertListAdapter(arrayList: ArrayList<RouterUserEntity>, isSelect: Boolean) : BaseQuickAdapter<RouterUserEntity, BaseViewHolder>(R.layout.layout_user_list_item, arrayList) {
    var isSelect = isSelect;
    var helperItem :BaseViewHolder? = null;
    override fun convert(helper: BaseViewHolder?, item: RouterUserEntity?) {
        helperItem = helper;


        if(!item!!.nickName.equals(""))
        {
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.nickName))
            helper!!.setText(R.id.tvNickName,nickNameSouce)
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.mnemonic))
            helper!!.setText(R.id.tvNickName,nickNameSouce)
        }

        var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
        if (item!!.nickName != null && !item!!.nickName.equals("")) {
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.nickName))
            imagebutton.setText(nickNameSouce)
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.mnemonic))
            imagebutton.setText(nickNameSouce)
        }

    }
     fun setCheckBox(positon:Int)
    {
        helperItem!!.setChecked(R.id.checkBox,!helperItem!!.getView<CheckBox>(R.id.checkBox).isChecked)
    }
}