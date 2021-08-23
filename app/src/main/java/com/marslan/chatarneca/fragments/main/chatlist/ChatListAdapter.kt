package com.marslan.chatarneca.fragments.main.chatlist

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(
    var currentList: List<EntityMessage>,
    var chatList: List<EntityChat>,
    private val clickListener: (EntityChat) -> Unit,
    private val longClickListener: (EntityChat) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemChatListBinding.inflate(inflate, parent, false)
        return ChatViewHolder(binding)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatViewHolder).bind(position, clickListener, longClickListener)
    }

    inner class ChatViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(position: Int, clickListener: (EntityChat) -> Unit, longClickListener: (EntityChat) -> Boolean) {
            val message = currentList[position]
            var chat = EntityChat()
            chatList.forEach {
                if(it.id == message.chatID){
                    chat = it
                }
            }
            if(chat.users.split("%").size > 2)
                binding.chatImage.setImageResource(R.drawable.ic_list_group)
            val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
            val date = sdf.format(Date())
            val currentDate = date.split(" ")
            val messageDate = message.date.split(" ")
            binding.chatDate.text =
                if(messageDate[0] == currentDate[0])
                    messageDate[1]
                else
                    messageDate[0]
            binding.chatText.text = message.text
            if(!message.iSaw)
                binding.chatText.setTypeface(binding.chatName.typeface,Typeface.BOLD)
            else
                binding.chatText.setTypeface(binding.chatName.typeface,Typeface.NORMAL)
            binding.chatName.text = chat.chatName
            binding.root.setOnClickListener { clickListener(chat) }
            binding.root.setOnLongClickListener { longClickListener(chat) }
        }
    }
}