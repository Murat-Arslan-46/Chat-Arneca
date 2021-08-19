package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData

class SharedRepository(private val sharedDao: SharedDao) {

    fun getMessage(id: Int) = sharedDao.getMessage(id)

    fun getChatMessage(id : Int) : LiveData<List<EntityMessage>> = sharedDao.getChatMessage(id)

    val allChatWithLastMessage: LiveData<List<EntityMessage>> = sharedDao.allChatWithLastMessage()

    suspend fun newMessage(entityMessage: EntityMessage){sharedDao.newMessage(entityMessage)}

    //fun deleteMessage(entityMessage: EntityMessage){sharedDao.deleteMessage(entityMessage)}

    suspend fun updateMessage(entityMessage: EntityMessage){sharedDao.updateMessage(entityMessage)}

    val readAllChat: LiveData<List<EntityChat>> = sharedDao.getChat()

    fun readSingleChat(id: Int) = sharedDao.getSingleChat(id)

    suspend fun newChat(entityChat: EntityChat){sharedDao.newChat(entityChat)}

    fun deleteChat(entityChat: EntityChat){
        if(entityChat.users.split("%").size <= 2)
            sharedDao.deleteChat(entityChat)
        sharedDao.deleteMessageWithChat(entityChat.id)
    }

    suspend fun updateChat(entityChat: EntityChat){sharedDao.updateChat(entityChat)}

    suspend fun newUser(entityUser: EntityUser){sharedDao.newUser(entityUser)}
    suspend fun deleteUser(entityUser: EntityUser){sharedDao.deleteUser(entityUser)}
    suspend fun updateUser(user: EntityUser){sharedDao.updateUser(user)}
    val getUsers : LiveData<List<EntityUser>> = sharedDao.getUsers()
    fun getUser(id: String) = sharedDao.getUser(id)
}
