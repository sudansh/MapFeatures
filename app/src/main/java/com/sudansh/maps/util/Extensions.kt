package com.sudansh.maps.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (T) -> Unit) {
	this.observe(owner, Observer { it ->
		it?.let {
			observer(it)
		}
	})
}

inline fun View.snack(
	message: String,
	length: Int = Snackbar.LENGTH_LONG,
	action: Snackbar.() -> Unit = {}
) {
	val snack = Snackbar.make(this, message, length)
	snack.action()
	snack.show()
}

