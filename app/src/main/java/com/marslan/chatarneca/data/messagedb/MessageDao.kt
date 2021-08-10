package com.marslan.chatarneca.data.messagedb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newMessage(entityMessage : EntityMessage)

    @Delete
    fun DeleteMessage(entityMessage: EntityMessage)

    @Query("select * from messageDB")
    fun readAllData() : LiveData<List<EntityMessage>>

}