package com.apps.michaedow.cutransit.main_activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.SuggestionsAdapter
import com.apps.michaedow.cutransit.database.Stops.StopDao
import com.apps.michaedow.cutransit.database.Stops.StopDatabase
import com.apps.michaedow.cutransit.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

}
