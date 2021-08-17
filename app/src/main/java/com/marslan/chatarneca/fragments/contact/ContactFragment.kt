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
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.FragmentContactBinding
import kotlin.collections.ArrayList

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
        adapter = ContactAdapter(arrayListOf(),this::openChat)
        viewModel.getFirebaseDatabase().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null)
                        snapshot.getValue<ArrayList<User>>()?.apply {
                            val temp = arrayListOf<User>()
                            this.forEach {
                                if(it.id != viewModel.getAuth().currentUser!!.uid)
                                    temp.add(it)
                            }
                            adapter.currentList = temp
                            adapter.notifyDataSetChanged()
                        }
                }
            }
        )
        binding.userList.adapter = adapter
        return binding.root
    }
    private fun openChat(toRef: String) {
        val users = "${viewModel.getAuth().currentUser!!.uid}%$toRef"
        val randID = (1000..9999).random()
        val name = "chat"
        var chat = EntityChat(randID, name, toRef, users)
        viewModel.getAllChat().value?.forEach {
            val list = it.users.split("%")
            if (list[0] == toRef || list[1] == toRef)
                chat = it
        }
        viewModel.setCurrentChat(chat)
        findNavController().navigateUp()
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
}