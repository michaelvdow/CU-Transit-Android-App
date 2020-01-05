package com.apps.michaedow.cutransit.main_activity.near_me

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.databinding.FragmentNearMeBinding
import com.apps.michaedow.cutransit.main_activity.MainActivity

class NearMeFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var viewModel: NearMeViewModel
    private lateinit var binding: FragmentNearMeBinding
    private lateinit var adapter: NearMeListAdapter
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_me, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(NearMeViewModel::class.java)
        observeViewModel(viewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view
        val recyclerView = binding.nearMeList
        adapter = NearMeListAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        //Create swipe refresh layout for empty text view
        swipeRefreshLayout = binding.nearMeSwipeRefreshEmptyView
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorAccent
            ), ContextCompat.getColor(context!!, R.color.colorPrimary)
        )
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.setEnabled(true)

        // Setup swipe refresh layout for list view
        swipeRefreshLayout = binding.nearMeSwipeRefreshList
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorAccent
            ), ContextCompat.getColor(context!!, R.color.colorPrimary)
        )
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.setEnabled(true)


        (activity as MainActivity).requestPermission(false)
        viewModel.updateLocation()
    }

    private fun observeViewModel(viewModel: NearMeViewModel) {
        viewModel.location.observe(viewLifecycleOwner, Observer { location ->
            adapter.setLocation(location)
        })

        viewModel.refreshing.observe(viewLifecycleOwner, Observer { refreshing ->
            binding.nearMeSwipeRefreshList?.isRefreshing = refreshing
            binding.nearMeSwipeRefreshEmptyView?.isRefreshing = refreshing
        })

        viewModel.stops.observe(viewLifecycleOwner, Observer { stops ->
            adapter.setStops(stops)
            if (stops.size == 0) {
                binding.nearMeSwipeRefreshList.visibility = View.GONE
                binding.nearMeSwipeRefreshEmptyView.visibility = View.VISIBLE
            } else {
                binding.nearMeSwipeRefreshList.visibility = View.VISIBLE
                binding.nearMeSwipeRefreshEmptyView.visibility = View.GONE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onRefresh() {
        (activity as MainActivity).requestPermission(true)
        viewModel.updateLocation()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, s: String?) {
        if (s?.equals("metric") ?: false) {
            viewModel.updateLocation()
        }
    }

}