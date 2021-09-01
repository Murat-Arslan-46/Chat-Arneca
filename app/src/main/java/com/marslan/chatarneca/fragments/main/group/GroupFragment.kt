package com.marslan.chatarneca.fragments.main.group

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.marslan.chatarneca.R
import com.marslan.chatarneca.data.EntityChat
import com.marslan.chatarneca.data.EntityMessage
import com.marslan.chatarneca.data.SharedViewModel
import com.marslan.chatarneca.databinding.FragmentGroupBinding
import com.marslan.chatarneca.fragments.info.ChatInfoFragment

class GroupFragment : Fragment() {

    companion object{
        private lateinit var binding: FragmentGroupBinding
        private lateinit var viewModel: SharedViewModel
        private lateinit var listAdapter: GroupListAdapter
        private var chat: EntityChat? = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        listAdapter = GroupListAdapter(this::onClick,this::onLongClick)
        viewModel.getAllChat().observe(requireActivity(),{list ->
            val groupList = list.filter { it.id.toString() == it.toRef }
            listAdapter.setCurrentList(groupList)
            listAdapter.setCurrentList(list)
        })
        binding.groupList.adapter = listAdapter
        binding.groupBtnNew.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
        }
        return binding.root
    }

    private fun onClick(entityChat: EntityChat){
        viewModel.setCurrentChat(entityChat)
        setHasOptionsMenu(false)
        chat = null
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }
    private fun onLongClick(entityChat: EntityChat){
        chat = entityChat
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setHasOptionsMenu(false)
        when (item.itemId){
            R.id.delete_group -> {
                if(chat!!.toRef == "0"){
                    viewModel.deleteChat(chat!!)
                }
            }
            R.id.info_group -> {
                viewModel.setCurrentChat(chat!!)
                findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
                findNavController().navigate(R.id.action_chatFragment_to_chatInfoFragment)
            }
            R.id.leave_group -> {
                viewModel.leaveGroup(chat!!)
                val beforeUsers = chat!!.users.split("%")
                var afterUsers = ""
                beforeUsers.forEachIndexed { index, it ->
                    if(index != 0){
                        if (it != viewModel.getAuth().uid.toString())
                            afterUsers += "%$it"
                    }
                    else{
                        if (it != viewModel.getAuth().uid.toString())
                            afterUsers += it
                    }
                }
                chat!!.users = afterUsers
                val message = EntityMessage(
                    text = "${chat!!.name}%${chat!!.description}%${chat!!.imageSrc}",
                    date = chat!!.users,
                    fromID = "-1",
                    chatID = chat!!.id,
                    ref = "-1"
                )
                if(chat!!.manager)
                    message.ref = "-2"
                afterUsers.split("%").forEach {
                    viewModel.getFirebaseDatabase().getReference(it).push().setValue(message)
                }
                Toast.makeText(requireContext(), "leave the group", Toast.LENGTH_SHORT).show()
            }
        }
        chat = null
        return true
    }

}