package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.marslan.chatarneca.databinding.FragmentMainBinding
import com.marslan.chatarneca.fragments.main.contact.ContactFragment
import com.marslan.chatarneca.fragments.main.group.GroupFragment
import com.marslan.chatarneca.fragments.main.chatlist.ChatListFragment
import com.marslan.chatarneca.fragments.main.home.HomeFragment


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MainAdapter

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf(
            ChatListFragment(),
            GroupFragment(),
            ContactFragment(),
            HomeFragment()
        )
        adapter = MainAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.apply {
            vpHome.adapter = adapter
        }
        TabLayoutMediator(binding.tabs,binding.vpHome){tab, position ->
            tab.text = requireActivity().getText(adapter.tittle[position])
            tab.icon = requireActivity().getDrawable(adapter.icon[position])
        }.attach()
        return binding.root
    }
}