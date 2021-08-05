package com.marslan.chatarneca.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.Chat
import com.marslan.chatarneca.data.Message
import com.marslan.chatarneca.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentMainBinding
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
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        myRefPath = viewModel.getUser().currentUser!!.uid
        val myRef = viewModel.getDatabase().getReference(myRefPath)
        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                newChat()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                newMessage()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("failed",error.message)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("clihd moved","!!")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("clihd removed","!!")
            }
        })
        binding.mainSignOut.setOnClickListener {
            viewModel.getUser().signOut()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container,LoginFragment()).commit()
        }
        binding.newChat.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container,ChatFragment(),"CHAT").commit()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createChat(toID: ArrayList<String>){
        val fromID = viewModel.getUser().currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val message = Message("hi!",fromID,date)
        toID.add(fromID)
        val randID = Random().nextInt().toString()
        val chat = Chat(randID,toID, arrayListOf(message))
    }

    private fun newMessage(){}

    private fun newChat(){}
}