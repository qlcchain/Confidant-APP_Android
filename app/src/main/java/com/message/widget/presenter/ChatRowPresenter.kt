package com.message.widget.presenter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.message.Message
import com.message.UserProvider
import com.message.adapter.MessageListAdapter
import com.message.widget.ChatRow.MessageListViewHolder
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.DateUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.ImageButtonWithText
import kotlinx.android.synthetic.main.activity_user_info.*
import java.util.*

abstract class ChatRowPresenter(var itemView : View, var message: Message, var position : Int, var adapter : MessageListAdapter) : MessageListViewHolder.ChatRowActionCallback {

    lateinit var llParent : LinearLayout
    lateinit var avatar : ImageButtonWithText
    lateinit var context: Context
    lateinit var tvTimestamp: TextView
    var userEntity: UserEntity? = null
    lateinit var progress_bar : ProgressBar

    /**
     * 统一的业务在这里处理，比如 头像的点击，是否显示时间，
     */
    fun init() {
        llParent = itemView.findViewById(R.id.llParent)
        avatar = itemView.findViewById(R.id.iv_userhead)
        tvTimestamp = itemView.findViewById(R.id.timestamp)
        userEntity = UserProvider.getInstance().getUserById(message.from)
        context = llParent.context
        llParent.setOnClickListener {
            hideSoftInput(llParent.context as Activity)
        }
        avatar.setOnClickListener {
            onAvatarClick(message)
        }
        tvTimestamp.text = DateUtil.getTimestampString(Date(message.timeStatmp), context)
        if (position == 0) {
            tvTimestamp.visibility = View.VISIBLE
        } else {
            if (message.timeStatmp - adapter.getData().get(position - 1).timeStatmp > 1000 * 10 * 60 * 5) {
                tvTimestamp.visibility = View.VISIBLE
            } else {
                tvTimestamp.visibility = View.GONE
            }
        }
        if (message.from.equals(SpUtil.getString(context, ConstantValue.userId, ""))) {
            progress_bar = itemView.findViewById(R.id.progress_bar)
           /* if (message.status == Message.Status.SUCCESS) {
                progress_bar.visibility = View.GONE
            } else if (message.status == Message.Status.CREATE) {
                progress_bar.visibility = View.VISIBLE
            }*/
        }
        var usernameSouce = String(RxEncodeTool.base64Decode(userEntity?.nickName))
        if (userEntity != null && userEntity?.remarks != null && userEntity?.remarks != "") {
            usernameSouce = String(RxEncodeTool.base64Decode(userEntity?.remarks))
        }
        avatar.setText(usernameSouce)
        var avatarPath = Base58.encode( RxEncodeTool.base64Decode(userEntity!!.signPublicKey))+".jpg"
        avatar.setImageFile(avatarPath)
    }

    private fun is24Time(context: Context): Boolean {
        val cv = context.contentResolver
        val strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24)
        return if (strTimeFormat == "24") {
            true
        } else false
    }

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    fun hideSoftInput(activity: Activity) {
        val view = activity.window.peekDecorView()
        if (view != null) {
            val inputmanger = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputmanger.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onResendClick(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBubbleClick(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBubbleLongClick(message: Message, view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDetachedFromWindow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onParentClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAvatarClick(message: Message) {
        context.toast("点击了头像，。。。")
    }

}
