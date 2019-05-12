package com.sudansh.maps.data

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.*

data class Features(
	var id: String = "",
	val geometry: Geometry?,
	val properties: Properties?
) : ClusterItem {
	override fun getSnippet() = ""

	override fun getTitle() = properties?.title.orEmpty()

	override fun getPosition() = LatLng(
		geometry?.coordinates?.get(1) ?: 0.0,
		geometry?.coordinates?.get(0) ?: 0.0
	)

	fun address(context: Context): String {
		val geocoder = Geocoder(context, Locale.getDefault())

		val addresses = geocoder.getFromLocation(
			geometry?.coordinates?.get(1) ?: 0.0,
			geometry?.coordinates?.get(0) ?: 0.0,
			1
		)

		return addresses[0].getAddressLine(0).orEmpty()
	}
}

data class Base(val features: List<Features>?)

data class Geometry(val coordinates: List<Double>?)

data class Properties(
	val title: String?,
	val usecase: String?,
	val minzoom: Int?,
	val visibility: String?,
	val amenity: String?
)