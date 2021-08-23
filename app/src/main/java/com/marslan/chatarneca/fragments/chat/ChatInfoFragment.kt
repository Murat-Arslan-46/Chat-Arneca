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
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.FragmentChatInfoBinding
import com.marslan.chatarneca.fragments.main.contact.ContactAdapter

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
        chat = viewModel.getCurrentChat()
        adapter = ContactAdapter(arrayListOf(),this::clickUser)
        binding.chatInfoNameInput.setText(viewModel.getCurrentChat().chatName)
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
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"change name ${chat.chatName}",Toast.LENGTH_SHORT).show()
    }
    private fun update(){
        viewModel.getFirebaseDatabase().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var temp = arrayListOf<User>()
                    val toID = chat.toRef.split("%")
                    if(snapshot.value != null) {
                        snapshot.getValue<ArrayList<User>>()?.let {
                            temp = it
                        }
                    }
                    temp.filter { toID.contains(it.id).xor(switch) }.apply {
                        adapter.currentList = this
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        )
    }
    private fun deleteUser(id: String) {
        val toID = chat.users.split("%")
        var temp = "%${viewModel.getAuth().currentUser!!.uid}"
        toID.forEach {
            if(it != id)
                temp += "%$it"
        }
        chat.users = temp
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"delete user $id",Toast.LENGTH_SHORT).show()
    }
    private fun addUser(id: String) {
        chat.users += "%$id"
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"add user $id",Toast.LENGTH_SHORT).show()
    }
    private fun clickUser(id: String){
        if(switch)
            addUser(id)
        else
            deleteUser(id)
    }
}