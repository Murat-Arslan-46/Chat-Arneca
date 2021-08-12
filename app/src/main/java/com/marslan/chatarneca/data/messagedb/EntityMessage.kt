package com.marslan.chatarneca.data.messagedb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messageDB")
data class EntityMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val text: String = "",
    val date: String = "00/00/00 00:00",
    val fromID: String = "0",
    val chatID: Int = 1,
    val isRead: Boolean = false
)
