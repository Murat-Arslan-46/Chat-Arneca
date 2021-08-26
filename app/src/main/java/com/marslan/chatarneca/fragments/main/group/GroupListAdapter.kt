package com.marslan.chatarneca.fragments.main.group

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.databinding.ItemGroupListBinding

class GroupListAdapter(
    private val onClickListener: (EntityChat) -> Unit,
    private val onLongClickListener: (EntityChat) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object{
        private var currentList = arrayListOf<EntityChat>()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemGroupListBinding.inflate(inflate,parent,false)
        return GroupListViewHolder(binding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GroupListViewHolder).bind(currentList[position])
    }
    override fun getItemCount() = currentList.size
    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentList(list: List<EntityChat>){
        currentList = list as ArrayList<EntityChat>
        notifyDataSetChanged()
    }
    inner class GroupListViewHolder(val binding: ItemGroupListBinding)
        :RecyclerView.ViewHolder(binding.root){
            fun bind(chat: EntityChat){
                binding.apply{
                    groupListName.text = chat.name
                    groupListUsers.text = chat.users.split("%").size.toString()
                    groupListDescription.text = chat.description
                    groupListIcon.setImageResource(R.drawable.ic_list_group)
                    root.setOnClickListener { onClick(chat) }
                    root.setOnLongClickListener { onLongClick(chat) }
                }
            }
        private fun onLongClick(chat: EntityChat):Boolean{
            onLongClickListener(chat)
            return true
        }
        private fun onClick(chat: EntityChat){
            onClickListener(chat)
        }
    }

}