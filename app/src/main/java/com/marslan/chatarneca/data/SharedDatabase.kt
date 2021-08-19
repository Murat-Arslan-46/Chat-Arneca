package com.marslan.chatarneca.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EntityMessage::class, EntityChat::class, EntityUser::class], version = 1)
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
                    "chat_app_v2_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}