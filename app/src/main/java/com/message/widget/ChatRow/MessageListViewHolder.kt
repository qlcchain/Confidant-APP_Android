package com.message.widget.ChatRow

import android.support.v7.widget.RecyclerView
import android.view.View
import com.message.Message
import com.message.widget.presenter.ChatRowPresenter

class MessageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var chatRowPresenter : ChatRowPresenter

    fun setItemChatRowPresenter(chatRowPresenter : ChatRowPresenter) {
        this.chatRowPresenter = chatRowPresenter
    }
    interface ChatRowActionCallback {
        abstract fun onResendClick(message: Message)

        abstract fun onBubbleClick(message: Message)

        abstract fun onBubbleLongClick(message: Message, view: View)

        abstract fun onDetachedFromWindow()

        abstract fun onParentClick()

        abstract fun onAvatarClick(message: Message)
    }
}