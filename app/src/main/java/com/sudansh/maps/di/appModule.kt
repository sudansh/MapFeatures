package com.sudansh.maps.di

import com.sudansh.maps.ui.login.LoginViewModel
import com.sudansh.maps.repository.FeatureRepository
import com.sudansh.maps.ui.map.MapsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val localModule = module {
	single { FeatureRepository(get()) }
}

val appModule = module {
	viewModel { MapsViewModel(get()) }
	viewModel { LoginViewModel() }
}