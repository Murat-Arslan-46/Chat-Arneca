package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage

@Dao
interface SharedDao {

    @Insert(entity =EntityMessage::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newMessage(entityMessage : EntityMessage)

    @Delete
    fun DeleteMessage(entityMessage: EntityMessage)

    @Query("select * from messageDB where chatID in (:mid)")
    fun getChatMessage(mid: Int) : LiveData<List<EntityMessage>>

    @Query("select * from chatDB")
    fun getChat() : LiveData<List<EntityChat>>

    @Insert(entity = EntityChat::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newChat(entityChat: EntityChat)

    @Query("update chatDB set lastMessage = :msg , lastDate = :date where chatID in (:id)")
    fun updateChat(msg : String, date: String, id: Int)
}