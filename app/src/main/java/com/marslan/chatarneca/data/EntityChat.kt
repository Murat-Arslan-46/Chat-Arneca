package com.marslan.chatarneca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatDB")
data class EntityChat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = -1,
    var chatName: String = "chat",
    val toRef: String = "null",
    var users: String = "null",
    var manager: Boolean = false
)
