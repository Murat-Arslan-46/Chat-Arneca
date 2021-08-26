package com.marslan.chatarneca.fragments.main.group

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentGroupBinding

class GroupFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentGroupBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var listAdapter: GroupListAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        listAdapter = GroupListAdapter(this::onClick,this::onLongClick)
        viewModel.getAllChat().observe(requireActivity(),{list ->
            val groupList = list.filter { it.id.toString() == it.toRef }
            listAdapter.setCurrentList(groupList)
            listAdapter.setCurrentList(list)
        })
        binding.groupList.adapter = listAdapter
        return binding.root
    }

    private fun onClick(chat: EntityChat){
        viewModel.setCurrentChat(chat)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
    private fun onLongClick(chat: EntityChat){}

}