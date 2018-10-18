package com.message.widget.presenter

import android.view.View
import android.widget.TextView
import com.message.adapter.MessageListAdapter
import com.stratagile.pnrouter.R
import com.message.Message

class TextRecvChatRowPresenter(itemView : View, message: Message, position : Int, adapter: MessageListAdapter) : ChatRowPresenter(itemView, message, position, adapter) {

    var tv_chatcontent : TextView
    init {
        init()
        tv_chatcontent = itemView.findViewById(R.id.tv_chatcontent)
        tv_chatcontent.text = message.msg
    }

}