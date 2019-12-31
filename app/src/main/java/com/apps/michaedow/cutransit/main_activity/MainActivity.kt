package com.apps.michaedow.cutransit.main_activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.mainToolbar)
        setupTabs()
        requestPermissions()
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        }

    }

    fun setupTabs() {
        val tabLayout = binding.slidingTabs
        val viewPager = binding.viewpager

        tabLayout.addTab(tabLayout.newTab().setText(R.string.near_me_fragment))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favorites_fragment))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_fragment))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ViewPagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem =  tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}
