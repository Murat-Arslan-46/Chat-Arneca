package com.marslan.chatarneca.data

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.marslan.chatarneca.MainActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.data.messagedb.EntityMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application): AndroidViewModel(application) {
    private val readAllChat: LiveData<List<EntityChat>>
    private val repository : SharedRepository
    private val auth : FirebaseAuth
    private val db : FirebaseDatabase
    private var chat : EntityChat

    init {
        val messageDao = SharedDatabase.getDatabase(application).messageDao()
        repository = SharedRepository(messageDao)
        readAllChat = repository.readAllChat
        auth = Firebase.auth
        db = Firebase.database
        chat = EntityChat()
    }
    fun listenerOpen(context: Context){
        getDB().getReference(auth.currentUser!!.uid)
            .addChildEventListener(object: ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.value != null) {
                        val message = snapshot.getValue<EntityMessage>()
                        if (message != null) {
                            newMessage(message,message.fromID)
                            getDB()
                                .getReference(auth.currentUser!!.uid).removeValue()
                            notification(context,message)
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("change data",";)")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("cancel firebase",";)")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("moved child",";)")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d("empty firebase",";)")
                }
            })
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun notification(context: Context, message: EntityMessage){
        createNotificationChannel(context)
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,0)
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(message.fromID)
            setContentText(message.text)
            setContentIntent(pendingIntent)
            setStyle(NotificationCompat.BigTextStyle().bigText(message.text))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }
    @SuppressLint("WrongConstant")
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "${context.packageName}-${context.getString(R.string.app_name)}",
                context.getString(R.string.app_name),
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
            channel.description = "App notification channel."
            channel.setShowBadge(false)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun getAuth() = auth
    fun getDB() = db
    fun getChat() = chat
    fun setChat(newChat: EntityChat){ chat = newChat }
    fun getMessage(id: Int) : LiveData<List<EntityMessage>> {
        viewModelScope.launch(Dispatchers.IO) {
            try{repository.updateChat(false,id)}
            catch (e: Exception){Log.e("read fail", e.message.toString())}
        }
        return repository.getChatMessage(id)
    }
    fun newMessage(entityMessage: EntityMessage,toID: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.newMessage(entityMessage,toID)
        }
    }
    fun getChatList() = readAllChat
}