package com.marslan.chatarneca.fragments.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.data.SharedRepository
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.databinding.ItemChatListBinding
import java.security.acl.Owner

class MainAdapter(private val clickListener: (EntityChat) -> Unit ) :
    ListAdapter<EntityChat, RecyclerView.ViewHolder>(ItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemChatListBinding.inflate(inflate, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatViewHolder).bind(currentList[position], clickListener)
    }

    inner class ChatViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: EntityChat, clickListener: (EntityChat) -> Unit) {
            binding.chatDate.text = chat.lastDate
            binding.chatText.text = chat.lastMessage
            binding.chatName.text = chat.chatName
            binding.root.setOnClickListener { clickListener(chat) }
        }
    }
    private class ItemCallBack: DiffUtil.ItemCallback<EntityChat>() {
        override fun areItemsTheSame(oldItem: EntityChat, newItem: EntityChat) =
            oldItem.chatID == newItem.chatID
        override fun areContentsTheSame(oldItem: EntityChat, newItem: EntityChat) =
            oldItem == newItem

    }
}