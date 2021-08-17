package com.marslan.chatarneca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.ActivityLoginBinding
import android.view.animation.Animation

import android.view.animation.AlphaAnimation
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.data.User


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
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
        Handler(Looper.getMainLooper()).postDelayed( {
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
        if(binding.loginUserName.text.isEmpty()) {
            Toast.makeText(this,"mail please",Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.loginUserName.startAnimation(anim)
            return
        }
        if(binding.loginPassword.text.isEmpty()) {
            Toast.makeText(this,"password please",Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.loginPassword.startAnimation(anim)
            return
        }
        binding.loginLogo.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(
            binding.loginUserName.text.toString(),
            binding.loginPassword.text.toString()
        ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")
                    openApp()
                } else {
                    binding.loginLogo.visibility = View.INVISIBLE
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                }
            }
    }
    private fun createUser(auth: FirebaseAuth){
        if(binding.loginUserName.text.isEmpty()) {
            Toast.makeText(this,"mail please",Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.loginUserName.startAnimation(anim)
            return
        }
        if(binding.loginPassword.text.isEmpty()) {
            Toast.makeText(this,"password please",Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.loginPassword.startAnimation(anim)
            return
        }
        auth.createUserWithEmailAndPassword(
            binding.loginUserName.text.toString(),
            binding.loginPassword.text.toString()
        ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(
                        auth.uid.toString(),
                        "name",
                        binding.loginUserName.text.toString(),
                        "111-222-33-44"
                    )
                    viewModel.getFirebaseDatabase().getReference("users").get().addOnSuccessListener{
                        if(it.value != null){
                            var value = it.getValue<ArrayList<User>>()
                            if(value != null)
                                value.add(user)
                            else
                                value = arrayListOf(user)
                            viewModel.getFirebaseDatabase().getReference("users").setValue(value)
                        }
                    }
                     openApp()
                }
                else {
                    Toast.makeText(this,task.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
    }
}