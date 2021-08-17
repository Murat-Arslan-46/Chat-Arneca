package com.marslan.chatarneca.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val repository : SharedRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chat : EntityChat
    private var userIndex : Int
    init {
        val messageDao = SharedDatabase.getDatabase(application).messageDao()
        userIndex = -1
        auth = Firebase.auth
        db = Firebase.database
        repository = SharedRepository(messageDao)
        chat = EntityChat()
    }
    fun getAuth() = auth
    fun getFirebaseDatabase() = db
    fun getUserIndex() = userIndex
    fun setUserIndex(index: Int){userIndex = index}
    fun getCurrentChat() = chat
    fun setCurrentChat(newChat: EntityChat){ chat = newChat }
    fun getMessageWithChatID(id: Int) = repository.getChatMessage(id)
    fun getAllChatWithLastMessage() = repository.allChatWithLastMessage
    fun newMessage(entityMessage: EntityMessage){
        if(entityMessage.fromID == "-1"){
            db.getReference("users")
                .child(userIndex.toString())
                .child("listenerRef").get().addOnSuccessListener {
                    if(it.value != null){
                        it.getValue<ArrayList<String>>()?.add(entityMessage.text)
                    }
                }
        }
        else if(entityMessage.fromID == auth.currentUser!!.uid){
            viewModelScope.launch(Dispatchers.IO) {
                repository.newMessage(entityMessage)
                val list = getAllChat().value
                if(list == null)
                    newChat(chat)
                else if(list.filter { it.users == chat.users }.isEmpty())
                    newChat(chat)
            }
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                repository.newMessage(entityMessage)
                val users = "${entityMessage.fromID}%${auth.currentUser!!.uid}"
                val randID = entityMessage.id%10000

                val name = "chat"
                var tempChat = EntityChat(randID, name, entityMessage.fromID, users)
                val list = getAllChat().value
                if(list == null)
                    newChat(tempChat)
                else if(list.filter { it.users == users }.isEmpty())
                    newChat(tempChat)
            }
        }
    }
    fun updateMessage(entityMessage: EntityMessage){
        viewModelScope.launch(Dispatchers.IO) { repository.updateMessage(entityMessage) }
    }
    fun getAllChat() = repository.readAllChat
    fun newChat(entityChat: EntityChat){
        viewModelScope.launch(Dispatchers.IO) { repository.newChat(entityChat) }
    }
    fun deleteChat(entityChat: EntityChat){
        viewModelScope.launch(Dispatchers.IO) { repository.deleteChat(entityChat) }
    }
    fun updateChat(entityChat: EntityChat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChat(entityChat)
        }
    }
}