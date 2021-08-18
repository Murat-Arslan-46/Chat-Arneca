package com.marslan.chatarneca.fragments.contact

import android.graphics.Typeface
import android.graphics.Typeface.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.ItemChatListBinding

class ContactAdapter (
    var currentList: List<User>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        fun bind(user: User, clickListener: (String) -> Unit) {
            binding.chatDate.text = ""
            binding.chatText.text = user.mail
            binding.chatName.text = user.name
            binding.root.setOnClickListener {
                if(binding.chatName.typeface.isBold)
                    binding.chatName.typeface = DEFAULT
                else
                    binding.chatName.typeface = DEFAULT_BOLD
                clickListener(user.id)
            }
        }
    }

    override fun getItemCount() = currentList.size
}