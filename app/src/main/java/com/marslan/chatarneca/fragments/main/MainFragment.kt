package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.LoginActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: SharedViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = MainAdapter (listOf(), listOf(),this::openChat,this::deleteChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
        viewModel.getAllChatWithLastMessage().observe(viewLifecycleOwner,{
            adapter.currentList = it
            adapter.notifyDataSetChanged()
        })
        viewModel.getAllChat().observe(viewLifecycleOwner,{
            adapter.chatList = it
            adapter.notifyDataSetChanged()
        })
        binding.newChat.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
        }
        binding.mainSignOut.setOnClickListener {
            auth.signOut()
            val activity = Intent(context,LoginActivity::class.java)
            requireActivity().startActivity(activity)
            requireActivity().finish()
        }
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