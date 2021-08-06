package com.marslan.chatarneca.ui.main

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SharedViewModel : ViewModel() {
    private var chatID = "-1"
    private val auth = Firebase.auth
    private val database = Firebase.database

    fun getChat() = chatID
    fun getDatabase() = database
    fun getUser() = auth
    fun setChat(id: String){
        chatID = id
    }
}