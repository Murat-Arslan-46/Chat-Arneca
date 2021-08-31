package com.marslan.chatarneca.data

import androidx.lifecycle.LiveData

class SharedRepository(private val sharedDao: SharedDao) {

    val getMessageWithSendFalse = sharedDao.getMessageWithSendFalse()
    suspend fun getMessage(ref: String) = sharedDao.getMessage(ref)
    fun getChatMessage(id : Int) = sharedDao.getChatMessage(id)
    val getMessageLastForChatList = sharedDao.getMessageLastForChatList()
    suspend fun newMessage(entityMessage: EntityMessage){sharedDao.newMessage(entityMessage)}
    fun deleteMessage(entityMessage: EntityMessage){sharedDao.deleteMessage(entityMessage)}
    fun deleteMessageWithChat(id: Int){ sharedDao.deleteMessageWithChat(id) }
    suspend fun updateMessage(entityMessage: EntityMessage){sharedDao.updateMessage(entityMessage)}

    val readAllChat: LiveData<List<EntityChat>> = sharedDao.getChat()
    fun readSingleChat(id: Int) = sharedDao.getSingleChat(id)
    suspend fun newChat(entityChat: EntityChat){sharedDao.newChat(entityChat)}
    fun deleteChat(entityChat: EntityChat){ sharedDao.deleteChat(entityChat) }
    suspend fun updateChat(entityChat: EntityChat){sharedDao.updateChat(entityChat)}

    suspend fun newUser(entityUser: EntityUser){sharedDao.newUser(entityUser)}
    suspend fun deleteUser(entityUser: EntityUser){sharedDao.deleteUser(entityUser)}
    suspend fun updateUser(user: EntityUser){sharedDao.updateUser(user)}
    val getUsers = sharedDao.getUsers()
    fun getUser(id: String) = sharedDao.getUser(id)
}
