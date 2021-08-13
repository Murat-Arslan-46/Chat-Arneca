package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage

class SharedRepository(private val sharedDao: SharedDao) {

    fun getChatMessage(id : Int) : LiveData<List<EntityMessage>> = sharedDao.getChatMessage(id)

    suspend fun newMessage(entityMessage: EntityMessage, toID: String){
        sharedDao.newMessage(entityMessage)
        newChat(entityMessage,toID)
        sharedDao.updateChat(!entityMessage.isRead,entityMessage.text,entityMessage.date,entityMessage.chatID)
    }

    val readAllChat: LiveData<List<EntityChat>> = sharedDao.getChat()

    private suspend fun newChat(entityMessage: EntityMessage,toID: String){
        sharedDao.newChat(
            EntityChat(
                entityMessage.chatID,
                "chat",
                toID,
                !entityMessage.isRead,
                entityMessage.text,
                entityMessage.date
            )
        )
    }
    fun updateChat(read: Boolean,id: Int){
        sharedDao.updateChatRead(read,id)
    }

}
