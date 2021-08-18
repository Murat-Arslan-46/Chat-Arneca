package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.LoginActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var auth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = MainAdapter (listOf(), listOf(),this::openChat,this::deleteChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        auth = viewModel.getAuth()
        viewModel.getAllChatWithLastMessage().observe(viewLifecycleOwner,{
            adapter.currentList = it
            adapter.notifyDataSetChanged()
        })
        viewModel.getAllChat().observe(viewLifecycleOwner,{
            adapter.chatList = it
            adapter.notifyDataSetChanged()
        })
        binding.mainChatList.adapter = adapter
        setHasOptionsMenu(true)
        return (binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_sign_out -> {
                auth.signOut()
                val activity = Intent(context,LoginActivity::class.java)
                requireActivity().startActivity(activity)
                requireActivity().finish()
                true
            }
            R.id.main_menu_new_chat -> {
                viewModel.setGroupFlag(false)
                findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
                true
            }
            R.id.main_menu_new_group -> {
                viewModel.setGroupFlag(true)
                findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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