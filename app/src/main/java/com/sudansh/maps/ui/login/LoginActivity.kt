package com.sudansh.maps.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sudansh.maps.R
import com.sudansh.maps.ui.map.MapsActivity
import com.sudansh.maps.util.observeNonNull
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

	private val loginVM: LoginViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		loginVM.response.observeNonNull(this) {
			if (it.isEmpty()) {
				startActivity(Intent(this@LoginActivity, MapsActivity::class.java))
			} else {
				Toast.makeText(this@LoginActivity, it, Toast.LENGTH_LONG).show()
			}
		}
		loginVM.showLoading.observeNonNull(this) {
			progressBar.visibility = if (it) View.VISIBLE else View.GONE
		}
	}

	fun onLoginClick(v: View) {
		loginVM.login()
	}
}