package com.marslan.chatarneca.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.marslan.chatarneca.MainActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        val auth = Firebase.auth
        splash(auth)
        binding.loginSubmit.setOnClickListener {
            signIn(auth)
        }
        binding.loginCreate.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
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
        val activity = Intent(requireActivity(), MainActivity::class.java)
        startActivity(activity)
        requireActivity().finish()
    }
    private fun signIn(auth: FirebaseAuth){
        if(binding.loginUserName.text.isEmpty()) {
            Toast.makeText(requireActivity(),"mail please", Toast.LENGTH_LONG).show()
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 50
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.RELATIVE_TO_SELF
            binding.loginUserName.startAnimation(anim)
            return
        }
        if(binding.loginPassword.text.isEmpty()) {
            Toast.makeText(requireActivity(),"password please", Toast.LENGTH_LONG).show()
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
                Toast.makeText(requireActivity(),task.exception.toString(), Toast.LENGTH_LONG).show()
                Log.w("TAG", "createUserWithEmail:failure", task.exception)
            }
        }
    }
}