package com.marslan.chatarneca.fragments.main.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import com.marslan.chatarneca.LoginActivity
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityUser
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentHomeBinding
import com.marslan.chatarneca.fragments.chat.ChatFragment
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentHomeBinding
        private lateinit var viewModel: SharedViewModel
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.getUsers().observe(requireActivity(),{ list ->
            val user = list.filter { it.id == viewModel.getAuth().uid.toString() }[0]
            binding.userName.text = user.name
            binding.userMail.text = user.mail
            binding.userPhone.text = user.phone
            if(user.imageSrc == "null")
                binding.userImage.setImageResource(R.drawable.ic_list_person)
            else{
                val file = File(viewModel.getAppDir(), user.imageSrc)
                if (file.exists()) {
                    val image = Uri.fromFile(file)
                    Log.e("path", file.path)
                    binding.userImage.setImageURI(image)
                }
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sign_out -> {
                viewModel.getAuth().signOut()
                val activity = Intent(requireContext(),LoginActivity::class.java)
                startActivity(activity)
                requireActivity().finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}