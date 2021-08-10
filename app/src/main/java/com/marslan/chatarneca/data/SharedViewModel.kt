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
import com.marslan.chatarneca.data.messagedb.EntityMessage
import com.marslan.chatarneca.data.messagedb.MessageDatabase
import com.marslan.chatarneca.data.messagedb.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val readAllData: LiveData<List<EntityMessage>>
    private val repository : MessageRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chatID : String
    init {
        val messageDao = MessageDatabase.getDatabase(application).messageDao()
        repository = MessageRepository(messageDao)
        readAllData = repository.readAllData
        auth = Firebase.auth
        db = Firebase.database
        chatID = "-1"
    }
    fun getAuth() = auth
    fun getDB() = db
    fun getChat() = chatID
    fun setChat(id : String){ chatID = id }
    fun newMessage(entityMessage: EntityMessage){
        viewModelScope.launch(Dispatchers.IO) {
            repository.newMessage(entityMessage)
        }
    }
}