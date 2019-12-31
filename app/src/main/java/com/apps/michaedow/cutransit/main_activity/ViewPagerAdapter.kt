package com.apps.michaedow.cutransit.main_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.apps.michaedow.cutransit.main_activity.near_me.NearMeFragment

class ViewPagerAdapter(fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NearMeFragment()
            1 -> return NearMeFragment()
            else -> return NearMeFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}