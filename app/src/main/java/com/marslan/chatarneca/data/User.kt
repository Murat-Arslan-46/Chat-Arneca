package com.marslan.chatarneca.data

data class User(
    val id: String = "id",
    val name: String = "name",
    val mail: String = "mail",
    val phone: String = "phone",
    val listenerRef: ArrayList<String> = arrayListOf()
)
