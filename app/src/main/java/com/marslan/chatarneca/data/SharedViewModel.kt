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
    fun getUserIndex() = userIndex
    fun setUserIndex(index: Int){userIndex = index}
    fun getCurrentChat() = chat
    fun setCurrentChat(newChat: EntityChat){ chat = newChat }
    fun getMessageWithChatID(id: Int) = repository.getChatMessage(id)
    fun getAllChatWithLastMessage() = repository.allChatWithLastMessage
    fun newMessage(entityMessage: EntityMessage){
        when (entityMessage.fromID) {
            "-1" -> {
                when(entityMessage.text){
                    "send_success"->{
                        viewModelScope.launch(Dispatchers.IO) {
                            val currentChat = repository.readSingleChat(entityMessage.chatID)[0]
                            val currentMessage = repository.getMessage(entityMessage.id)[0]
                            val userSize = currentChat.users.split("%").size
                            currentMessage.sendList += "%${entityMessage.sendList}"
                            val sendSize = currentMessage.sendList.split("%").size
                            if(sendSize+1 == userSize){
                                db.getReference(currentChat.toRef).child(entityMessage.ref).setValue(null)
                                currentMessage.send = true
                            }
                            repository.updateMessage(currentMessage)

                        }
                    }
                    "seen_success"->{

                    }
                    else ->{
                        val ref =db.getReference("users")
                            .child(userIndex.toString())
                            .child("listenerRef")
                        ref.get().addOnSuccessListener {
                            if(it.value != null){
                                var list = arrayListOf<String>()
                                    if(it.getValue<ArrayList<String>>() != null)
                                        list = it.getValue<ArrayList<String>>()!!
                                list.add(entityMessage.text)
                                ref.setValue(list)
                            }
                        }
                        newChat(
                            EntityChat(
                            entityMessage.chatID,
                            "group chat",
                            entityMessage.chatID.toString(),
                            entityMessage.sendList
                        )
                        )
                    }
                }
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
                }
                val list = getAllChat().value
                if(list == null || list.none { it.id == entityMessage.chatID }) {
                    val users = "${entityMessage.fromID}%${auth.currentUser!!.uid}"
                    val randID = entityMessage.chatID
                    val name = entityMessage.fromID
                    val tempChat = EntityChat(randID, name, entityMessage.fromID, users)
                    newChat(tempChat)
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

    fun getSingleChat(id: Int) = repository.readSingleChat(id)

    fun deleteChat(entityChat: EntityChat){
        viewModelScope.launch(Dispatchers.IO) { repository.deleteChat(entityChat) }
    }
    fun updateChat(entityChat: EntityChat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChat(entityChat)
        }
    }

    fun newUser(user: EntityUser){
        viewModelScope.launch(Dispatchers.IO) { repository.newUser(user) }
    }
    fun updateUser(user: EntityUser){
        viewModelScope.launch(Dispatchers.IO) { repository.updateUser(user) }
    }
}