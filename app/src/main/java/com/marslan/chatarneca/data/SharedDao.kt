package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import java.util.*

@Dao
interface SharedDao {

    @Insert(entity =EntityMessage::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newMessage(entityMessage : EntityMessage)

    @Insert(entity = EntityChat::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newChat(entityChat: EntityChat)

    @Query("select * from messageDB where chatID in (:mid)")
    fun getChatMessage(mid: Int) : LiveData<List<EntityMessage>>

    @Query("select * from chatDB ")
    fun getChat() : LiveData<List<EntityChat>>

    @Query("delete from messageDB where chatID in (:id)")
    fun deleteMessage(id: Int)

    @Delete(entity = EntityChat::class)
    fun deleteChat(entityChat: EntityChat)

    @Query("update chatDB set isRead = :read, lastMessage = :msg , lastDate = :date where chatID in (:id)")
    fun updateChat(read: Boolean, msg : String, date: String, id: Int)

    @Query("update chatDB set isRead = :read where chatID in (:id)")
    fun updateChatRead(read: Boolean, id: Int)
}