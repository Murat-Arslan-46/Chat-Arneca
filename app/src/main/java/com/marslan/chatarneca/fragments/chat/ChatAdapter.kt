package com.marslan.chatarneca.fragments.chat

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.databinding.ItemMessageReceiveBinding
import com.marslan.chatarneca.databinding.ItemMessageSendBinding
import java.io.File
import java.io.FileOutputStream

class ChatAdapter(
    private var currentList: List<EntityMessage>,
    val selectedList: List<EntityMessage>,
    private val isNotGroup: Boolean,
    private val onClick: (EntityMessage) -> Unit,
    private val onLongClick: (EntityMessage) -> Boolean
    )
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val SEND = 1
        const val RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        if(viewType == RECEIVE){
            val binding = ItemMessageReceiveBinding.inflate(inflate,parent,false)
            return ReceiveMessageViewHolder(binding)
        }
        val binding = ItemMessageSendBinding.inflate(inflate,parent,false)
        return SendMessageViewHolder(binding)
    }
    fun getLastItem() = currentList.last()
    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == SEND){
            (holder as SendMessageViewHolder).bind(currentList[position])
        }
        else{
            (holder as ReceiveMessageViewHolder).bind(position)
        }
    }
    override fun getItemViewType(position: Int): Int {
    return  if(currentList[position].fromID == Firebase.auth.currentUser!!.uid)
                SEND
            else
                RECEIVE
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setSubmitList(list: List<EntityMessage>){
        currentList = list
        notifyDataSetChanged()
    }

    inner class SendMessageViewHolder(private val binding: ItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(message: EntityMessage){
            binding.sendMessage.text = message.text
            binding.root.setOnLongClickListener {
                if(selectedList.isEmpty()){
                    if (selectedList.none { it == message })
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                    else
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
                onLongClick(message)
            }
            binding.root.setOnClickListener {
                if(selectedList.isNotEmpty()){
                    if(selectedList.none { it == message })
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                    else
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
                onClick(message)
            }
            if(message.send)
                binding.statusCheck.visibility = View.VISIBLE
            else
                binding.statusCheck.visibility = View.INVISIBLE
            if(message.media) {
                val appDir = File(Environment.getExternalStorageDirectory(), "ChatApp")
                    .apply {
                        if (!exists())
                            mkdir()
                    }
                val imageDir = File(appDir, "image")
                    .apply {
                        if (!exists())
                            mkdir()
                    }
                val file = File(imageDir, "${message.chatID}-${message.id}.jpg")
                Log.e("path", file.path)
                if (file.exists()) {
                    val image = Uri.fromFile(file)
                    Log.e("path", file.path)
                    binding.sendMediaBox.setImageURI(image)
                }
                binding.sendMediaBox.visibility = View.VISIBLE
            }
            else
                binding.sendMediaBox.visibility = View.GONE
        }
    }

    inner class ReceiveMessageViewHolder(private val binding: ItemMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("NotifyDataSetChanged")
        fun bind(position: Int){
            val message = currentList[position]
            binding.receiveMessage.text = message.text
            if(isNotGroup)
                binding.receiveFromText.visibility = View.GONE
            else if(position != 0 && message.fromID == currentList[position-1].fromID)
                binding.receiveFromText.visibility = View.GONE
            else
                binding.receiveFromText.visibility = View.VISIBLE
            binding.root.setOnLongClickListener {
                if(selectedList.isEmpty()){
                    if (selectedList.none { it == message })
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                    else
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
                onLongClick(message)
            }
            binding.root.setOnClickListener {
                if(selectedList.isNotEmpty()){
                    if(selectedList.none { it == message })
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                    else
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
                onClick(message)
            }
            if(message.media) {
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
                if(file.exists()) {
                    val image = Uri.fromFile(file)
                    Log.e("path",file.path)
                    binding.receiveMediaBox.setImageURI(image)
                }
                else
                    binding.receiveMediaBox.setImageResource(R.drawable.ic_download)
                binding.receiveMediaBox.visibility = View.VISIBLE
                binding.receiveMediaBox.setOnClickListener {
                    val ref = FirebaseStorage.getInstance().reference
                    val firebaseRef = ref.child("${message.chatID}/${message.id}.jpg")
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
                    val maxDownloadSizeBytes: Long = 1024 * 1024
                    firebaseRef.getBytes(maxDownloadSizeBytes).addOnSuccessListener {
                        if(it != null){
                            val stream = FileOutputStream(localFile.path)
                            stream.write(it)
                            stream.close()
                            notifyDataSetChanged()
                        }
                    }
                }
            }
            else
                binding.receiveMediaBox.visibility = View.GONE
        }
    }
}