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
    private val imageViewer: (Uri) -> Unit,
    private val onClick: () -> Unit,
    private val onLongClick: () -> Unit
)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val SEND = 1
        const val RECEIVE = 2
        private var isNotGroup = false
        private var currentList = arrayListOf<EntityMessage>()
        private val selectedList = arrayListOf<EntityMessage>()
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
    fun setCurrentList(list: List<EntityMessage>){
        currentList = list as ArrayList<EntityMessage>
        notifyDataSetChanged()
    }
    fun isNotGroup(bool: Boolean){ isNotGroup = bool }
    fun getSelected() = selectedList

    inner class SendMessageViewHolder(private val binding: ItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(message: EntityMessage){
            binding.sendMessage.text = message.text
            binding.root.setOnLongClickListener {
                onLongClick()
                if(selectedList.isEmpty()){
                    if (selectedList.none { it == message }) {
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        selectedList.add(message)
                    }
                    else {
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                        selectedList.remove(message)
                    }
                }
                true
            }
            binding.root.setOnClickListener {
                if(selectedList.isNotEmpty()){
                    if (selectedList.none { it == message }) {
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        selectedList.add(message)
                    }
                    else {
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                        selectedList.remove(message)
                    }
                }
                onClick()
            }
            binding.statusCheck.apply {
                visibility =
                            if (message.send)
                                View.VISIBLE
                            else
                                View.INVISIBLE
            }
            binding.sendMediaBox.apply {
                if (message.media) {
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

                    if (file.exists()) {
                        val image = Uri.fromFile(file)
                        setImageURI(image)
                        setOnClickListener { imageViewer(image) }
                    }
                    visibility = View.VISIBLE
                }
                else {
                    visibility = View.GONE
                }
            }
        }
    }

    inner class ReceiveMessageViewHolder(private val binding: ItemMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            val message = currentList[position]
            binding.receiveMessage.text = message.text
            binding.root.setOnLongClickListener {
                onLongClick()
                if(selectedList.isEmpty()){
                    if (selectedList.none { it == message }) {
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        selectedList.add(message)
                    }
                    else {
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                        selectedList.remove(message)
                    }
                }
                true
            }
            binding.root.setOnClickListener {
                if(selectedList.isNotEmpty()){
                    if (selectedList.none { it == message }) {
                        binding.root.setBackgroundResource(R.color.secondary_color_visible)
                        selectedList.add(message)
                    }
                    else {
                        binding.root.setBackgroundColor(Color.TRANSPARENT)
                        selectedList.remove(message)
                    }
                }
                onClick()
            }
            binding.receiveFromText.apply {
                text = message.fromID
                visibility =
                    if (isNotGroup)
                        View.GONE
                    else if (position != 0 && message.fromID == currentList[position - 1].fromID)
                        View.GONE
                    else
                        View.VISIBLE
            }
            binding.receiveMediaBox.apply {
                if (message.media) {
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
                    if (file.exists()) {
                        val image = Uri.fromFile(file)
                        Log.e("path", file.path)
                        setImageURI(image)
                        setOnClickListener { imageViewer(image) }
                    }
                    else {
                        setImageResource(R.drawable.ic_download)
                        setOnClickListener {downloadImage(message,file)}
                    }
                    visibility = View.VISIBLE
                }
                else {
                    visibility = View.GONE
                }
            }
        }
        @SuppressLint("NotifyDataSetChanged")
        fun downloadImage(message: EntityMessage, file: File){
            val maxDownloadSizeBytes: Long = 1024 * 1024
            val ref = FirebaseStorage.getInstance().reference
            val firebaseRef = ref.child("${message.chatID}/${message.id}.jpg")
            firebaseRef.getBytes(maxDownloadSizeBytes).addOnSuccessListener {
                if (it != null) {
                    val stream = FileOutputStream(file.path)
                    stream.write(it)
                    stream.close()
                    notifyDataSetChanged()
                }
            }
        }
    }
}