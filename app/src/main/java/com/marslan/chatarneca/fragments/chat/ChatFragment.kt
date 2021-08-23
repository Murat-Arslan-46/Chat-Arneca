package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.RecyclerView





class ChatFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter : ChatAdapter
    private lateinit var chat: EntityChat
    private lateinit var selectedMessage : ArrayList<EntityMessage>

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
        selectedMessage = arrayListOf()
        val isNotGroup = chat.users.split("%").size <= 2
        adapter = ChatAdapter(arrayListOf(),selectedMessage,isNotGroup,auth.uid.toString(),this::onClick,this::onLongClick)
        binding.chatMessageList.adapter = adapter
        viewModel.getMessageWithChatID(chat.id).observe(requireActivity(), { list ->
            adapter.currentList = list as ArrayList<EntityMessage>
            list.forEach {
                if(!it.iSaw){
                    it.iSaw = true
                    viewModel.updateMessage(it)
                }
            }
            adapter.notifyDataSetChanged()
            binding.chatMessageList.smoothScrollToPosition(adapter.itemCount)
        })
        return (binding.root)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.copy -> {
                val textToCopy = selectedMessage[0].text
                val clipboardManager = getSystemService(requireContext(),ClipboardManager::class.java) as ClipboardManager
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(),"copy message",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.delete -> {
                Log.d("delete","chat")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun sendMessage(auth: FirebaseAuth) {
        val text : String
        binding.chatInputText.text.apply {
            if(this.isEmpty())
                return
            text = this.toString()
            clear()
        }
        val fromID = auth.currentUser!!.uid
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm")
        val date = sdf.format(Date())
        val id =
            if(adapter.currentList.isNotEmpty())
                (((adapter.currentList[adapter.currentList.size-1].id/10000)+1)*10000) + chat.id
            else
                10000 + chat.id
        val message = EntityMessage(id,text,date,fromID,chat.id)
        val key = viewModel.getFirebaseDatabase().getReference(chat.toRef).push().key
        message.ref = key.toString()
        viewModel.getFirebaseDatabase().getReference(chat.toRef).child(key.toString()).setValue(message)
        viewModel.newMessage(message)
    }

    private fun onLongClick(message: EntityMessage):Boolean{
        if(selectedMessage.isEmpty()){
            setHasOptionsMenu(true)
            if (selectedMessage.none { it == message })
                selectedMessage.add(message)
            else
                selectedMessage.remove(message)
        }
        return true
    }

    private fun onClick(message: EntityMessage){
        if(selectedMessage.isNotEmpty()){
            if(selectedMessage.none { it == message })
                selectedMessage.add(message)
            else
                selectedMessage.remove(message)
        }
        if(selectedMessage.isEmpty())
            setHasOptionsMenu(false)
    }
}
