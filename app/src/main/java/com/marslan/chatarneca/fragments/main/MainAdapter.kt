package com.marslan.chatarneca.fragments.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.data.Chat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.databinding.ItemChatListBinding

class MainAdapter(private val clickListener: (String) -> Unit ) :
    ListAdapter<Chat, RecyclerView.ViewHolder>(ItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemChatListBinding.inflate(inflate, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatViewHolder).bind(position, clickListener)
    }

    inner class ChatViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, clickListener: (String) -> Unit) {
            val chat = currentList[position]
            var msg : EntityMessage
            msg = if(chat.messageList.isNotEmpty())
                    chat.messageList[chat.messageList.size-1]
                else
                    EntityMessage(1,"","","0",chat.id.toInt())
            binding.chatText.text = msg.text
            binding.chatDate.text = msg.date
            binding.chatName.text = chat.name
            binding.root.setOnClickListener { clickListener(position.toString()) }
        }
    }
    private class ItemCallBack: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat) =
            oldItem.messageList == newItem.messageList
        override fun areContentsTheSame(oldItem: Chat, newItem: Chat) =
            oldItem == newItem

    }
}