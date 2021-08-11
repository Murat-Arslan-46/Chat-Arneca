package com.marslan.chatarneca.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage

@Database(entities = [EntityMessage::class, EntityChat::class], version = 1)
abstract class SharedDatabase : RoomDatabase() {

    abstract fun messageDao(): SharedDao

    companion object {
        @Volatile
        private var INSTANCE: SharedDatabase? = null

        fun getDatabase(context: Context): SharedDatabase {
            val temp = INSTANCE
            if(temp != null){
                return temp
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SharedDatabase::class.java,
                    "arneca_chat_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}