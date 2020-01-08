package com.apps.michaeldow.cutransitcompanion.views.route.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentRouteListBinding

class RouteListFragment: Fragment() {

    private lateinit var viewModel: RouteListViewModel
    private lateinit var binding: FragmentRouteListBinding
    private lateinit var adapter: RouteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_route_list, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(RouteListViewModel::class.java)
        observeViewModel(viewModel)
        viewModel.departure = arguments?.getSerializable("departure") as Departure

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view
        val recyclerView = binding.routeList
        adapter = RouteListAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        adapter.setRouteColor(viewModel.departure.route.route_color)
        adapter.setRouteTextColor(viewModel.departure.route.route_text_color)

        viewModel.getStops()
    }

    private fun observeViewModel(viewModel: RouteListViewModel) {
        viewModel.stopTimes.observe(viewLifecycleOwner, Observer { stopTimes ->
            if (stopTimes != null) {
                adapter.setStops(stopTimes)
                for (i in stopTimes.indices) {
                    if (stopTimes[i].stop_point.stop_id == viewModel.departure.stop_id) {
                        binding.routeList.scrollToPosition(i)
                        break
                    }
                }
            }
        })
    }
}