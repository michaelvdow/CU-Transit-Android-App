package com.apps.michaedow.cutransit.departures

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.databinding.FragmentDeparturesBinding

class DeparturesFragment: Fragment(), OnRefreshListener {

    private lateinit var viewModel: DeparturesViewModel
    private lateinit var binding: FragmentDeparturesBinding
    private lateinit var adapter: DeparturesListAdapter
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var favoriteItem: MenuItem? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_departures, container, false)
        binding.lifecycleOwner = this

        // Setup view model
        viewModel = ViewModelProviders.of(this).get(DeparturesViewModel::class.java)
        arguments?.let {
            val safeArgs = DeparturesFragmentArgs.fromBundle(it)
            viewModel.stopName = safeArgs.stopName
        }

        // Setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.departureToolbar)
        (activity as AppCompatActivity).title = (viewModel.stopName)
        setHasOptionsMenu(true)
        (binding.departureToolbar as Toolbar).setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        observeViewModel(viewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view
        val recyclerView = binding.departureList
        adapter = DeparturesListAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        //Create swipe refresh layout
        swipeRefreshLayout = binding.departureSwipeRefresh
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorAccent
            ), ContextCompat.getColor(context!!, R.color.colorPrimary)
        )
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.setEnabled(true)

        viewModel.updateDepartures()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_departure, menu)
        favoriteItem = menu.findItem(R.id.action_favorite)
        viewModel.checkIfFavorite()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.favoriteClicked()
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel(viewModel: DeparturesViewModel) {
        viewModel.refreshing.observe(viewLifecycleOwner, Observer {refreshing ->
            swipeRefreshLayout?.isRefreshing = refreshing
        })

        viewModel.departures.observe(viewLifecycleOwner, Observer {departures ->
            if (departures != null) {
                adapter.setDepartures(departures)
                if (departures.size == 0) {
                    binding.departureList.visibility = View.GONE
                    binding.departureListEmptyText.visibility = View.VISIBLE
                } else {
                    binding.departureList.visibility = View.VISIBLE
                    binding.departureListEmptyText.visibility = View.GONE
                }
            }
        })

        viewModel.isFavorite.observe(viewLifecycleOwner, Observer { isFavorite ->
            if (isFavorite) {
                favoriteItem?.setIcon(R.drawable.ic_favorite_white)
            } else {
                favoriteItem?.setIcon(R.drawable.ic_favorite_border_white)
            }
        })
    }

    override fun onRefresh() {
        viewModel.updateDepartures()
    }

}