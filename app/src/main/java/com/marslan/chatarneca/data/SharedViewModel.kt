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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

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
                var tempChat = try{
                    getSingleChat(entityMessage.chatID)[0]
                } catch (e: Exception){
                    EntityChat(
                        entityMessage.chatID,
                        entityMessage.seenList,
                        entityMessage.text,
                        entityMessage.date,
                    )
                }
                when(entityMessage.text){
                    "send_success"->{
                        var list = repository.getMessageSendList(entityMessage.id)[0]
                        list += "%${entityMessage.sendList}"
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
                    repository.newMessage(entityMessage)
                    val users = "${entityMessage.fromID}%${auth.currentUser!!.uid}"
                    val randID = entityMessage.chatID
                    val name = entityMessage.fromID
                    val tempChat = EntityChat(randID, name, entityMessage.fromID, users)
                    val list = getAllChat().value
                    if(list == null)
                        newChat(tempChat)
                    else if(list.none { it.id == randID })
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
}