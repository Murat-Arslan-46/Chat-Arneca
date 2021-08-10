package com.marslan.chatarneca.data.messagedb

import androidx.lifecycle.LiveData

class MessageRepository(private val messageDao: MessageDao) {
    val readAllData : LiveData<List<EntityMessage>> = messageDao.readAllData()

    suspend fun newMessage(entityMessage: EntityMessage){
        messageDao.newMessage(entityMessage)
    }
}