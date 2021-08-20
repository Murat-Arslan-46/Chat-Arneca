package com.marslan.chatarneca.fragments.contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.User
import com.marslan.chatarneca.databinding.FragmentContactBinding
import java.lang.Exception
import kotlin.collections.ArrayList

class ContactFragment : Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var adapter: ContactAdapter
    private lateinit var users: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        users = arrayListOf()
        adapter = if(!viewModel.getGroupFlag())
                    ContactAdapter(arrayListOf(),this::newChat)
                else
                    ContactAdapter(arrayListOf(),this::selectUser)
        viewModel.getFirebaseDatabase().getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Log.d("get failed","user list")
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null)
                        snapshot.getValue<ArrayList<User>>()?.apply {
                            val temp = arrayListOf<User>()
                            this.forEach {
                                try {
                                    if (it.id != viewModel.getAuth().currentUser!!.uid)
                                        temp.add(it)
                                }catch (e : Exception){}
                            }
                            adapter.currentList = temp
                            adapter.notifyDataSetChanged()
                        }
                }
            }
        )
        setHasOptionsMenu(true)
        binding.userList.adapter = adapter
        return binding.root
    }override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_menu, menu)
        if (!viewModel.getGroupFlag())
            menu.getItem(0).setVisible(false)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.contact_menu_create_chat -> {
                newGroup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun newChat(toRef: String) {
        val users = "${viewModel.getAuth().currentUser!!.uid}%$toRef"
        val randID = (1000..9999).random()
        val name = "chat"
        var chat = EntityChat(randID, name, toRef, users)
        viewModel.getAllChat().value?.forEach {
            val list = it.users.split("%")
            if (list.size <=2 && (list[0] == toRef || list[1] == toRef))
                chat = it
        }
        viewModel.setCurrentChat(chat)
        findNavController().navigateUp()
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
    private fun selectUser(toRef: String){
        if(users.none() { it == toRef }) {
            users.add(toRef)
        }
        else{
            users.remove(toRef)
        }
    }
    private fun newGroup(){
        var userList = viewModel.getAuth().currentUser!!.uid
        users.forEach { userList += "%$it" }
        val randID = (1000..9999).random()
        val name = "chat"
        var chat = EntityChat(randID, name, randID.toString(), userList)
        if(users.size < 2) {
            viewModel.getAllChat().value?.forEach {
                val list = it.users.split("%")
                if (list[0] == users[0] || list[1] == users[0])
                    chat = it
            }
        }
        else{
            users.add(viewModel.getAuth().uid.toString())
            users.forEach {
                viewModel.getFirebaseDatabase()
                    .getReference(it).push().setValue(EntityMessage(
                        text = userList,
                        fromID = "-1",
                        chatID = randID
                    ))
            }
            chat.manager = true
        }
        viewModel.setCurrentChat(chat)
        findNavController().navigateUp()
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
}