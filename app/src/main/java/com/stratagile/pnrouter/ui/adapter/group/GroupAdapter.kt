package com.stratagile.pnrouter.ui.adapter.group

import android.content.Intent
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.EaseConstant.EXTRA_CHAT_GROUP
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.entity.events.SelectGroupChange
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.FireBaseUtils
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.ImageButtonWithText
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

class GroupAdapter (arrayList: List<GroupEntity>) : BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.item_group_layout, arrayList) {

    private var slelctMap =  HashMap<String, Boolean>()
    private var isCheckMode = false

    fun isCheckMode(): Boolean {
        return isCheckMode
    }

    fun setCheckMode(checkMode: Boolean) {
        isCheckMode = checkMode
    }
    override fun convert(helper: BaseViewHolder?, item: GroupEntity?, payloads: MutableList<Any>) {

    }

    override fun convert(helper: BaseViewHolder, item: GroupEntity) {
        helper.setGone(R.id.checkBox, isCheckMode)
        val imageButtonWithText = helper.getView<ImageButtonWithText>(R.id.groupIcon)
        val checkBox = helper.getView<CheckBox>(R.id.checkBox)
        helper.setText(R.id.groupName,String(RxEncodeTool.base64Decode(item.gName)))
        imageButtonWithText.setImage(R.mipmap.group_head)
            helper.itemView.setOnClickListener {
                if (isCheckMode) {
                    helper.setChecked(R.id.checkBox, !checkBox.isChecked)
                    slelctMap.put(item.gId,checkBox.isChecked)
                    getSelectedCount()
                }else{
                    FireBaseUtils.logEvent(mContext, FireBaseUtils.FIR_CHAT_SEND_GROUP_TEXT)
                    val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, item.gId.toString())
                    intent.putExtra(EXTRA_CHAT_GROUP, item)
                    UserDataManger.currentGroupData = item
                    mContext.startActivity(intent)
                }

            }

    }

    private fun getSelectedCount() {

        var count = 0;
        for (item in slelctMap.values)
        {
            if(item)
            {
                count ++;
            }
        }
        EventBus.getDefault().post(SelectGroupChange(count))
    }
}