package com.apps.michaeldow.cutransitcompanion.views.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.views.route.list.RouteListFragment
import com.apps.michaeldow.cutransitcompanion.views.route.map.RouteMapFragment

class RouteViewPagerAdapter(fm: FragmentManager, internal var totalTabs: Int, val departure: Departure) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                val fragment = RouteMapFragment()
                val args = Bundle()
                args.putSerializable("departure", departure)
                fragment.arguments = args
                return fragment
            }
            else -> {
                val fragment = RouteListFragment()
                val args = Bundle()
                args.putSerializable("departure", departure)
                fragment.arguments = args
                return fragment
            }
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}