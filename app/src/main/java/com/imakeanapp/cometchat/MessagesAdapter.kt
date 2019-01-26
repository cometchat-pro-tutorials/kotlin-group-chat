package com.imakeanapp.cometchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.models.BaseMessage
import com.cometchat.pro.models.TextMessage

class MessagesAdapter(private val uid: String,
                      private var messages: List<BaseMessage>)  : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    companion object {
        private const val SENT = 0
        private const val RECEIVED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = when (viewType) {
            SENT -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_sent, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_received, parent, false)
            }
        }
        return MessageViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender?.uid!!.contentEquals(uid)) {
            SENT
        } else {
            RECEIVED
        }
    }

    fun updateMessages(messages: List<BaseMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: BaseMessage) {
            if (message is TextMessage) {
                messageText.text = message.text
            }
        }
    }
}