package com.marslan.chatarneca.fragments.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.FragmentGroupInfoBinding
import com.marslan.chatarneca.fragments.main.contact.ContactAdapter

class GroupInfoFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentGroupInfoBinding
        private lateinit var viewModel: SharedViewModel
        private var switch: Boolean = false
        private lateinit var adapter: ContactAdapter
        private lateinit var chat: EntityChat
        private lateinit var users: List<EntityUser>
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupInfoBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        chat = viewModel.getCurrentChat()!!
        adapter = ContactAdapter(arrayListOf(),this::onClick)
        binding.apply {
            groupInfoNameInput.setText(chat.name)
            groupInfoDescInput.setText(chat.description)
            groupInfoChangeNameBtn.setOnClickListener { editName() }
            groupInfoChangeDescBtn.setOnClickListener { editDescription() }
            groupInfoAddUserBtn.setOnClickListener { update() }
            groupInfoUsrlist.adapter = adapter
        }
        users = listOf()
        viewModel.getUsers().observe(requireActivity(),{list->
            val allUsers = arrayListOf<EntityUser>()
            list.forEach { allUsers.add(it) }
            users = allUsers
            update()
        })
        if((chat.users.split("%").size <= 2) || !chat.manager){
            binding.apply {
                groupInfoAddUser.visibility = View.GONE
                groupInfoAddUserBtn.visibility = View.GONE
                groupInfoRemoveUser.visibility = View.GONE
            }
        }
        return (binding.root)
    }

    private fun editDescription() {
        chat.description = binding.groupInfoDescInput.text.toString()
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"change description ${chat.name}",Toast.LENGTH_SHORT).show()
    }
    private fun editName(){
        chat.name = binding.groupInfoNameInput.text.toString()
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"change name ${chat.name}",Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun update(){
        switch = binding.groupInfoAddUserBtn.isChecked
        val chatUsers = chat.users.split("%")
        if(!switch){
            val currentList = arrayListOf<EntityUser>()
            chatUsers.forEach { user ->
                currentList.add(users.filter { it.id == user }[0])
            }
            adapter.currentList = currentList
        }
        else{
            val currentList = arrayListOf<EntityUser>()
            users.forEach { currentList.add(it) }
            chatUsers.forEach { user ->
                currentList.remove(users.filter { it.id == user }[0])
            }
            adapter.currentList = currentList
        }
        adapter.notifyDataSetChanged()
    }

    private fun deleteUser(id: String) {
        val toID = chat.users.split("%")
        var temp = viewModel.getAuth().currentUser!!.uid
        toID.forEach {to_ID->
            if(to_ID != id && toID.none { it == id })
                temp += "%$id"
        }
        chat.users = temp
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"delete user $id",Toast.LENGTH_SHORT).show()
    }
    private fun addUser(id: String) {
        chat.users += "%$id"
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"add user $id",Toast.LENGTH_SHORT).show()
    }
    private fun onClick(id: String){
        if(chat.manager) {
            if (switch)
                addUser(id)
            else
                deleteUser(id)
        }
        else{
            viewModel.setCurrentUser(id)
            findNavController().navigate(R.id.action_groupInfoFragment_to_userFragment)
        }
    }
}