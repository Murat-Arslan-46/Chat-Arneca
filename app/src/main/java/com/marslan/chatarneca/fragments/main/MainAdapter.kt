package com.marslan.chatarneca.fragments.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.marslan.chatarneca.R

class MainAdapter(
    list: ArrayList<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm,lifecycle) {

    private val fragmentList = list

    val tittle = listOf(
        R.string.chat,
        R.string.group,
        R.string.contact,
        R.string.home
    )

    val icon = listOf(
        R.drawable.tab_ic_chat,
        R.drawable.tab_ic_group,
        R.drawable.tab_ic_conact,
        R.drawable.tab_ic_home
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}