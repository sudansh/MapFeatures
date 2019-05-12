package com.sudansh.maps.network

import com.sudansh.maps.data.Base
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface ApiService {

	@GET("v4/geo/features")
	fun getFeaturesAsync(): Deferred<Base>

}