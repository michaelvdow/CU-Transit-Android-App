package com.apps.michaedow.cutransit.main_activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.SuggestionsAdapter
import com.apps.michaedow.cutransit.database.Stops.StopDao
import com.apps.michaedow.cutransit.database.Stops.StopDatabase
import com.apps.michaedow.cutransit.databinding.FragmentTabsBinding
import com.google.android.material.tabs.TabLayout

class TabFragment: Fragment(), SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private lateinit var binding: FragmentTabsBinding
    private lateinit var stopDao: StopDao
    private lateinit var prefs: SharedPreferences
    private lateinit var suggestionsAdapter: SuggestionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tabs, container, false)

        // Setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
        (activity as AppCompatActivity).title = ("CU Transit")
        setHasOptionsMenu(true)

        setupTabs()
        return binding.root
    }

    // Run things that need context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val database = StopDatabase.getDatabase(context)
        stopDao = database.stopDao()
    }

    private fun createSearch(menu: Menu?) {
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        val autoCompleteView = searchView?.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        searchView.setOnQueryTextListener(this)
        searchView.setOnSuggestionListener(this)
//        TODO: FINISH SETTING UP SEARCH
//        val cursor: Cursor? = stopDao.getCursor("TEMPORARY SEARCH")
//        suggestionsAdapter = SuggestionsAdapter(this, cursor)
//        searchView.suggestionsAdapter = suggestionsAdapter
    }

    private fun setupTabs() {
        val tabLayout = binding.slidingTabs
        val viewPager = binding.viewpager

        tabLayout.addTab(tabLayout.newTab().setText(R.string.near_me_fragment))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favorites_fragment))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_fragment))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ViewPagerAdapter(childFragmentManager, tabLayout.tabCount)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.metric)?.isChecked = prefs.getBoolean("metric", false)
        menu.findItem(R.id.darkTheme)?.isChecked = prefs.getBoolean("darkTheme", false)
        createSearch(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.metric) {
            val editor = prefs.edit()
            editor.putBoolean("metric", !item.isChecked)
            editor.apply()
            item.isChecked = !item.isChecked
        } else if (id == R.id.darkTheme) {
            val editor = prefs.edit()
            editor.putBoolean("darkTheme", !item.isChecked)
            editor.apply()
            item.isChecked = !item.isChecked

            // Restart activity to enable dark theme
            val intent = activity?.intent
            activity?.finish()
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuggestionClick(position: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}