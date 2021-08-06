package com.marslan.chatarneca.data

import com.marslan.chatarneca.R
import com.marslan.chatarneca.databinding.ItemMessageReceiveBinding
import com.marslan.chatarneca.databinding.ItemMessageSendBinding
import com.xwray.groupie.databinding.BindableItem

class SendMessageItem(private val message: Message) : BindableItem<ItemMessageSendBinding>() {
    override fun getLayout(): Int {
        return R.layout.item_message_send
    }

    override fun bind(viewBinding: ItemMessageSendBinding, position: Int) {
        viewBinding.message = message
    }
}

class ReceiveMessageItem(private val message: Message) : BindableItem<ItemMessageReceiveBinding>() {
    override fun getLayout(): Int {
        return R.layout.item_message_receive
    }

    override fun bind(viewBinding: ItemMessageReceiveBinding, position: Int) {
        viewBinding.message = message
    }
}