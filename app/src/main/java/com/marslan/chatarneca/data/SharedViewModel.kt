package com.marslan.chatarneca.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val repository : SharedRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chat : EntityChat
    private var userIndex : Int
    private var newGroupFlag : Boolean
    init {
        val messageDao = SharedDatabase.getDatabase(application).messageDao()
        userIndex = -1
        auth = Firebase.auth
        db = Firebase.database
        repository = SharedRepository(messageDao)
        chat = EntityChat()
        newGroupFlag = false
    }
    fun getAuth() = auth
    fun getFirebaseDatabase() = db
    fun getGroupFlag() = newGroupFlag
    fun setGroupFlag(flag: Boolean) {newGroupFlag = flag}
    fun setUserIndex(index: Int){userIndex = index}
    fun getCurrentChat() = chat
    fun setCurrentChat(newChat: EntityChat){ chat = newChat }
    fun getAllMessage() = repository.readAllMessage
    fun checkMessageSend(ref: String, count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getMessage(ref)
            if(list.isNotEmpty()) {
                val message = repository.getMessage(ref)[0]
                val currentChat = repository.readSingleChat(message.chatID)[0]
                if (currentChat.users.split("%").size == count + 1) {
                    message.send = true
                    updateMessage(message)
                    db.getReference("${message.chatID}-${message.fromID}")
                        .child(message.ref)
                        .setValue(null)
                    db.getReference(message.chatID.toString())
                        .child(message.ref)
                        .setValue(null)
                }
            }
        }
    }
    fun getMessageWithChatID(id: Int) = repository.getChatMessage(id)
    fun getAllChatWithLastMessage() = repository.allChatWithLastMessage
    fun checkMessageIsNew(message: EntityMessage){
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getMessage(message.ref)
            if (message.fromID != auth.uid) {
                message.send = true
                newMessage(message)
                val ref = "${message.chatID}-${message.fromID}"
                if(list.isEmpty())
                db.getReference(ref)
                    .child(message.ref)
                    .push().setValue(auth.currentUser!!.uid)
            }
        }

    }
    fun newMessage(entityMessage: EntityMessage){
        when (entityMessage.fromID) {
            "-1" -> {
                val ref =db.getReference("users")
                    .child(userIndex.toString())
                    .child("listenerRef")
                ref.get().addOnSuccessListener {
                    if(it.value != null){
                        var list = arrayListOf<String>()
                            if(it.getValue<ArrayList<String>>() != null)
                                list = it.getValue<ArrayList<String>>()!!
                        list.add(entityMessage.chatID.toString())
                        ref.setValue(list)
                    }
                }
                newChat(
                    EntityChat(
                        entityMessage.chatID,
                        "group chat",
                        entityMessage.chatID.toString(),
                        entityMessage.text
                    )
                )
            } // system message
            auth.currentUser!!.uid -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.newMessage(entityMessage)
                    val list = getAllChat().value
                    if(list == null)
                        newChat(chat)
                    else if(list.none { it.users == chat.users })
                        newChat(chat)
                }
            } // new message send
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val message = entityMessage.copy()
                    message.fromID = repository.getUser(entityMessage.fromID)[0].name
                    repository.newMessage(message)
                    val list = getAllChat().value
                    if(list == null || list.none { it.id == entityMessage.chatID }) {
                        val users = "${entityMessage.fromID}%${auth.currentUser!!.uid}"
                        val randID = entityMessage.chatID
                        val name = message.fromID
                        val tempChat = EntityChat(randID, name, entityMessage.fromID, users)
                        newChat(tempChat)
                    }
                }
            } // new message receive
        }
    }
    fun updateMessage(entityMessage: EntityMessage){
        viewModelScope.launch(Dispatchers.IO) { repository.updateMessage(entityMessage) }
    }
    fun getAllChat() = repository.readAllChat
    private fun newChat(entityChat: EntityChat){
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

    //fun newUser(user: EntityUser){viewModelScope.launch(Dispatchers.IO) { repository.newUser(user) }}
    fun updateUser(user: EntityUser){
        viewModelScope.launch(Dispatchers.IO) { repository.updateUser(user) }
    }
}