package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentChatBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {

    companion object {
        var attachMedia = false
        var media: Uri? = null
        private lateinit var viewModel: SharedViewModel
        private lateinit var binding: FragmentChatBinding
        private lateinit var adapter : ChatAdapter
        private lateinit var chat: EntityChat
        private lateinit var selectedMessage : ArrayList<EntityMessage>
    }

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
        adapter = ChatAdapter(arrayListOf(),selectedMessage,isNotGroup,this::onClick,this::onLongClick)
        binding.chatMessageList.adapter = adapter
        viewModel.getMessageWithChatID(chat.id).observe(requireActivity(), { list ->
            adapter.setSubmitList(list as ArrayList<EntityMessage>)
            list.forEach {
                if(!it.iSaw){
                    it.iSaw = true
                    viewModel.updateMessage(it)
                }
            }
            binding.chatMessageList.smoothScrollToPosition(adapter.itemCount)
        })
        binding.chatSendMedia.setOnClickListener { attachMedia() }
        binding.chatMediaCancel.setOnClickListener {
            binding.chatMediaPreview.visibility = View.GONE
            binding.chatMediaCancel.visibility = View.GONE
            attachMedia = false
            media = null
        }
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
            if(adapter.itemCount != 0)
                (((adapter.getLastItem().id/10000)+1)*10000) + chat.id
            else
                10000 + chat.id
        val message = EntityMessage(id,text,date,fromID,chat.id,media = attachMedia)
        val key = viewModel.getFirebaseDatabase().getReference(chat.toRef).push().key
        message.ref = key.toString()
        if(attachMedia){
            Firebase.storage
                .getReference("${message.chatID}/${message.id}.jpg")
                .putFile(media!!)
            val appDir = File(Environment.getExternalStorageDirectory(),"ChatApp")
                .apply {
                    if (!exists())
                        mkdir()
                }
            val imageDir = File(appDir,"image")
                .apply {
                    if (!exists())
                        mkdir()
                }
            val localFile = File(imageDir,"${message.chatID}-${message.id}.jpg")
            val stream = FileOutputStream(localFile.path)
            val byteArray = requireActivity().contentResolver.openInputStream(media!!)?.readBytes()
            stream.write(byteArray)
            stream.close()
            binding.chatMediaPreview.visibility = View.GONE
            binding.chatMediaCancel.visibility = View.GONE
            attachMedia = false
            media = null
        }
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

    private fun attachMedia() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            media = data?.data
            binding.chatMediaPreview.setImageURI(media)
            binding.chatMediaPreview.visibility = View.VISIBLE
            binding.chatMediaCancel.visibility = View.VISIBLE
            attachMedia = true
        }
    }

}
