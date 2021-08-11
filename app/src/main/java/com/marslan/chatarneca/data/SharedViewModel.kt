package com.marslan.chatarneca.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val readAllChat: LiveData<List<EntityChat>>
    private val repository : SharedRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chat : EntityChat

    init {
        val messageDao = SharedDatabase.getDatabase(application).messageDao()
        repository = SharedRepository(messageDao)
        readAllChat = repository.readAllChat
        auth = Firebase.auth
        db = Firebase.database
        chat = EntityChat()
    }
    fun getAuth() = auth

    fun getDB() = db

    fun getChat() = chat
    fun setChat(newChat : EntityChat){ chat = newChat }

    fun getMessage(id: Int) = repository.getChatMessage(id)
    fun newMessage(entityMessage: EntityMessage,toID: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.newMessage(entityMessage,toID)
        }
    }
    fun getChatList() = readAllChat
}