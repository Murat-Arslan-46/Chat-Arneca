package com.marslan.chatarneca.data

data class Chat(
    val id: Int = -1,
    val userList: ArrayList<String> = arrayListOf(),
    val messageList: ArrayList<Message> = arrayListOf(),
    var name: String = ""
)
