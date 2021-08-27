package com.marslan.chatarneca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatDB")
data class EntityChat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = -1,
    var name: String,
    val toRef: String,
    var users: String,
    var description: String,
    var imageSrc: String = "null",
    var manager: Boolean = false
)
