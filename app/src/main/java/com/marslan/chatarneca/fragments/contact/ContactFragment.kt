package com.marslan.chatarneca.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.newContact.setOnClickListener {
            if(binding.newId.text.isNotEmpty()) {
                viewModel.getChatList().observe(requireActivity(),{ list ->
                    var randID = (1..9999).random()
                    list.forEach {
                        if(it.toID == binding.newId.text.toString())
                            randID = it.chatID
                    }
                    val name = "chat"
                    val chat = EntityChat(randID,name,binding.newId.text.toString())
                    viewModel.setChat(chat)
                    if(findNavController().currentDestination!!.id == R.id.contactFragment)
                    findNavController().navigate(R.id.action_contactFragment_to_chatFragment)
                })

            }
        }

        return binding.root
    }
}