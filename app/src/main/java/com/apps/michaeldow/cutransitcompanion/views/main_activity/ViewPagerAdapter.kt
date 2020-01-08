package com.apps.michaeldow.cutransitcompanion.views.main_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites.FavoritesFragment
import com.apps.michaeldow.cutransitcompanion.views.main_activity.map.BusMapFragment
import com.apps.michaeldow.cutransitcompanion.views.main_activity.near_me.NearMeFragment

class ViewPagerAdapter(fm: FragmentManager, internal var totalTabs: Int) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return NearMeFragment()
            1 -> return FavoritesFragment()
            else -> return BusMapFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}