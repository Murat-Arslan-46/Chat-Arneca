package com.marslan.chatarneca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserDB")
data class EntityUser(
    @PrimaryKey
    val id : String,
    val name: String,
    val mail: String,
    val phone: String
)
