package com.sudansh.maps.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sudansh.maps.network.ApiService
import com.sudansh.maps.network.HeaderInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {
	single { createWebService<ApiService>(get()) }
	single { okHttpClient(get()) }
	single { headerInterceptor() }
}

fun okHttpClient(
	headerInterceptor: HeaderInterceptor
): OkHttpClient {
	val httpClient = OkHttpClient.Builder()
		.connectTimeout(60L, TimeUnit.SECONDS)
		.readTimeout(60L, TimeUnit.SECONDS)
		.addInterceptor(headerInterceptor)
	return httpClient.build()
}

fun headerInterceptor(): HeaderInterceptor {
	return HeaderInterceptor()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient): T {
	return Retrofit.Builder()
		.baseUrl("https://api.proximi.fi/")
		.addConverterFactory(GsonConverterFactory.create())
		.addCallAdapterFactory(CoroutineCallAdapterFactory())
		.client(okHttpClient)
		.build().create(T::class.java)
}