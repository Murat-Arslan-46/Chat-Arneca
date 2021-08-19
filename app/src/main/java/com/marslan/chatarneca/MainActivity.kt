package com.marslan.chatarneca

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.data.User

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.getFirebaseDatabase().getReference("users").get().addOnSuccessListener {
            if(it != null){
                it.getValue<List<User>>()?.forEachIndexed { index , user->
                    try{
                        if (user.id == viewModel.getAuth().currentUser!!.uid) {
                            viewModel.setUserIndex(index)
                            user.listenerRef.forEach { ref ->
                                viewModel.getFirebaseDatabase().getReference(ref)
                                    .addChildEventListener(listener())
                            }
                        }
                        viewModel.updateUser(EntityUser(
                            user.id,
                            user.name,
                            user.mail,
                            user.phone
                        ))
                    }catch (e: Exception){}
                }
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
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
                        viewModel.newMessage(message)
                        if(message.fromID == "-1") //system message
                        {
                            viewModel.getFirebaseDatabase()
                                .getReference(viewModel.getAuth().currentUser!!.uid)
                                .setValue(null)
                            if(message.text != "send_success" && message.text != "seen_success") {
                                viewModel.getFirebaseDatabase()
                                    .getReference(message.text).addChildEventListener(listener())
                            }
                        }
                        else if(message.fromID != viewModel.getAuth().currentUser!!.uid) {
                            notification(message)
                            val ref = message.fromID
                            message.sendList = viewModel.getAuth().currentUser!!.uid
                            message.fromID = "-1"
                            message.text = "send_success"
                            viewModel.getFirebaseDatabase()
                                .getReference(ref).push()
                                .setValue(message)
                        }
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
}