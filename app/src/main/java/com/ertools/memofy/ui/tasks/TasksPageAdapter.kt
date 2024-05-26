package com.ertools.memofy.ui.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TasksPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TasksPageFragment.newInstance(0)
            1 -> TasksPageFragment()
            else -> TasksPageFragment.newInstance(1)
        }
    }
}