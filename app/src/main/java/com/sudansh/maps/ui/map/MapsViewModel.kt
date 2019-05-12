package com.sudansh.maps.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudansh.maps.data.Resource
import com.sudansh.maps.repository.FeatureRepository
import com.sudansh.maps.data.Features
import kotlinx.coroutines.launch

class MapsViewModel(private val repo: FeatureRepository) : ViewModel() {

	fun setActiveMarker(features: Features) {
		activeMarker.value = features
	}

	val isLoading: MutableLiveData<Boolean> = MutableLiveData()
	var features = MutableLiveData<Resource<List<Features>>>()
	val activeMarker = MutableLiveData<Features>()

	init {
		fetchItems()
	}

	private fun fetchItems() {
		viewModelScope.launch {
			try {
				val response = repo.fetchFeatures()
				if (response.features?.isNullOrEmpty() == true) {
					features.value = Resource.error("Empty data", null)
				} else {
					features.value = Resource.success(response.features)
				}
			} catch (e: Exception) {
				features.value = Resource.error(e.localizedMessage, null)
			}
		}
	}

}