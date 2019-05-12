package com.sudansh.maps.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.sudansh.maps.R
import com.sudansh.maps.data.Features
import com.sudansh.maps.data.Resource
import com.sudansh.maps.data.Status.ERROR
import com.sudansh.maps.data.Status.LOADING
import com.sudansh.maps.data.Status.SUCCESS
import com.sudansh.maps.util.observeNonNull
import com.sudansh.maps.util.snack
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MapsActivity : AppCompatActivity(R.layout.activity_maps),
					 GoogleApiClient.ConnectionCallbacks, LocationListener,
					 GoogleMap.OnMarkerClickListener {

	private lateinit var infoWindowAdapter: GoogleMap.InfoWindowAdapter
	private var mGoogleApiClient: GoogleApiClient? = null
	private lateinit var googleMap: GoogleMap
	private val vm: MapsViewModel by viewModel()
	private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(bottomsheet) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		getSystemService(Context.LOCATION_SERVICE)

		mapView.apply {
			onCreate(savedInstanceState)
			onResume()
			getMapAsync { mMap ->
				onMapReady(mMap)
			}
			createInfoWindowAdapter()
		}
		toolbar.apply {
			setNavigationOnClickListener { finish() }
		}
		setupBottomsheet()
		navigate.setOnClickListener {
			openMaps()
		}
		vm.activeMarker.observeNonNull(this) {
			openDetailSheet(it)
		}
		if (!Places.isInitialized()) {
			Places.initialize(applicationContext, getString(R.string.google_maps_key))
		}
		val autoCompleteFragment =
			supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
		autoCompleteFragment.apply {
			setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))
		}

	}

	private fun setupBottomsheet() {
		bottomSheetBehavior.apply {
			state = STATE_COLLAPSED
			setBottomSheetCallback(object :
									   BottomSheetBehavior.BottomSheetCallback() {
				override fun onSlide(bottomSheet: View, slideOffset: Float) {}

				override fun onStateChanged(bottomSheet: View, newState: Int) {
					when (newState) {
						STATE_EXPANDED -> {
							navigate.visibility = View.VISIBLE
							badge.visibility = View.VISIBLE
						}
						STATE_COLLAPSED -> {
							navigate.visibility = View.GONE
							badge.visibility = View.GONE
						}
						else -> {
						}
					}
				}
			})
		}
	}

	/**
	 * open Maps application
	 */
	private fun openMaps() {
		vm.activeMarker.value?.run {
			val uri = Uri.parse("geo:${position.latitude},${position.longitude}")
			val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
				setPackage("com.google.android.apps.maps")
			}
			startActivity(mapIntent)
		}
	}

	private fun createInfoWindowAdapter() {
		infoWindowAdapter = object : GoogleMap.InfoWindowAdapter {
			override fun getInfoWindow(marker: Marker): View? {
				val popup = LayoutInflater.from(this@MapsActivity)
					.inflate(R.layout.info_window_layout, null)

				(popup.findViewById(R.id.title) as TextView).text = marker.title

				return popup
			}

			override fun getInfoContents(marker: Marker): View? {
				val popup = LayoutInflater.from(this@MapsActivity)
					.inflate(R.layout.info_window_layout, null)

				(popup.findViewById(R.id.title) as TextView).text = marker.title
				return popup
			}
		}
	}

	private fun onMapReady(map: GoogleMap) {
		val metrics = DisplayMetrics()
		windowManager.defaultDisplay.getMetrics(metrics)
		map.apply {
			googleMap = this
			googleMap.setOnMarkerClickListener(this@MapsActivity)
			googleMap.setInfoWindowAdapter(infoWindowAdapter)
		}
		vm.features.observeNonNull(this) {
			updateFeatures(it)
		}
		//Check location permission
		val permissionCheck =
			ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
				this,
				arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				REQUEST_LOCATION
			)
		} else {
			this.googleMap.isMyLocationEnabled = true
			if (mGoogleApiClient == null) {
				buildGoogleApiClient()
			}
		}
	}

	/**
	 * Update the recyclerview with the features list
	 */
	private fun updateFeatures(resource: Resource<List<Features>>) {
		when (resource.status) {
			SUCCESS -> {
				buildMarkers(resource.data.orEmpty())
				vm.isLoading.value = false
			}
			LOADING -> vm.isLoading.value = true
			ERROR -> {
				vm.isLoading.value = false
				mainContainer.snack(resource.message.orEmpty())
			}
		}
	}

	/**
	 * Create a latlng boundary to center the map based on all the pins
	 */
	private fun buildMarkers(list: List<Features>) {
		val bounds = LatLngBounds.Builder()
		list.forEach {
			bounds.include(it.position)
			placeMarkerAndSetTag(it)
		}
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 160))
	}

	/**
	 * Create custom markers
	 */
	private fun placeMarkerAndSetTag(features: Features) {
		val b = (ContextCompat.getDrawable(
			this@MapsActivity,
			R.drawable.ic_marker
		) as BitmapDrawable).bitmap
		val scaled = Bitmap.createScaledBitmap(b, 100, 100, false)
		val marker = googleMap.addMarker(
			MarkerOptions()
				.position(features.position)
				.title(features.title)
				.snippet(features.snippet)
				.icon(BitmapDescriptorFactory.fromBitmap(scaled))
		)
		marker.tag = features
	}

	override fun onMarkerClick(marker: Marker): Boolean {
		vm.setActiveMarker(marker.tag as Features)
		marker.showInfoWindow()
		return true
	}

	/**
	 * open detail sheet to show additional information
	 */
	private fun openDetailSheet(features: Features) {
		bottomSheetBehavior.state = STATE_EXPANDED
		findViewById<TextView>(R.id.address)?.text = features.address(this@MapsActivity)
		findViewById<TextView>(R.id.phone)?.text = "+123456789" //showing dummy number
	}

	@SuppressLint("MissingPermission")
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			if (mGoogleApiClient == null) {
				buildGoogleApiClient()
			}
			googleMap.isMyLocationEnabled = true
		}
	}

	@Synchronized
	private fun buildGoogleApiClient() {
		mGoogleApiClient = GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addApi(LocationServices.API)
			.build()
		mGoogleApiClient?.connect()
	}

	override fun onLocationChanged(location: Location?) {
		location?.let {
			val latLng = LatLng(location.latitude, location.longitude)
			//stop location updates
			if (mGoogleApiClient != null) {
				LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
			}
		}
	}

	override fun onConnected(bundle: Bundle?) {
		//Fetch location updates
		val mLocationRequest = LocationRequest().apply {
			interval = 3000
			fastestInterval = 3000
			priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
		}
		if (ActivityCompat.checkSelfPermission(
				this@MapsActivity,
				Manifest.permission.ACCESS_FINE_LOCATION
			) == PackageManager.PERMISSION_GRANTED
		)
			LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient,
				mLocationRequest,
				this
			)
	}

	override fun onConnectionSuspended(p0: Int) {}

	companion object {
		const val REQUEST_LOCATION = 1000
	}
}
