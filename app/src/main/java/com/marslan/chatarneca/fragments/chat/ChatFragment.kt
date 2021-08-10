package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.*
import com.marslan.chatarneca.databinding.FragmentChatBinding
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.data.SharedViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




class ChatFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentChatBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var chat: Chat
    private lateinit var currentList: ArrayList<EntityMessage>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding
            .inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())
            .get(SharedViewModel::class.java)

        val auth = viewModel.getAuth()
        val db = viewModel.getDB()

        db.getReference(auth.currentUser!!.uid)
            .child(viewModel.getChat())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null) {
                        val value = snapshot.getValue<Chat>()
                        newMessage(auth.uid.toString(),value)
                        db.getReference(auth.currentUser!!.uid)
                            .child(viewModel.getChat())
                            .setValue(Chat(value!!.id,value.userList, arrayListOf()))
                    }
                    else
                        Log.d("get failed","null list")
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("fail message",error.message)
                }
        })
        binding.chatSendMessage.setOnClickListener {
            sendMessage(db,auth)
        }
        binding.chatMessageList.adapter = adapter
        return (binding.root)
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendMessage(db : FirebaseDatabase, auth : FirebaseAuth) {
        val text = binding.chatInputText.text.toString()
        binding.chatInputText.text.clear()
        val fromID = auth.currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val id = 1
        val message = EntityMessage(id,text,date,fromID,chat.id.toInt())
        currentList.add(message)
        for(user in chat.userList)
            db.getReference(user)
                .child(viewModel.getChat())
                .child("messageList")
                .setValue(currentList)
    }

    private fun newMessage(uid: String, value: Chat?) {
        chat = value!!
        val list = chat.messageList
        adapter.clear()
        list.forEach {
            if (it.fromID == uid) {
                adapter.add(SendMessageItem(it))
            } else {
                adapter.add(ReceiveMessageItem(it))
            }
            viewModel.newMessage(it)
        }
        currentList = list
    }
}
