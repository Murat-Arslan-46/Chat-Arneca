package com.marslan.chatarneca.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.Chat
import com.marslan.chatarneca.data.Message
import com.marslan.chatarneca.data.ReceiveMessageItem
import com.marslan.chatarneca.data.SendMessageItem
import com.marslan.chatarneca.databinding.FragmentChatBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentChatBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private var index = -1
    private lateinit var chat: Chat
    private lateinit var currentList: ArrayList<Message>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding
            .inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val ref = viewModel.getDatabase().getReference(viewModel.getUser().uid.toString())
        val chatID = viewModel.getChat()
        //.equalTo(chatID,"id")
        ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var list = arrayListOf<Message>()
                    index = -1
                    if(snapshot.value != null) {
                        val value = snapshot.getValue<ArrayList<Chat>>()
                        for(currentChat in value!!){
                            index++
                            if(currentChat.id == chatID) {
                                chat = currentChat
                                list = chat.messageList
                                adapter.clear()
                                list.forEach {
                                    if (it.fromID == viewModel.getUser().uid) {
                                        adapter.add(SendMessageItem(it))
                                    } else {
                                        adapter.add(ReceiveMessageItem(it))
                                    }
                                }
                            }
                        }
                    }
                    else
                        Log.d("get failed","null list")
                    currentList = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("fail message",error.message)
                }
        })
        binding.chatSendMessage.setOnClickListener {
            sendMessage()
        }
        return (binding.root)
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendMessage() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            val text = binding.chatInputText.text.toString()
            val fromID = viewModel.getUser().currentUser!!.uid
            val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
            val date = sdf.format(Date())
            val message = Message(text,fromID,date)
            val ref = viewModel.getDatabase().getReference(fromID)/*
            ref.child(index.toString()).get().addOnSuccessListener {
                currentList = it.getValue<ArrayList<Message>>()!!
            }*/
            currentList.add(message)
            for(user in chat.userList){
                val ref = viewModel.getDatabase().getReference(user)
                    .child("$index")
                    .child("messageList")
                ref.setValue(currentList)
            }
        },50)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.chatMessageList.adapter = adapter
    }
}
