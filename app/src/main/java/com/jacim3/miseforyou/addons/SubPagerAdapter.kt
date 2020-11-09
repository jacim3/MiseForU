package com.jacim3.miseforyou.addons

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jacim3.miseforyou.fragments.SubFragment1
import com.jacim3.miseforyou.fragments.SubFragment2

private const val NUM_ITEMS = 2

class SubPagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {

    override fun getItemCount(): Int {
        return NUM_ITEMS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            0 -> {
                SubFragment1.newInstance("123","456")
            }
            1 -> {
                SubFragment2.newInstance("789","123")
            }
            else -> Fragment()
        }
    }
}