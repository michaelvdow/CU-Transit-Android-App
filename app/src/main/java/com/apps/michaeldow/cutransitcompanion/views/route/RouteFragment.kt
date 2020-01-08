package com.apps.michaeldow.cutransitcompanion.views.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentRouteBinding
import com.google.android.material.tabs.TabLayout


class RouteFragment: Fragment() {

    private lateinit var viewModel: RouteViewModel
    private lateinit var binding: FragmentRouteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_route, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(RouteViewModel::class.java)
        arguments?.let {
            val safeArgs = RouteFragmentArgs.fromBundle(it)
            viewModel.departure = safeArgs.departure
        }

        // Setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.routeToolbar)
        (activity as AppCompatActivity).title = (viewModel.departure.headsign)
        setHasOptionsMenu(true)
        (binding.routeToolbar as Toolbar).setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        observeViewModel(viewModel)

        setupTabs()

        return binding.root
    }

    fun observeViewModel(viewModel: RouteViewModel) {

    }

    private fun setupTabs() {
        val tabLayout = binding.routeSlidingTabs
        val viewPager = binding.routeViewpager
        viewPager.offscreenPageLimit = 1

        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_fragment))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.stops))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = RouteViewPagerAdapter(childFragmentManager, tabLayout.tabCount, viewModel.departure)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem =  tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager.currentItem =  tab.position
            }
        })

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                tabLayout.setScrollPosition(position, positionOffset, true)
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }
}