package com.marslan.chatarneca.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.MainAdapter
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.Chat
import com.marslan.chatarneca.data.Message
import com.marslan.chatarneca.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var myRefPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = MainAdapter (this::openChat)
        return (binding.root)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        myRefPath = viewModel.getUser().currentUser!!.uid
        val myRef = viewModel.getDatabase().getReference(myRefPath)
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue<ArrayList<Chat>>()
                if (value != null)
                    refresh(value)
                else
                    refresh(arrayListOf<Chat>())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        binding.mainSignOut.setOnClickListener {
            viewModel.getUser().signOut()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container,LoginFragment()).commit()
        }
        binding.newChat.setOnClickListener {
            var toID = "RlFxGSF10khblvd7stfZaRvMazJ2"
            if(myRefPath == "RlFxGSF10khblvd7stfZaRvMazJ2")
                toID = "Hf0LR9vztGWKjhjX4vWxxAr93E53"
            createChat(arrayListOf(toID))
            parentFragmentManager.beginTransaction()
                .replace(R.id.container,ChatFragment(),"CHAT").commit()
        }
        binding.mainChatList.adapter = adapter
    }

    @SuppressLint("SimpleDateFormat")
    private fun createChat(toID: ArrayList<String>){
        val fromID = viewModel.getUser().currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val message = Message("hi!","0",date)
        toID.add(fromID)
        val randID = Random().nextInt().toString()
        val chat = Chat(randID,toID, arrayListOf(message))
        for(id in toID){
            var chatList = arrayListOf<Chat>()
            val ref = viewModel.getDatabase().getReference(id)
            ref.get().addOnSuccessListener {
                if(it.value != null)
                    chatList = it.value as ArrayList<Chat>
                chatList.add(chat)
                ref.setValue(chatList)
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh(chatlist: ArrayList<Chat>){
        adapter.submitList(chatlist)
        adapter.notifyDataSetChanged()
    }

    private fun openChat(chat: Chat){
        viewModel.setChat(chat.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container,ChatFragment(),"CHAT").commit()
    }
}