package com.marslan.chatarneca.fragments.contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.userdb.EntityUser
import com.marslan.chatarneca.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        var users = arrayListOf<EntityUser>()
        viewModel.getDB().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null)
                        users = snapshot.getValue<ArrayList<EntityUser>>()!!
                    adapter.currentList = users
                    adapter.notifyDataSetChanged()
                }
            }
        )
        adapter = ContactAdapter(users,this::openChat)
        binding.userList.adapter = adapter
        return binding.root
    }
    private fun openChat(id: String){
        viewModel.getChatList().observe(requireActivity(),{ list ->
            try {
                var randID = (1..9999).random()
                list.forEach {
                    if(it.toID == id)
                        randID = it.chatID
                }
                val name = "chat"
                val chat = EntityChat(randID,name,id)
                viewModel.setChat(chat)
                findNavController().navigateUp()
            }catch (e: Exception){
                Log.d("","")
            }
        })
    }
}