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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
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
        private var attachMedia = false
        private var media: Uri? = null
        private lateinit var binding: FragmentChatBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var chat: EntityChat
        private lateinit var adapter : ChatAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        chat = viewModel.getCurrentChat()
        adapter = ChatAdapter(
            this::imageViewer,
            this::onClickMessage,
            this::onLongClickMessage
        )
        adapter.isNotGroup(chat.users.split("%").size <= 2)
        binding.apply{
            chatMessageList.adapter = adapter
            chatSendMessage.setOnClickListener { onClickSend() }
            chatSendMedia.setOnClickListener { onClickAttach() }
            chatMediaCancel.setOnClickListener { cancelMedia() }
        }
        viewModel.getMessageWithChatID(chat.id).observe(requireActivity(), { list ->
            adapter.setCurrentList(list as ArrayList<EntityMessage>)
            list.forEach {
                if(!it.iSaw){
                    it.iSaw = true
                    viewModel.updateMessage(it)
                }
            }
            binding.chatMessageList.smoothScrollToPosition(adapter.itemCount)
        })
        requireActivity().actionBar?.title = chat.chatName
        requireActivity().actionBar?.subtitle = chat.toRef
        return (binding.root)
    }
    @SuppressLint("SimpleDateFormat")
    private fun onClickSend() {
        binding.chatSendMessage.setBackgroundResource(R.drawable.ic_waiting)
        val id =
        if(adapter.itemCount != 0)
            (((adapter.getLastItem().id/10000)+1)*10000) + chat.id
        else
            10000 + chat.id
        val text = binding.chatInputText.text.toString()
        val date = SimpleDateFormat("dd/MM/yy HH:mm").format(Date())
        val fromID = viewModel.getAuth().uid.toString()
        val message = EntityMessage(id,text,date,fromID,chat.id,media = attachMedia)
        message.ref = viewModel.getFirebaseDatabase()
            .getReference(chat.toRef).push().key.toString()

        if(attachMedia){
            Firebase.storage
            .getReference("${message.chatID}/${message.id}.jpg").putFile(media!!)
            .addOnSuccessListener {
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
                val file = File(imageDir,"${message.chatID}-${message.id}.jpg")
                val stream = FileOutputStream(file.path)
                val byteArray = requireActivity().contentResolver.openInputStream(media!!)?.readBytes()
                stream.write(byteArray)
                stream.close()
                sendToFirebase(message)
            }
            .addOnCanceledListener {
                Toast.makeText(requireContext(),getString(R.string.error_send),Toast.LENGTH_SHORT).show()
            }
        }
        else{
            sendToFirebase(message)
        }
    }
    private fun sendToFirebase(message: EntityMessage){
        viewModel.getFirebaseDatabase().getReference(chat.toRef)
            .child(message.ref).setValue(message)
            .addOnSuccessListener {
                viewModel.newMessage(message)
                binding.chatInputText.text.clear()
                cancelMedia()
                binding.chatSendMessage.setBackgroundResource(R.drawable.btn_chat_send)
                binding.chatSendMessage.setOnClickListener { onClickSend() }
            }
            .addOnCanceledListener {
                Toast.makeText(requireContext(),getString(R.string.error_send),Toast.LENGTH_SHORT).show()
                binding.chatSendMessage.setBackgroundResource(R.drawable.btn_chat_send)
                binding.chatSendMessage.setOnClickListener { onClickSend() }
            }
    }
    private fun onLongClickMessage(){
        if(adapter.getSelected().isEmpty())
            setHasOptionsMenu(true)
    }
    private fun onClickMessage(){
        if(adapter.getSelected().isEmpty())
            setHasOptionsMenu(false)
    }
    private fun onClickAttach() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }
    private fun cancelMedia(){
        binding.chatMediaPreview.visibility = View.GONE
        binding.chatMediaCancel.visibility = View.GONE
        attachMedia = false
        media = null
    }
    private fun imageViewer(uri: Uri){
        binding.chatImageViewer.apply {
            setImageURI(uri)
            visibility = View.VISIBLE
            setOnClickListener { visibility = View.GONE }
        }
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_selector_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.copy -> {
                val textToCopy = adapter.getSelected()[0].text
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
            R.id.info -> {
                findNavController().navigate(R.id.action_chatFragment_to_chatInfoFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
