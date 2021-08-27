package com.marslan.chatarneca.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.marslan.chatarneca.R
import com.marslan.chatarneca.databinding.FragmentMainBinding
import com.marslan.chatarneca.fragments.contact.ContactFragment
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

        requireActivity().title = getString(R.string.app_name)
        val fragmentList = arrayListOf(
            GroupFragment(),
            ChatListFragment(),
            HomeFragment()
        )
        adapter = MainAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.vpHome.adapter = adapter
        binding.vpHome.currentItem = 1
        TabLayoutMediator(binding.tabs,binding.vpHome){tab, position ->
            tab.icon = requireActivity().getDrawable(adapter.icon[position])
        }.attach()
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            var text = false
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if(text) {
                    text = !text
                    onTabUnselected(tab)
                }
                else {
                    text = !text
                    onTabSelected(tab)
                }
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pagerControl = binding.tabs.selectedTabPosition == binding.vpHome.currentItem
                if(pagerControl && !text)
                    return
                Handler(Looper.getMainLooper()).postDelayed({
                    val index = binding.vpHome.currentItem
                    tab?.text = getString(adapter.title[index])
                    text = true
                }, 10)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Handler(Looper.getMainLooper()).postDelayed({tab?.text = ""},10)
            }
        })
        return binding.root
    }
}