package com.marslan.chatarneca.fragments.contact

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.*
import com.marslan.chatarneca.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    companion object {
        private lateinit var binding: FragmentContactBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var adapter: ContactAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        adapter = ContactAdapter(this::onClick)
        viewModel.getUsers().observe(requireActivity(),{ list->
            val users = arrayListOf<EntityUser>()
            list.forEach {
                if(it.id != viewModel.getAuth().uid.toString())
                    users.add(it)
            }
            adapter.setCurrentList(users)
        })
        binding.userList.adapter = adapter
        binding.contactNewChat.setOnClickListener { newChat() }
        return binding.root
    }

    private fun onClick(user: EntityUser) {
        if(adapter.getSelectedList().isEmpty()){
            binding.contactNewChat.visibility = View.GONE
        }
        else {
            binding.contactNewChat.visibility = View.VISIBLE
            if(adapter.getSelectedList().size == 1)
                binding.contactBtnIcon.setImageResource(R.drawable.btn_chat_send)
            else
                binding.contactBtnIcon.setImageResource(R.drawable.ic_list_group)
        }
    }

    private fun newChat(){
        val users = adapter.getSelectedList()
        when(users.size){
            0 -> {return}
            1 -> {
                val user = users[0]
                val userList = "${viewModel.getAuth().uid.toString()}%${user.id}"
                var chat = EntityChat(
                    (1000..9999).random(),
                    user.name,
                    user.id,
                    userList,
                    user.description,
                    user.imageSrc
                )
                viewModel.getAllChat().value?.forEach {
                    val list = it.users.split("%")
                    if (list.size <=2 && (list[0] == user.id || list[1] == user.id))
                        chat = it
                }
                viewModel.setCurrentChat(chat)
                findNavController().navigateUp()
                findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
            }
            else -> {
                var userList = viewModel.getAuth().uid.toString()
                users.forEach { userList += "%${it.id}" }
                val id = (1000..9999).random()
                val chat = EntityChat(
                    id,
                    "new group chat",
                    id.toString(),
                    userList,
                    "hi everyone :)",
                    "null",
                    true
                )
                viewModel.setCurrentChat(chat)
                findNavController().navigateUp()
                findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
                findNavController().navigate(R.id.action_chatFragment_to_chatInfoFragment)
            }
        }
        adapter.clearSelected()
    }

}