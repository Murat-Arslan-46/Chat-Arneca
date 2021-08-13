package com.marslan.chatarneca.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val readAllChat: LiveData<List<EntityChat>>
    private val repository : SharedRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chat : EntityChat?

    init {
        val messageDao = SharedDatabase.getDatabase(application).messageDao()
        repository = SharedRepository(messageDao)
        readAllChat = repository.readAllChat
        auth = Firebase.auth
        db = Firebase.database
        chat = EntityChat()
    }
    fun listenerOpen(){
        getDB().getReference(auth.currentUser!!.uid)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.value != null) {
                        val message = snapshot.getValue<EntityMessage>()
                        if (message != null) {
                            newMessage(message,message.fromID)
                            getDB()
                                .getReference(auth.currentUser!!.uid).removeValue()
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("change data",";)")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("cancel firebase",";)")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("moved child",";)")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("empty firebase",";)")
                }
            })
    }
    fun getAuth() = auth

    fun getDB() = db

    fun getChat() = chat
    fun setChat(newChat: EntityChat?){ chat = newChat }

    fun getMessage(id: Int) : LiveData<List<EntityMessage>> {
        viewModelScope.launch(Dispatchers.IO) {
            try{repository.updateChat(false,id)}
            catch (e: Exception){Log.e("read fail", e.message.toString())}
        }
    return repository.getChatMessage(id)
}

    fun newMessage(entityMessage: EntityMessage,toID: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.newMessage(entityMessage,toID)
        }
    }
    fun getChatList() = readAllChat
}