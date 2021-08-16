package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.userdb.EntityUser
import com.marslan.chatarneca.databinding.FragmentChatInfoBinding
import com.marslan.chatarneca.fragments.contact.ContactAdapter

class ChatInfoFragment : Fragment() {

    private lateinit var binding: FragmentChatInfoBinding
    private lateinit var viewModel: SharedViewModel
    private var switch : Boolean = false
    private lateinit var adapter: ContactAdapter
    private lateinit var chat: EntityChat

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatInfoBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        chat = viewModel.getChat()
        adapter = ContactAdapter(arrayListOf(),this::clickUser)
        binding.chatInfoNameInput.setText(viewModel.getChat().chatName)
        binding.chatInfoChangeNameBtn.setOnClickListener { editName() }
        binding.chatInfoAddUserBtn.setOnClickListener {
            switch = binding.chatInfoAddUserBtn.isChecked
            update()
        }
        update()
        binding.chatInfoUsrlist.adapter = adapter
        return (binding.root)
    }
    private fun editName(){
        chat.chatName = binding.chatInfoNameInput.text.toString()
        viewModel.updateChatName(chat)
        Toast.makeText(requireContext(),"change name ${chat.chatName}",Toast.LENGTH_SHORT).show()
    }
    private fun update(){
        viewModel.getDB().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var temp = arrayListOf<EntityUser>()
                    val toID = chat.toID.split("%")
                    val users = arrayListOf<EntityUser>()
                    if(snapshot.value != null)
                        temp = snapshot.getValue<ArrayList<EntityUser>>()!!
                    toID.forEach { id ->
                        temp.forEach {
                            if(!switch && it.id == id)
                                users.add(it)
                            else if(switch)
                                users.add(it)
                        }
                    }
                    adapter.currentList = users
                    adapter.notifyDataSetChanged()
                }
            }
        )
    }
    private fun deleteUser(id: String) {}
    private fun addUser(id: String) {}
    private fun clickUser(id: String){
        if(switch)
            addUser(id)
        else
            deleteUser(id)
    }
}