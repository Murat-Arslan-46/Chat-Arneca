package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.marslan.chatarneca.data.*
import com.marslan.chatarneca.databinding.FragmentChatBinding
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




class ChatFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentChatBinding
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var chat: EntityChat
    private lateinit var currentList: ArrayList<EntityMessage>

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged", "RestrictedApi")
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
        chat = viewModel.getChat()
        binding.chatSendMessage.setOnClickListener {
            sendMessage(db,auth)
        }
        binding.chatMessageList.adapter = adapter
        adapter.clear()
        viewModel.getMessage(chat.chatID).observe(requireActivity(), { list ->
            currentList = list as ArrayList<EntityMessage>
            adapter.clear()
            list.forEach {
                if (it.fromID == auth.currentUser!!.uid) {
                    adapter.add(SendMessageItem(it))
                } else {
                    adapter.add(ReceiveMessageItem(it))
                }
            }
            adapter.notifyDataSetChanged()
        })

        return (binding.root)
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun sendMessage(db : FirebaseDatabase,auth: FirebaseAuth) {
        val text = binding.chatInputText.text.toString()
        binding.chatInputText.text.clear()
        val fromID = auth.currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val id =
            if(currentList.isEmpty())
                chat.chatID*10000
            else
                currentList[currentList.size-1].id+1
        val message = EntityMessage(id,text,date,fromID,chat.chatID)
        currentList.add(message)
        db.getReference(chat.toID)
            .push().setValue(message)
        viewModel.newMessage(message,chat.toID)
        adapter.add(SendMessageItem(message))
    }
}
