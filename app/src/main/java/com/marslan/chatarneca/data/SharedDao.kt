package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SharedDao {

    @Insert(entity = EntityMessage::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newMessage(entityMessage : EntityMessage)

    @Query("select sendList from messageDB where id in (:id)")
    fun getMessageSendList(id: Int) : List<String>

    @Query("select * from messageDB where chatID in (:mid)")
    fun getChatMessage(mid: Int) : LiveData<List<EntityMessage>>

    @Delete(entity = EntityMessage::class)
    fun deleteMessage(entityMessage: EntityMessage)

    @Query("delete from messageDB where chatID in (:id)")
    fun deleteMessageWithChat(id: Int)

    @Query("select max(id) as id , * from messageDB group by chatID")
    fun allChatWithLastMessage() : LiveData<List<EntityMessage>>

    @Insert(entity = EntityMessage::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(entityMessage: EntityMessage)

    @Insert(entity = EntityChat::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newChat(entityChat: EntityChat)

    @Query("select * from chatDB ")
    fun getChat() : LiveData<List<EntityChat>>

    @Query("select * from chatDB where id = :id ")
    fun getSingleChat(id: Int) : List<EntityChat>

    @Delete(entity = EntityChat::class)
    fun deleteChat(entityChat: EntityChat)

    @Insert(entity = EntityChat::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateChat(entityChat: EntityChat)
}