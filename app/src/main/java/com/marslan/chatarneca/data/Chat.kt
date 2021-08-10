package com.marslan.chatarneca.data

import com.marslan.chatarneca.data.messagedb.EntityMessage

data class Chat(
    val id: String = "-1",
    val userList: ArrayList<String> = arrayListOf(),
    val messageList: ArrayList<EntityMessage> = arrayListOf(),
    var name: String = ""
)
