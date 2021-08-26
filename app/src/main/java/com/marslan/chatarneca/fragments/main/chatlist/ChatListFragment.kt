package com.marslan.chatarneca.fragments.main.chatlist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private lateinit var adapter: ChatListAdapter
    private lateinit var binding: FragmentChatListBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var auth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater, container, false)

        adapter = ChatListAdapter (listOf(), listOf(),this::openChat,this::deleteChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        auth = viewModel.getAuth()
        viewModel.getMessageLastForChatList().observe(viewLifecycleOwner,{
            adapter.currentList = it
            adapter.notifyDataSetChanged()
        })
        viewModel.getAllChat().observe(viewLifecycleOwner,{
            adapter.chatList = it
            adapter.notifyDataSetChanged()
        })
        binding.mainChatList.adapter = adapter
        return (binding.root)
    }

    private fun openChat(chat: EntityChat){
        viewModel.setCurrentChat(chat)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }

    private fun deleteChat(chat: EntityChat):Boolean{
        viewModel.deleteChat(chat)
        return true
    }
}