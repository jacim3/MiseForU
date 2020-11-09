package com.jacim3.miseforyou.addons

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jacim3.miseforyou.fragments.FirstFragment
import com.jacim3.miseforyou.fragments.SecondFragment

private const val NUM_ITEMS = 2

class MyPagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {

    override fun getItemCount(): Int {
        return NUM_ITEMS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FirstFragment.newInstance()
            }
            1 -> {
                SecondFragment.newInstance()
            }
            else -> Fragment()
        }
    }
}


