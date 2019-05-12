package com.sudansh.maps

import android.app.Application
import com.sudansh.maps.di.appModule
import com.sudansh.maps.di.localModule
import com.sudansh.maps.di.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		instance = this
		startKoin {
			// declare used Android context
			androidContext(this@App)
			// declare modules
			modules(listOf(remoteModule, localModule, appModule))
		}
	}

	companion object {
		lateinit var instance: App
	}
}