package com.marslan.chatarneca.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marslan.chatarneca.R
import com.marslan.chatarneca.databinding.FragmentLoginBinding
import androidx.lifecycle.ViewModelProvider


class LoginFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        splash()
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return (binding.root)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding.loginSubmit.setOnClickListener {
            signIn()
        }
        binding.loginCreate.setOnClickListener {
            createUser()
        }
    }
    private fun splash(){
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(viewModel.getUser().currentUser != null){
                openApp()
            }
            else{
                binding.loginLogo.visibility = View.INVISIBLE
            }
        },1000)
    }
    private fun openApp(){
        parentFragmentManager.beginTransaction()
            .replace(R.id.container,MainFragment())
            .commit()
    }
    private fun signIn(){
        viewModel.getUser().signInWithEmailAndPassword(
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
    private fun createUser(){
        viewModel.getUser().createUserWithEmailAndPassword(
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