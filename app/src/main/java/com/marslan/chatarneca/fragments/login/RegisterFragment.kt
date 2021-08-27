package com.marslan.chatarneca.fragments.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.marslan.chatarneca.MainActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.FragmentRegisterBinding

class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        binding.registerSubmit.setOnClickListener {
            createUser(Firebase.auth)
        }
        return binding.root
    }
    private fun createUser(auth: FirebaseAuth){
        if(binding.registerUserMail.text.isEmpty()) {
            Toast.makeText(requireActivity(),"mail please", Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.registerUserName.startAnimation(anim)
            return
        }
        if(binding.registerUserName.text.isEmpty()) {
            Toast.makeText(requireActivity(),"name please", Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.registerUserName.startAnimation(anim)
            return
        }
        if(binding.registerPassword.text.isEmpty()) {
            Toast.makeText(requireActivity(),"password please", Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.registerPassword.startAnimation(anim)
            return
        }
        if(binding.registerPhone.text.isEmpty()) {
            Toast.makeText(requireActivity(),"phone please", Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.registerPhone.startAnimation(anim)
            return
        }
        auth.createUserWithEmailAndPassword(
            binding.registerUserMail.text.toString(),
            binding.registerPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = EntityUser(
                    auth.uid.toString(),
                    binding.registerUserName.text.toString(),
                    binding.registerUserMail.text.toString(),
                    binding.registerPhone.text.toString(),
                    "null",
                    binding.registerUserMail.text.toString(),
                    "hi there!"
                )
                Firebase.database.getReference(requireActivity().getString(R.string.firebaseUserRef)).child(user.id).setValue(user)
                    .addOnSuccessListener {
                        viewModel.newUser(user)
                        openApp()
                    }
            }
            else {
                Toast.makeText(requireActivity(),task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openApp(){
        val activity = Intent(requireActivity(), MainActivity::class.java)
        startActivity(activity)
        requireActivity().finish()
    }

}