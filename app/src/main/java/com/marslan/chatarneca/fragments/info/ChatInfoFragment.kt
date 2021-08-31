package com.marslan.chatarneca.fragments.info

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.databinding.FragmentChatInfoBinding
import com.marslan.chatarneca.fragments.contact.ContactAdapter

class ChatInfoFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentChatInfoBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var adapter: ContactAdapter
        private lateinit var chat: EntityChat
        private lateinit var users: List<EntityUser>
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatInfoBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        chat = viewModel.getCurrentChat()!!
        adapter = ContactAdapter(this::onClick)
        binding.apply {
            chatInfoNameInput.setText(chat.name)
            chatInfoDescInput.setText(chat.description)
            chatInfoAddUserBtn.setOnClickListener { update() }
            chatInfoUsrlist.adapter = adapter
            if(!chat.manager)
                chatInfoManager.visibility = View.GONE
            chatInfoManager.setOnClickListener { managerMode() }
            if(chat.toRef != chat.id.toString())
                chatInfoLeave.visibility = View.GONE
            chatInfoLeave.setOnClickListener { leaveChat() }
        }
        users = listOf()
        viewModel.getUsers().observe(requireActivity(),{list->
            val allUsers = arrayListOf<EntityUser>()
            list.forEach { allUsers.add(it) }
            users = allUsers
            update()
        })
        managerMode()
        requireActivity().title = chat.name
        return (binding.root)
    }
    private fun leaveChat(){
        viewModel.leaveGroup(chat)
    }
    private fun managerMode() {
        if(binding.chatInfoManager.isChecked){
            binding.apply {
                chatInfoNameInput.isEnabled = true
                chatInfoDescInput.isEnabled = true
                chatInfoAddUser.visibility = View.VISIBLE
                chatInfoAddUserBtn.visibility = View.VISIBLE
                chatInfoRemoveUser.visibility = View.VISIBLE
            }
        }
        else{
            binding.apply {
                chatInfoNameInput.isEnabled = false
                chatInfoDescInput.isEnabled = false
                chatInfoAddUser.visibility = View.GONE
                chatInfoAddUserBtn.visibility = View.GONE
                chatInfoRemoveUser.visibility = View.GONE
            }
            if(chat.description != binding.chatInfoDescInput.text.toString()){
                chat.description = binding.chatInfoDescInput.text.toString()
                viewModel.updateChat(chat)
                Toast.makeText(
                    requireContext(),
                    "change description ${chat.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if(chat.name != binding.chatInfoNameInput.text.toString()){
                chat.name = binding.chatInfoNameInput.text.toString()
                viewModel.updateChat(chat)
                Toast.makeText(
                    requireContext(),
                    "change name ${chat.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun update(){
        val chatUsers = chat.users.split("%")
        if(!binding.chatInfoAddUserBtn.isChecked){
            val currentList = arrayListOf<EntityUser>()
            chatUsers.forEach { user ->
                currentList.add(users.filter { it.id == user }[0])
            }
            adapter.setCurrentList(currentList)
        }
        else{
            val currentList = arrayListOf<EntityUser>()
            users.forEach { currentList.add(it) }
            chatUsers.forEach { user ->
                currentList.remove(users.filter { it.id == user }[0])
            }
            adapter.setCurrentList(currentList)
        }
    }

    private fun deleteUser(id: String) {
        if( id != viewModel.getAuth().currentUser!!.uid) {
            val beforeUsers = chat.users.split("%")
            var afterUsers = viewModel.getAuth().uid.toString()
            beforeUsers.forEach {
                if (it != id && it != viewModel.getAuth().uid.toString())
                    afterUsers += "%$it"
            }
            chat.users = afterUsers
            viewModel.updateChat(chat)
            val message = EntityMessage(
                text = "${chat.name}%${chat.description}%${chat.imageSrc}",
                date = chat.users,
                fromID = "-1",
                chatID = chat.id,
                ref = "-2"
            )
            viewModel.getFirebaseDatabase().getReference(id).push().setValue(message)
            message.ref = "-1"
            afterUsers.split("%").forEach {
                if(it != viewModel.getAuth().uid.toString())
                    viewModel.getFirebaseDatabase().getReference(it).push().setValue(message)
            }
            Toast.makeText(requireContext(), "delete user $id", Toast.LENGTH_SHORT).show()
        }
    }
    private fun addUser(id: String) {
        chat.users += "%$id"
        viewModel.updateChat(chat)
        val message = EntityMessage(
            text = "${chat.name}%${chat.description}%${chat.imageSrc}",
            date = chat.users,
            fromID = "-1",
            chatID = chat.id,
            ref = "-1"
        )
        chat.users.split("%").forEach {
            if(it != viewModel.getAuth().uid.toString())
                viewModel.getFirebaseDatabase().getReference(it).push().setValue(message)
        }
        Toast.makeText(requireContext(),"add user $id",Toast.LENGTH_SHORT).show()
    }
    private fun onClick(user: EntityUser) {
        val id = user.id
        if(binding.chatInfoManager.isChecked) {
            if (binding.chatInfoAddUserBtn.isChecked)
                addUser(id)
            else
                deleteUser(id)
        }
        else{
            viewModel.setCurrentUser(id)
            findNavController().navigate(R.id.action_chatInfoFragment_to_userFragment)
        }
    }
}