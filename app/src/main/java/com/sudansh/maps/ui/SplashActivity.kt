package com.sudansh.maps.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudansh.maps.managers.ProfileManager
import com.sudansh.maps.ui.login.LoginActivity
import com.sudansh.maps.ui.map.MapsActivity

class SplashActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (ProfileManager.isSigned()) {
			//Registered user
			startActivity(Intent(this@SplashActivity, MapsActivity::class.java))
		} else {
			//Unsigned user
			startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
		}
		finish()
	}
}