package com.marslan.chatarneca.fragments.info

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
import com.marslan.chatarneca.databinding.FragmentGroupInfoBinding
import com.marslan.chatarneca.fragments.main.contact.ContactAdapter

class GroupInfoFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentGroupInfoBinding
        private lateinit var viewModel: SharedViewModel
        private var switch: Boolean = false
        private lateinit var adapter: ContactAdapter
        private lateinit var chat: EntityChat
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupInfoBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        chat = viewModel.getCurrentChat()!!
        adapter = ContactAdapter(arrayListOf(),this::onClick)
        binding.apply {
            groupInfoNameInput.setText(chat.name)
            groupInfoDescInput.setText(chat.description)
            groupInfoChangeNameBtn.setOnClickListener { editName() }
            groupInfoChangeDescBtn.setOnClickListener { editDescription() }
            groupInfoAddUserBtn.setOnClickListener { update() }
            groupInfoUsrlist.adapter = adapter
        }
        update()
        return (binding.root)
    }

    private fun editDescription() {
        chat.description = binding.groupInfoDescInput.text.toString()
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"change description ${chat.name}",Toast.LENGTH_SHORT).show()
    }
    private fun editName(){
        chat.name = binding.groupInfoNameInput.text.toString()
        viewModel.updateChat(chat)
        Toast.makeText(requireContext(),"change name ${chat.name}",Toast.LENGTH_SHORT).show()
    }

    private fun update(){
        switch = binding.groupInfoAddUserBtn.isChecked
        viewModel.getFirebaseDatabase().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var allUsers = arrayListOf<User>()
                    val chatUsers = chat.toRef.split("%")
                    if(snapshot.value != null) {
                        snapshot.getValue<ArrayList<User>>()?.let {
                            allUsers = it
                        }
                    }
                    val currentList = arrayListOf<User>()
                    chatUsers.forEach { user ->
                        currentList.add(allUsers.filter { it.id == user }[0])
                    }
                    allUsers.filter { chatUsers.contains(it.id).xor(switch) }
                        .apply {
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
    private fun onClick(id: String){
        if(switch)
            addUser(id)
        else
            deleteUser(id)
    }
}