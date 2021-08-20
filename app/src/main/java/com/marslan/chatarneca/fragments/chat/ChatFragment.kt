package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.data.*
import com.marslan.chatarneca.databinding.FragmentChatBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*


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
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
        chat = viewModel.getCurrentChat()
        binding.chatSendMessage.setOnClickListener {
            sendMessage(auth)
        }
        binding.chatMessageList.adapter = adapter
        adapter.clear()
        viewModel.getMessageWithChatID(chat.id).observe(requireActivity(), { list ->
            currentList = list as ArrayList<EntityMessage>
            adapter.clear()
            list.forEach {
                if (it.fromID == auth.currentUser!!.uid) {
                    adapter.add(SendMessageItem(it))
                } else {
                    adapter.add(ReceiveMessageItem(it))
                }
                if(!it.iSaw){
                    it.iSaw = true
                    viewModel.updateMessage(it)
                }
            }
            binding.chatMessageList.smoothScrollToPosition(binding.chatMessageList.adapter!!.itemCount)
        })
        setHasOptionsMenu(true)
        return (binding.root)
    }/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                findNavController().navigate(R.id.action_chatFragment_to_chatInfoFragment)
                true
            }
            R.id.delete -> {
                Log.d("delete","chat")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
*/
    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun sendMessage(auth: FirebaseAuth) {
        val text : String
        binding.chatInputText.text.apply {
            text = this.toString()
            clear()
        }
        val fromID = auth.currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val id =
            if(currentList.size!=0)
                (((currentList[currentList.size-1].id/10000)+1)*10000) + chat.id
            else
                10000 + chat.id
        val message = EntityMessage(id,text,date,fromID,chat.id)
        val key = viewModel.getFirebaseDatabase().getReference(chat.toRef).push().key
        message.ref = key.toString()
        viewModel.getFirebaseDatabase().getReference(chat.toRef).child(key.toString()).setValue(message)
        viewModel.newMessage(message)
    }
}
