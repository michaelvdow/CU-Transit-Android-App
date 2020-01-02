package com.apps.michaedow.cutransit.main_activity.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.apps.michaedow.cutransit.Utils.BetterLocationProvider
import com.apps.michaedow.cutransit.databinding.FragmentBusMapBinding
import com.apps.michaedow.cutransit.main_activity.TabFragmentDirections
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class BusMapFragment: Fragment(), OnMapReadyCallback {


    private lateinit var viewModel: MapViewModel
    private lateinit var binding: FragmentBusMapBinding
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var locationProvider: BetterLocationProvider

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private val markers: ArrayList<MarkerOptions> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, com.apps.michaedow.cutransit.R.layout.fragment_bus_map, container, false)
        binding.lifecycleOwner = this

        mapView = binding.mapView
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

        locationProvider = BetterLocationProvider.instance

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        observeViewModel(viewModel)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Setup settings
        map = googleMap
        map.setMinZoomPreference(12f)
        locationPermissionGranted()

        map.setOnInfoWindowClickListener { marker ->
            viewModel.currentLocation = markers[marker.tag as Int].position
            val action = TabFragmentDirections.actionTabFragmentToDeparturesFragment(marker.title)
            findNavController().navigate(action)
        }

        setupMarkers()
    }

    // If markers is empty, pull from database again
    private fun setupMarkers() {
        if (markers.isEmpty()) {
            val stops = viewModel.stops.value
            if (context != null && stops != null) {
                val icon = bitmapDescriptorFromVector(context as Context, com.apps.michaedow.cutransit.R.drawable.ic_bus_marker)
                for (stop in stops) {
                    val markerOptions = MarkerOptions()
                        .position(LatLng(stop.stopLat.toDouble(), stop.stopLon.toDouble()))
                        .title(stop.stopName)
                        .icon(icon)
                    map.addMarker(markerOptions).tag = markers.size
                    markers.add(markerOptions)
                }
            }
        } else {
            map.clear()
            for (i in 0 until markers.size) {
                map.addMarker(markers[i]).tag = i
            }
        }
    }

    private fun observeViewModel(viewModel: MapViewModel) {
        viewModel.stops.observe(viewLifecycleOwner, Observer {
            if (::map.isInitialized) {
                setupMarkers()
            }
        })
    }

    // https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, com.apps.michaedow.cutransit.R.drawable.ic_bus_marker)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun locationPermissionGranted() {
        if (context != null && (ContextCompat.checkSelfPermission(context as Context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context as Context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) && ::map.isInitialized) {
            map.isMyLocationEnabled = true
            val uiSettings: UiSettings = map.uiSettings
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isMyLocationButtonEnabled = true

            // Move to current location
            val currentLocation = viewModel.currentLocation
            if (currentLocation == null) {
                locationProvider.updateLocation()?.addOnSuccessListener { newLocation: Location? ->
                    if (newLocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(newLocation.latitude, newLocation.longitude)))
                        map.moveCamera(CameraUpdateFactory.zoomTo(17f))
                    }
                }
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                map.moveCamera(CameraUpdateFactory.zoomTo(17f))
            }
        }
    }

}