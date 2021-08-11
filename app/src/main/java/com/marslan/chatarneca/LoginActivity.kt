package com.marslan.chatarneca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.chatdb.EntityChat
import com.marslan.chatarneca.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        val auth = viewModel.getAuth()
        splash(auth)
        binding.loginSubmit.setOnClickListener {
            signIn(auth)
        }
        binding.loginCreate.setOnClickListener {
            createUser(auth)
        }
    }
    private fun splash(auth: FirebaseAuth){
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(auth.currentUser != null){
                openApp()
            }
            else{
                binding.loginLogo.visibility = View.INVISIBLE
            }
        },2000)
    }
    private fun openApp(){
        val activity = Intent(this,MainActivity::class.java)
        startActivity(activity)
        finish()
    }
    private fun signIn(auth: FirebaseAuth){
        auth.signInWithEmailAndPassword(
            binding.loginUserName.text.toString(),
            binding.loginPassword.text.toString()
        )
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    openApp()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                }
            }
    }
    private fun createUser(auth: FirebaseAuth){
        auth.createUserWithEmailAndPassword(
            binding.loginUserName.text.toString(),
            binding.loginPassword.text.toString()
        )
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                }
            }
    }
}