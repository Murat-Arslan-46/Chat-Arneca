package com.marslan.chatarneca

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.User
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.getFirebaseDatabase().getReference(viewModel.getAuth().uid.toString())
            .addChildEventListener(listener())
        viewModel.getFirebaseDatabase().getReference(getString(R.string.firebaseUserRef))
            .addChildEventListener(object : ChildEventListener{
                override fun onCancelled(error: DatabaseError) {}
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.value != null){
                        val user = snapshot.getValue<User>()
                        if(user != null){
                            viewModel.updateUser(
                                EntityUser(
                                    user.id,
                                    user.name,
                                    user.mail,
                                    user.phone,
                                    user.imageSrc,
                                    user.userName,
                                    user.description
                                )
                            )
                        }
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.value != null){
                        val user = snapshot.getValue<User>()
                        if(user != null){
                            viewModel.updateUser(
                                EntityUser(
                                    user.id,
                                    user.name,
                                    user.mail,
                                    user.phone,
                                    user.imageSrc,
                                    user.userName,
                                    user.description
                                )
                            )
                        }
                    }
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
        viewModel.getAllChat().observe(this,{
            it.forEach { chat ->
                if(chat.id.toString() == chat.toRef){
                    viewModel.getFirebaseDatabase().getReference(chat.toRef)
                        .addChildEventListener(listener())
                }
            }
        })
        viewModel.getMessageWithSendFalse().observe(this,{
            it.forEach { message ->
                if(!message.send){
                    viewModel.getFirebaseDatabase()
                        .getReference("${message.chatID}-${message.fromID}")
                        .addChildEventListener(listenerStatus())
                }
            }
        })
        supportFragmentManager.findFragmentById(R.id.fragment)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
        val appDir = File(Environment.getExternalStorageDirectory(), "ChatApp")
            .apply {
                if (!exists())
                    mkdir()
            }
        val imageDir = File(appDir, "image")
            .apply {
                if (!exists())
                    mkdir()
            }
        viewModel.setAppDir(imageDir)
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notification(message: EntityMessage){
        createNotificationChannel()
        val channelId = "${packageName}-${getString(R.string.app_name)}"
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,0)
        val notificationBuilder = NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(message.fromID)
            setContentText(message.text)
            setContentIntent(pendingIntent)
            setStyle(NotificationCompat.BigTextStyle().bigText(message.text))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1001, notificationBuilder.build())
    }
    @SuppressLint("WrongConstant")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "${packageName}-${getString(R.string.app_name)}",
                getString(R.string.app_name),
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
            channel.description = "App notification channel."
            channel.setShowBadge(false)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun listener():ChildEventListener{
        return object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value != null) {
                    val message = snapshot.getValue<EntityMessage>()
                    if (message != null) {
                        viewModel.checkPointNewMessage(message)
                        if( message.fromID != "-1" &&
                            message.fromID != viewModel.getAuth().uid.toString()
                        )
                            notification(message)
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
        }
    }
    private fun listenerStatus():ChildEventListener{
        return object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value != null){
                    val ref = snapshot.key.toString()
                    val count = snapshot.childrenCount.toInt()
                    viewModel.checkMessageSend(ref,count)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value != null){
                    val ref = snapshot.key.toString()
                    val count = snapshot.childrenCount.toInt()
                    viewModel.checkMessageSend(ref,count)
                }
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
        }
    }
}