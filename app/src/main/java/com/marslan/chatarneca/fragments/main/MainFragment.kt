package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.FirebaseDatabase
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.Chat
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var adapter: MainAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var myRefPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = MainAdapter (this::openChat)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
        val db = viewModel.getDB()
        myRefPath = auth.currentUser!!.uid
        val myRef = db.getReference(myRefPath)
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
        binding.newChat.setOnClickListener {
            var toID = "RlFxGSF10khblvd7stfZaRvMazJ2"
            if(myRefPath == "RlFxGSF10khblvd7stfZaRvMazJ2")
                toID = "Hf0LR9vztGWKjhjX4vWxxAr93E53"
            createChat(db,arrayListOf(toID,myRefPath))
        }
        binding.mainChatList.adapter = adapter
    }

    @SuppressLint("SimpleDateFormat")
    private fun createChat(db : FirebaseDatabase, toID : ArrayList<String>){
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val randID = Random().nextInt().toString()
        val message = EntityMessage(1,"hi!",date,"0",randID.toInt())
        val chat = Chat(randID,toID, arrayListOf(message))
        for(id in toID){
            var chatList = arrayListOf<Chat>()
            val ref = db.getReference(id)
            ref.get().addOnSuccessListener {
                if(it.value != null)
                    chatList = it.value as ArrayList<Chat>
                chatList.add(chat)
                ref.setValue(chatList)
            }
        }
        viewModel.setChat(adapter.itemCount.toString())
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh(chatlist: ArrayList<Chat>){
        adapter.submitList(chatlist)
        adapter.notifyDataSetChanged()
    }

    private fun openChat(chatID: String){
        viewModel.setChat(chatID)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
}