package com.marslan.chatarneca.fragments.main.contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.*
import com.marslan.chatarneca.databinding.FragmentContactBinding
import kotlin.collections.ArrayList

class ContactFragment : Fragment() {

    companion object {
        private lateinit var binding: FragmentContactBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var adapter: ContactAdapter
        private lateinit var users: ArrayList<String>
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        users = arrayListOf()
        adapter = ContactAdapter(arrayListOf(),this::onClick)
        viewModel.getUsers().observe(requireActivity(),{ list->
            val users = arrayListOf<EntityUser>()
            list.forEach {
                if(it.id != viewModel.getAuth().uid.toString())
                    users.add(it)
            }
            adapter.currentList = users
            adapter.notifyDataSetChanged()
        })
        binding.userList.adapter = adapter
        return binding.root
    }

    private fun onClick(id: String) {
        val users = "${viewModel.getAuth().currentUser!!.uid}%$id"
        val randID = (1000..9999).random()
        val name = "chat"
        var chat = EntityChat(randID, name, id, users,"hi!")
        viewModel.getAllChat().value?.forEach {
            val list = it.users.split("%")
            if (list.size <=2 && (list[0] == id || list[1] == id))
                chat = it
        }
        viewModel.setCurrentChat(chat)
        findNavController().navigateUp()
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
    private fun selectUser(toRef: String){
        if(users.none() { it == toRef }) {
            users.add(toRef)
        }
        else{
            users.remove(toRef)
        }
    }
    private fun newGroup(){
        var userList = viewModel.getAuth().currentUser!!.uid
        users.forEach { userList += "%$it" }
        val randID = (1000..9999).random()
        val name = "chat"
        var chat = EntityChat(randID, name, randID.toString(), userList,"hi!")
        if(users.size < 2) {
            viewModel.getAllChat().value?.forEach {
                val list = it.users.split("%")
                if (list[0] == users[0] || list[1] == users[0])
                    chat = it
            }
        }
        else{
            users.add(viewModel.getAuth().uid.toString())
            users.forEach {
                viewModel.getFirebaseDatabase()
                    .getReference(it).push().setValue(EntityMessage(
                        text = userList,
                        fromID = "-1",
                        chatID = randID
                    ))
            }
            chat.manager = true
        }
        viewModel.setCurrentChat(chat)
        findNavController().navigateUp()
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
}