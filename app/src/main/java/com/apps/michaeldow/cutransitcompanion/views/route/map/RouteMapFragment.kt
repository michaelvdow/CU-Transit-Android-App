package com.apps.michaeldow.cutransitcompanion.views.route.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
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
    private var handler: Handler? = null
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

        setupStatusBarClick()

        return binding.root
    }

    private fun setupStatusBarClick() {
        binding.routeCardView.setOnClickListener { view ->
            var nextStopId = viewModel.bus.value?.next_stop_id
            val stopTimes = viewModel.stopTimes.value
            if (nextStopId != null && stopTimes != null) {
                nextStopId = Utils.fixStopId(nextStopId)
                // Find stop time
                for (stopTime in stopTimes) {
                    if (nextStopId == Utils.fixStopId(stopTime.stop_point.stop_id)) {
                        moveCameraTo(LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon), true)
                        // Show info window for that marker
                        for (marker in markers) {
                            if (marker.tag != null && Utils.fixStopId(marker.tag as String) == nextStopId) {
                                marker.showInfoWindow()
                            }
                        }
                        break
                    }
                }
            }
        }

        binding.routeBusLocationButton.setOnClickListener {view ->
            val bus = viewModel.bus.value
            if (bus != null) {
                val location = bus.location
                moveCameraTo(LatLng(location.lat, location.lon), true)
            }
        }
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
        running = false
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        running = false
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

        locationPermissionGranted()
        map?.setOnInfoWindowClickListener { marker ->
            if (marker.tag != null) {
                if (findNavController().currentDestination?.id== R.id.routeFragment) {
                    viewModel.lastMarker = marker
                    running = false
                    if (runnable != null) {
                        handler?.removeCallbacks(runnable)
                    }
                    val action = RouteFragmentDirections.actionRouteFragmentToDeparturesFragment(marker.tag as String)
                    findNavController().navigate(action)
                }
            }
        }

        if (viewModel.shapes.value == null) {
            viewModel.getShape()
        } else {
            setupShapes(viewModel.shapes.value!!)
        }
        if (viewModel.stopTimes.value == null) {
            viewModel.getStops()
        } else {
            setupStops(viewModel.stopTimes.value!!)
        }
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
                val icon = bitmapDescriptorFromVector(context as Context, R.drawable.ic_bus, 25)
                val options = MarkerOptions()
                    .position(LatLng(bus.location.lat, bus.location.lon))
                    .icon(icon)
                    .title(Utils.fixLastUpdatedTime(bus.last_updated))
                    .visible(true)
                    .zIndex(3.0f)

                busMarker = map?.addMarker(options)

                updateStatusBar()
            }
        })
    }

    private fun setupStops(stopTimes: List<StopTime>) {
        val map = map
        if (map != null) {
            for (stopTime in stopTimes) {
                val location = LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon)
                var icon = bitmapDescriptorFromVector(context as Context, R.drawable.ic_stop_marker, 20)
                if (Utils.fixStopId(stopTime.stop_point.stop_id) == Utils.fixStopId(viewModel.departure.stop_id)) {
                    icon = bitmapDescriptorFromVector(context as Context, R.drawable.ic_stop_marker_red, 20)
                }
                val options = MarkerOptions()
                    .position(location)
                    .title(stopTime.stop_point.stop_name)
                    .icon(icon)

                val marker = map.addMarker(options)
                marker.tag = stopTime.stop_point.stop_id
                if (viewModel.lastMarker == null && stopTime.stop_point.stop_id == viewModel.departure.stop_id) {
                    moveCameraTo(LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon))
                    viewModel.lastMarker = marker
                    marker.showInfoWindow()
                } else if (viewModel.lastMarker != null && viewModel.lastMarker?.tag == marker.tag) {
                    moveCameraTo(LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon))
                    moveCameraTo(LatLng(stopTime.stop_point.stop_lat, stopTime.stop_point.stop_lon))
                    marker.showInfoWindow()
                    viewModel.lastMarker = marker
                }
                markers.add(marker)
            }

            updateStatusBar()
        }
    }

    private fun updateStatusBar() {
        val bus = viewModel.bus.value
        val stopTimes = viewModel.stopTimes.value
        val busNextStop = bus?.next_stop_id
        if (stopTimes != null && bus != null && busNextStop != null) {
            for (stopTime in stopTimes) {
                val nextStopId = Utils.fixStopId(busNextStop)
                if (nextStopId == Utils.fixStopId(stopTime.stop_point.stop_id)) {
                    binding.routeNextStop.text = getString(R.string.next_stop) + " " + stopTime.stop_point.stop_name
                    break
                }
            }
        }
        if (bus != null) {
            binding.routeUpdated.text = Utils.fixLastUpdatedTime(bus.last_updated)
        }
    }

    private fun moveCameraTo(position: LatLng, animate: Boolean = false) {
        println(position)
        if (animate) {
            map?.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                .target(position)
                .zoom(16f)
                .build()))
        } else {
            map?.moveCamera(CameraUpdateFactory.newLatLng(position))
            map?.moveCamera(CameraUpdateFactory.zoomTo(16f))
        }
    }

    private fun setupBusHandler() {
        val checkDuration: Long = 60000
        handler = Handler()
        runnable = Runnable {
            if (running) {
                viewModel.updateBusLocation()

                handler?.postDelayed(runnable, checkDuration)
            }
        }
        handler?.postDelayed(runnable, 0)
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
        var size = dpToPx(context, size.toFloat()).toInt()
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

    private fun dpToPx(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
    }

}