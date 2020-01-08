package com.apps.michaeldow.cutransitcompanion.views.departures

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.Utils
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentDeparturesBinding
import com.apps.michaeldow.cutransitcompanion.notification.NotificationService
import com.google.android.material.snackbar.Snackbar


class DeparturesFragment: Fragment(), OnRefreshListener, DeparturesListAdapter.OnDepartureLongClickListener {

    private lateinit var viewModel: DeparturesViewModel
    private lateinit var binding: FragmentDeparturesBinding
    private lateinit var adapter: DeparturesListAdapter
    private var favoriteItem: MenuItem? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_departures, container, false)
        binding.lifecycleOwner = this

        // Setup view model
        viewModel = ViewModelProviders.of(this).get(DeparturesViewModel::class.java)
        arguments?.let {
            val safeArgs = DeparturesFragmentArgs.fromBundle(it)
            viewModel.stopId = Utils.fixStopId(safeArgs.stopId)
        }

        observeViewModel(viewModel)

        // Hide keyboard
        if (activity != null) {
            val inputManager: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // check if no view has focus:
            val currentFocusedView = activity!!.currentFocus
            if (currentFocusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    currentFocusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        // Setup toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.departureToolbar)
        viewModel.getStopName()
        setHasOptionsMenu(true)
        (binding.departureToolbar as Toolbar).setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view
        val recyclerView = binding.departureList
        adapter = DeparturesListAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        adapter.setOnLongClickListener(this)

        if (context != null) {
            val itemDecoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_divider)
            itemDecoration.setDrawable(drawable!!)
            recyclerView.addItemDecoration(itemDecoration)
        }

        //Create swipe refresh layout
        var swipeRefreshLayout = binding.departureSwipeRefresh
        swipeRefreshLayout?.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorAccent
            ), ContextCompat.getColor(context!!, R.color.colorPrimary)
        )
        swipeRefreshLayout?.setOnRefreshListener(this)
        swipeRefreshLayout?.setEnabled(true)

        swipeRefreshLayout = binding.departureSwipeRefreshEmptyText
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
            binding.departureSwipeRefreshEmptyText?.isRefreshing = refreshing
            binding.departureSwipeRefresh?.isRefreshing = refreshing
        })

        viewModel.departures.observe(viewLifecycleOwner, Observer {departures ->
            if (departures != null) {
                adapter.setDepartures(departures)
                if (departures.size == 0) {
                    binding.departureSwipeRefresh.visibility = View.GONE
                    binding.departureSwipeRefreshEmptyText.visibility = View.VISIBLE
                    val snackbar = Snackbar.make(binding.departureFrameLayout, getString(R.string.no_data_found), Snackbar.LENGTH_SHORT)
                    snackbar.show()
                } else {
                    binding.departureSwipeRefresh.visibility = View.VISIBLE
                    binding.departureSwipeRefreshEmptyText.visibility = View.GONE
                    val snackbar = Snackbar.make(binding.departureFrameLayout, getString(R.string.updated_departures), Snackbar.LENGTH_SHORT)
                    snackbar.show()
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

        viewModel.stopName.observe(viewLifecycleOwner, Observer { stopName ->
            if (stopName != null) {
                (activity as AppCompatActivity).title = (stopName)
            }
        })
    }

    override fun onRefresh() {
        viewModel.updateDepartures()
    }

    override fun onLongClick(departure: Departure) {
        showDialog(departure)
    }

    private fun showDialog(departure: Departure) {
        if (context != null) {
            val dialog = Dialog(context as Context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_departure)

            val set = dialog.findViewById<Button>(R.id.dialog_set_button)
            val cancel = dialog.findViewById<Button>(R.id.dialog_cancel_button)

            val numberPicker = dialog.findViewById<NumberPicker>(R.id.dialog_number_picker)
            val timeLeft = departure.expected_mins
            if (timeLeft > 1) {
                numberPicker.maxValue = timeLeft - 1
            } else {
                numberPicker.maxValue = 1
            }
            numberPicker.minValue = 1
            numberPicker.wrapSelectorWheel = false

            set.setOnClickListener{view ->
                val alarmTime = numberPicker.value
                dialog.dismiss()
                NotificationService.startService(context as Context, departure, alarmTime)
            }

            cancel.setOnClickListener{view ->
                dialog.dismiss()
            }

            dialog.show()
        }
    }


}