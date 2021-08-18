package com.marslan.chatarneca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messageDB")
data class EntityMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    var text: String = "",
    val date: String = "00/00/00 00:00",
    var fromID: String = "0",
    val chatID: Int = 1,
    var iSaw: Boolean = false,
    var seen: Boolean = false,
    var seenList: String = "",
    var send: Boolean = false,
    var sendList: String = ""
)
