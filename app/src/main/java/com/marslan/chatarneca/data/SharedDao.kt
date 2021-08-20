package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SharedDao {

    @Insert(entity = EntityMessage::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun newMessage(entityMessage : EntityMessage)

    @Query("select * from messageDB where ref  = :ref")
    suspend fun getMessage(ref: String) : List<EntityMessage>

    @Query("select * from messageDB")
    fun getAllMessage() : LiveData<List<EntityMessage>>

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

    @Insert(entity = EntityUser::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun newUser(entityUser: EntityUser)

    @Insert(entity = EntityUser::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(entityUser: EntityUser)

    @Delete(entity = EntityUser::class)
    suspend fun deleteUser(entityUser: EntityUser)

    @Query("select * from UserDB")
    fun getUsers() : LiveData<List<EntityUser>>

    @Query("select * from UserDB where id = :id")
    fun getUser(id: String) : List<EntityUser>

}