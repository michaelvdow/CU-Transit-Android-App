package com.apps.michaeldow.cutransitcompanion.views.route.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
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
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.API.responses.shapeResponse.Shape
import com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse.StopTime
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.BetterLocationProvider
import com.apps.michaeldow.cutransitcompanion.Utils.Utils
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentRouteMapBinding
import com.apps.michaeldow.cutransitcompanion.views.route.RouteFragmentDirections
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class RouteMapFragment: Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: RouteMapViewModel
    private lateinit var binding: FragmentRouteMapBinding
    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private lateinit var locationProvider: BetterLocationProvider
    private val markers: ArrayList<Marker> = ArrayList()
    private var runnable: Runnable? = null
    private var running = true
    private var busMarker: Marker? = null

    private val MAP_VIEW_BUNDLE_KEY = "RouteMapViewBundleKey"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_route_map, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(RouteMapViewModel::class.java)
        observeViewModel(viewModel)
        viewModel.departure = arguments?.getSerializable("departure") as Departure

        mapView = binding.routeMapView
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mapView?.onCreate(mapViewBundle)
        mapView?.getMapAsync(this)

        locationProvider = BetterLocationProvider.instance

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        running = false
        mapView?.onStop()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Setup settings
        map = googleMap
        try {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context as Context, R.raw.style_dark))
            } else {
                map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context as Context, R.raw.style_light))
            }
        } catch (e: Exception) {

        }
        map?.setMinZoomPreference(12f)

        locationPermissionGranted()
        map?.setOnInfoWindowClickListener { marker ->
            if (marker.tag != null) {
                val action = RouteFragmentDirections.actionRouteFragmentToDeparturesFragment(marker.tag as String)
                findNavController().navigate(action)
            }
        }

        viewModel.getShape()
        viewModel.getStops()
        running = true
        setupBusHandler()
    }

    private fun observeViewModel(viewModel: RouteMapViewModel) {
        viewModel.shapes.observe(viewLifecycleOwner, Observer { shapes ->
            if (shapes != null) {
                setupShapes(shapes)
            }
        })

        viewModel.stopTimes.observe(viewLifecycleOwner, Observer { stopTimes ->
            if (stopTimes != null) {
                setupStops(stopTimes)
            }
        })

        viewModel.bus.observe(viewLifecycleOwner, Observer { bus ->
            if (bus != null) {
                if (busMarker != null) {
                    busMarker?.isVisible = false
                }
                val icon = bitmapDescriptorFromVector(context as Context, R.drawable.ic_bus, 60)
                val options = MarkerOptions()
                    .position(LatLng(bus.location.lat, bus.location.lon))
                    .icon(icon)
                    .title(Utils.fixLastUpdatedTime(bus.last_updated))
                    .visible(true)
                    .zIndex(3.0f)

                busMarker = map?.addMarker(options)
            }
        })
    }

    private fun setupStops(stopTimes: List<StopTime>) {
        val map = map
        if (map != null) {
            for (stopTime in stopTimes) {
                val location = LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon)
                val icon = bitmapDescriptorFromVector(context as Context, R.drawable.ic_stop_marker, 50)
                val options = MarkerOptions()
                    .position(location)
                    .title(stopTime.stop_point.stop_name)
                    .icon(icon)

                val marker = map.addMarker(options)
                marker.tag = stopTime.stop_point.stop_id
                if (stopTime.stop_point.stop_id == viewModel.departure.stop_id) {
                    moveCameraTo(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon)
                    marker.showInfoWindow()
                }
                markers.add(marker)
            }
        }
    }

    private fun moveCameraTo(lat: Double, lon: Double) {
        map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))
        map?.moveCamera(CameraUpdateFactory.zoomTo(17f))
    }

    private fun setupBusHandler() {
        val checkDuration: Long = 60000
        val handler = Handler()
        runnable = Runnable {
            if (running) {
                viewModel.updateBusLocation()
                println("UPDATING LOCATION")

                handler.postDelayed(runnable, checkDuration)
            }
        }
        handler.postDelayed(runnable, 0)
    }


    private fun setupShapes(shapes: List<Shape>) {
        val polylineOptions = PolylineOptions()
        for (shape in shapes) {
            polylineOptions.add(LatLng(shape.shape_pt_lat, shape.shape_pt_lon))
        }
        try {
            map?.addPolyline(
                polylineOptions.color(ContextCompat.getColor(context!!, R.color.colorPrimary))
                    .width(15f)
                    .endCap(RoundCap())

            )
        } catch (e: Exception) {

        }
    }


    private fun locationPermissionGranted() {
        val map = map
        if (context != null && (ContextCompat.checkSelfPermission(context as Context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context as Context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) && map != null) {
            map.isMyLocationEnabled = true
            val uiSettings: UiSettings = map.uiSettings
            uiSettings.isMapToolbarEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
        }
    }


    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int, size: Int): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        background!!.setBounds(0, 0, size, size)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable!!.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}