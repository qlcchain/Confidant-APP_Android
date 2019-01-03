package com.stratagile.pnrouter.ui.adapter.user

import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText

class ContactListAdapter(arrayList: ArrayList<UserEntity>,isSelect: Boolean) : BaseQuickAdapter<UserEntity, BaseViewHolder>(R.layout.layout_contact_list_item, arrayList) {
    var isSelect = isSelect;
    var helperItem :BaseViewHolder? = null;
    override fun convert(helper: BaseViewHolder?, item: UserEntity?) {
        helperItem = helper;


        if(item!!.remarks == null || item!!.remarks.equals(""))
        {
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.nickName))
            //var nickNameSouce = item!!.nickName
            helper!!.setText(R.id.tvNickName,nickNameSouce)
            var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
            if (nickNameSouce != null) {
                imagebutton.setText(nickNameSouce)
            }
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(item!!.remarks))
            //var nickNameSouce = item!!.nickName
            helper!!.setText(R.id.tvNickName,nickNameSouce)
            var imagebutton = helper!!.getView<ImageButtonWithText>(R.id.ivAvatar)
            if (nickNameSouce != null) {
                imagebutton.setText(nickNameSouce)
            }
        }
        try {
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(item)
        }catch (e: Exception)
        {

        }
        helper!!.addOnClickListener(R.id.tvRefuse)
        helper!!.addOnClickListener(R.id.tvAccept)
        helper!!.setGone(R.id.checkBox,isSelect)
    }
     fun setCheckBox(positon:Int)
    {
        helperItem!!.setChecked(R.id.checkBox,!helperItem!!.getView<CheckBox>(R.id.checkBox).isChecked)
    }
}