package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.LoginActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: SharedViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = MainAdapter (this::openChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
        viewModel.getDB().getReference(auth.currentUser!!.uid)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.value != null) {
                        val message = snapshot.getValue<EntityMessage>()
                        if (message != null) {
                            viewModel.newMessage(message,message.fromID)
                        }
                    }
                    viewModel.getDB()
                        .getReference(auth.currentUser!!.uid).removeValue()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("change data",";)")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("cancel firebase",";)")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("moved child",";)")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("empty firebase",";)")
                }
            })
        viewModel.getChatList().observe(requireActivity(), {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        binding.newChat.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
        }
        binding.mainSignOut.setOnClickListener {
            auth.signOut()
            val activity = Intent(context,LoginActivity::class.java)
            requireActivity().startActivity(activity)
            requireActivity().finish()
        }
        binding.mainChatList.adapter = adapter
        return (binding.root)
    }

    private fun openChat(chat: EntityChat){
        viewModel.setChat(chat)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
}