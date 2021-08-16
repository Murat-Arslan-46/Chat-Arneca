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
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = MainAdapter (this::openChat,this::deleteChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
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

    override fun onResume() {
        super.onResume()
        update()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(){
        viewModel.getChatList().observe(requireActivity(), {
            val list = it.sortedByDescending { chat ->
                chat.lastDate
            }
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        })
    }

    private fun openChat(chat: EntityChat){
        viewModel.setChat(chat)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }

    private fun deleteChat(index: Int):Boolean{
        viewModel.deleteChat(adapter.currentList[index])
        return true
    }
}