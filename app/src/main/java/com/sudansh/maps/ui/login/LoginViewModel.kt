package com.sudansh.maps.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudansh.maps.managers.ProfileManager
import com.sudansh.maps.network.TOKEN

class LoginViewModel : ViewModel() {

	val response = MutableLiveData<String>()
	val showLoading = MutableLiveData<Boolean>()

	fun login() {
		showLoading.value = true
		//TODO login logic : out of scope of assignment

		//use the given session token
		ProfileManager.sessionToken = TOKEN
		response.value = ""
	}
}
