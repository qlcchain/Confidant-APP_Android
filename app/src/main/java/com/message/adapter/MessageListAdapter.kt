package com.message.adapter

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.message.Message
import com.message.widget.ChatRow.MessageListViewHolder
import com.message.widget.presenter.TextRecvChatRowPresenter
import com.message.widget.presenter.TextSendChatRowPresenter
import com.stratagile.pnrouter.R

class MessageListAdapter(private val context: Context, private var data: ArrayList<Message>) : RecyclerView.Adapter<MessageListViewHolder>() {

    private val MESSAGE_TYPE_RECV_TXT = 0
    private val MESSAGE_TYPE_SENT_TXT = 1
    private val MESSAGE_TYPE_SENT_IMAGE = 2
    private val MESSAGE_TYPE_SENT_LOCATION = 3
    private val MESSAGE_TYPE_RECV_LOCATION = 4
    private val MESSAGE_TYPE_RECV_IMAGE = 5
    private val MESSAGE_TYPE_SENT_VOICE = 6
    private val MESSAGE_TYPE_RECV_VOICE = 7
    private val MESSAGE_TYPE_SENT_VIDEO = 8
    private val MESSAGE_TYPE_RECV_VIDEO = 9
    private val MESSAGE_TYPE_SENT_FILE = 10
    private val MESSAGE_TYPE_RECV_FILE = 11
    private val MESSAGE_TYPE_SENT_EXPRESSION = 12
    private val MESSAGE_TYPE_RECV_EXPRESSION = 13

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MessageListViewHolder {
        var itemView: View
        if (viewType == 0) {
            itemView = LayoutInflater.from(context).inflate(R.layout.ease_row_received_message, parent, false)
            return MessageListViewHolder(itemView)
        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.ease_row_sent_message, parent, false)
            return MessageListViewHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var message = data.get(position)
        //1为发送，0为接受
        when (message.type) {
            Message.Type.TXT -> {
                if (message.itemType == 0) {
                    return MESSAGE_TYPE_RECV_TXT
                } else {
                    return MESSAGE_TYPE_SENT_TXT
                }
            }
        }
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(@NonNull holder: MessageListViewHolder, position: Int) {
        when (holder.itemViewType) {
            MESSAGE_TYPE_RECV_TXT -> {
                holder.setItemChatRowPresenter(TextRecvChatRowPresenter(holder.itemView, data.get(position), position, this))
            }
            MESSAGE_TYPE_SENT_TXT -> {
                holder.setItemChatRowPresenter(TextSendChatRowPresenter(holder.itemView, data.get(position), position, this))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setNewData(newData: List<Message>) {
        this.data = if (data == null) ArrayList<Message>() else data
        notifyDataSetChanged()
    }

    fun getData(): List<Message> {
        return data
    }

    fun addData(data: Message) {
        this.data.add(data)
        notifyItemInserted(this.data.size)
        compatibilityDataSizeChanged(1)
    }

    private fun compatibilityDataSizeChanged(size: Int) {
        val dataSize = if (data == null) 0 else data.size
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }
}
