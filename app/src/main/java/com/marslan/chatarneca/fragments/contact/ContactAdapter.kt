package com.marslan.chatarneca.fragments.contact

import android.annotation.SuppressLint
import android.graphics.Typeface.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.databinding.ItemChatListBinding
import com.marslan.chatarneca.databinding.ItemUserListBinding

class ContactAdapter (
    private val clickListener: (EntityUser) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private val selectedList = arrayListOf<EntityUser>()
        var currentList = listOf<EntityUser>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemUserListBinding.inflate(inflate, parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ContactViewHolder).bind(position)
    }

    inner class ContactViewHolder(private val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = currentList[position]
            binding.userItemDesc.text = user.mail
            binding.userItemName.text = user.name
            binding.root.setOnClickListener {
                if(selectedList.none { user == it }){
                    selectedList.add(user)
                    binding.userItemDesc.setTypeface(binding.userItemName.typeface, BOLD)
                }
                else{
                    selectedList.remove(user)
                    binding.userItemDesc.setTypeface(binding.userItemName.typeface, NORMAL)
                }
                clickListener(user)
            }
        }
    }

    override fun getItemCount() = currentList.size
    @SuppressLint("NotifyDataSetChanged")
    fun setCurrentList(users: List<EntityUser>) {
        currentList = users
        notifyDataSetChanged()
    }
    fun getSelectedList() = selectedList
    fun clearSelected(){ selectedList.clear() }
}