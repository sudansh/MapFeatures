package com.sudansh.maps.repository

import com.sudansh.maps.data.Base
import com.sudansh.maps.network.ApiService

class FeatureRepository(private val api: ApiService) {

	suspend fun fetchFeatures(): Base {
		return api.getFeaturesAsync().await()
	}
}