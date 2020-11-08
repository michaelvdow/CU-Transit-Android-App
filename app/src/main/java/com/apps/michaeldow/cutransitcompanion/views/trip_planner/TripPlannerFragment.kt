package com.apps.michaeldow.cutransitcompanion.views.trip_planner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.apps.michaeldow.cutransitcompanion.BuildConfig
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentTripPlannerBinding
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions


class TripPlannerFragment: Fragment() {

    private lateinit var viewModel: TripPlannerViewModel
    private lateinit var binding: FragmentTripPlannerBinding

    private val START_RESULT = 1234
    private val END_RESULT = 4321

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_planner, container, false)
        binding.lifecycleOwner = this

        // Setup view model
        viewModel = ViewModelProviders.of(this).get(TripPlannerViewModel::class.java)

        setupAutocomplete()

        observeViewModel(viewModel)

        return binding.root
    }

    private fun setupAutocomplete() {
        binding.startText.setOnClickListener {
            val placeOptions = PlaceOptions.builder()
            .hint("Start: ")
            .statusbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .build(PlaceOptions.MODE_CARDS)
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(BuildConfig.MAPBOX_API_KEY)
                .placeOptions(placeOptions)
                .build(activity)
            startActivityForResult(intent, START_RESULT)
        }

        binding.endText.setOnClickListener {
            val placeOptions = PlaceOptions.builder()
                .hint("End: ")
                .statusbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .build(PlaceOptions.MODE_CARDS)
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(BuildConfig.MAPBOX_API_KEY)
                .placeOptions(placeOptions)
                .build(activity)
            startActivityForResult(intent, END_RESULT)
        }

    }

    private fun observeViewModel(viewModel: TripPlannerViewModel) {
        viewModel.startFeature.observe(viewLifecycleOwner, Observer { feature ->
            if (feature != null) {
                binding.startText.text = "Start: " + feature.text()
            }
        })

        viewModel.endFeature.observe(viewLifecycleOwner, Observer { feature ->
            if (feature != null) {
                binding.endText.text = "End: " + feature.text()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == START_RESULT) {
            println("HERE")
            val feature = PlaceAutocomplete.getPlace(data)
            viewModel.setStart(feature)
        } else if (resultCode == Activity.RESULT_OK && requestCode == END_RESULT) {
            val feature = PlaceAutocomplete.getPlace(data)
            viewModel.setEnd(feature)
        }
    }
}