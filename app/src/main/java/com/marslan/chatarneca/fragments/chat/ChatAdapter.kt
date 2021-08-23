package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.databinding.ItemMessageReceiveBinding
import com.marslan.chatarneca.databinding.ItemMessageSendBinding

class ChatAdapter(
    var currentList: List<EntityMessage>,
    val selectedList: List<EntityMessage>,
    private val isNotGroup: Boolean,
    private val fromID: String,
    private val onClick: (EntityMessage) -> Unit,
    private val onLongClick: (EntityMessage) -> Boolean
    )
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        val SEND = 1
        val RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        if(viewType == RECEIVE){
            val binding = ItemMessageReceiveBinding.inflate(inflate,parent,false)
            return ReceiveMessageViewHolder(binding)
        }
        val binding = ItemMessageSendBinding.inflate(inflate,parent,false)
        return SendMessageViewHolder(binding)
    }
    override fun getItemCount() = currentList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(currentList[position].fromID == fromID){
            (holder as SendMessageViewHolder).bind(currentList[position])
        }
        else{
            (holder as ReceiveMessageViewHolder).bind(position)
        }
    }
    override fun getItemViewType(position: Int): Int {
    return  if(currentList[position].fromID == fromID)
                SEND
            else
                RECEIVE
    }

    inner class SendMessageViewHolder(private val binding: ItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(message: EntityMessage){
                binding.message = message
                binding.root.setOnLongClickListener {
                    if(selectedList.none { it == message })
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                    else
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                    onLongClick(message)
                }
                binding.root.setOnClickListener {
                    if(selectedList.isNotEmpty()){
                        if(selectedList.none { it == message })
                            binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        else
                            binding.root.setBackgroundColor(Color.TRANSPARENT)
                    }
                    onClick(message)
                }
                if(message.send)
                    binding.imageView.visibility = View.VISIBLE
            }
        }

    inner class ReceiveMessageViewHolder(private val binding: ItemMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(position: Int){
                val message = currentList[position]
                binding.message = message
                if(isNotGroup)
                    binding.receiveFromText.visibility = View.GONE
                else if(position != 0 && message.fromID == currentList[position-1].fromID)
                    binding.receiveFromText.visibility = View.GONE
                else
                    binding.receiveFromText.visibility = View.VISIBLE
                binding.root.setOnLongClickListener {
                    if(selectedList.isEmpty()){
                        if (selectedList.none { it == message })
                            binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        else
                            binding.root.setBackgroundColor(Color.TRANSPARENT)
                    }
                    onLongClick(message)
                }
                binding.root.setOnClickListener {
                    if(selectedList.isNotEmpty()){
                        if(selectedList.none { it == message })
                            binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        else
                            binding.root.setBackgroundColor(Color.TRANSPARENT)
                    }
                    onClick(message)
                }
            }
        }
}