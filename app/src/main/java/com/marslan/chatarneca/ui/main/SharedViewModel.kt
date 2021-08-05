package com.marslan.chatarneca.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SharedViewModel : ViewModel() {
    private val chatID = MutableLiveData<Int>()
    private val auth = Firebase.auth
    private val database = Firebase.database

    fun chat(): LiveData<Int> = chatID
    fun getDatabase() = database
    fun getUser() = auth
    fun setChat(id: Int){
        chatID.value = id
    }
}