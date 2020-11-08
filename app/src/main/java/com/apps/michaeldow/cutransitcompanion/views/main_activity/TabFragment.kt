package com.apps.michaeldow.cutransitcompanion.views.main_activity

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentTabsBinding
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.SharedPreferenceKeys
import com.google.android.material.tabs.TabLayout

class TabFragment: Fragment(), SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private lateinit var binding: FragmentTabsBinding
    private lateinit var viewModel: TabFragmentViewModel
    private lateinit var prefs: SharedPreferences
    private var suggestionsAdapter: ArrayAdapter<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tabs, container, false)

        viewModel = ViewModelProviders.of(this).get(TabFragmentViewModel::class.java)

        // Setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
        (activity as AppCompatActivity).title = ("CU Transit")
        setHasOptionsMenu(true)

        setupTabs()

        observeViewModel(viewModel)
//
//        binding.fab.setOnClickListener {
//            val action = TabFragmentDirections.actionTabFragmentToTripPlannerFragment()
//            findNavController().navigate(action)
//        }

        return binding.root
    }

    private fun showChangeLog() {
        if (context != null) {
            val builder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(context as Context, R.style.AlertDialogTheme)
            }

            builder?.setTitle("Release Notes")
                ?.setView(R.layout.dialog_changelog)

            builder?.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            }
            builder?.create()?.show()
        }
    }


    // Run things that need context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun observeViewModel(viewModel: TabFragmentViewModel) {
        viewModel.suggestions.observe(viewLifecycleOwner, Observer { suggestions ->
            if (suggestions != null) {
                suggestionsAdapter?.clear()
                suggestionsAdapter?.addAll(suggestions)
                suggestionsAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.mutableStopId.observe(viewLifecycleOwner, Observer { stopId ->
            if (stopId != null) {
                if (view?.findNavController()?.currentDestination?.id== R.id.tabFragment) {
                    val action = TabFragmentDirections.actionTabFragmentToDeparturesFragment(stopId)
                    view?.findNavController()?.navigate(action)
                    viewModel.mutableStopId.postValue(null)
                }
            }
        })
    }

    private fun createSearch(menu: Menu?) {
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView

        val autoCompleteView = searchView?.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        searchView.setOnQueryTextListener(this)
        searchView.setOnSuggestionListener(this)

        if (context != null) {
            suggestionsAdapter = ArrayAdapter(context as Context, R.layout.list_row_suggestion, ArrayList<String>())
            autoCompleteView.setAdapter(suggestionsAdapter)
        }
    }

    private fun setupTabs() {
        val tabLayout = binding.slidingTabs
        val viewPager = binding.viewpager
        viewPager.offscreenPageLimit = 2

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.metric)?.isChecked = prefs.getBoolean(SharedPreferenceKeys.METRIC, false)
        menu.findItem(R.id.darkTheme)?.isChecked = prefs.getBoolean(SharedPreferenceKeys.DARK_THEME, false)
        createSearch(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.metric) {
            val editor = prefs.edit()
            editor.putBoolean(SharedPreferenceKeys.METRIC, !item.isChecked)
            editor.apply()
            item.isChecked = !item.isChecked
        } else if (id == R.id.darkTheme) {
            val editor = prefs.edit()
            editor.putBoolean(SharedPreferenceKeys.DARK_THEME, !item.isChecked)
            editor.apply()
            item.isChecked = !item.isChecked

            // Restart activity to enable dark theme
            val intent = activity?.intent
            activity?.finish()
            startActivity(intent)
        } else if (id == R.id.releaseNotes) {
            showChangeLog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val tab = binding.slidingTabs.getTabAt(binding.viewpager.currentItem)
        tab?.select()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            viewModel.search(newText.replace(" &", " and"))
        }
        return false
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        if (suggestionsAdapter != null) {
            val stopName = (suggestionsAdapter?.getItem(position) as String).replace("&", "and")
            viewModel.openDepartureFromStopName(stopName)
        }
        return true
    }
}