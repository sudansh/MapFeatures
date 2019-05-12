package com.sudansh.maps.managers

import android.content.Context
import com.google.gson.Gson
import com.sudansh.maps.App
import com.sudansh.maps.util.SP_FILE

object Prefs {

	private val sharedPreferences by lazy {
		App.instance.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
	}

	fun getString(key: String, defaultVal: String = ""): String {
		return sharedPreferences.getString(key, defaultVal).orEmpty()
	}

	fun write(key: String, value: Any?) {
		val editor = sharedPreferences.edit()
		when (value) {
			is Int -> editor.putInt(key, value)
			is Boolean -> editor.putBoolean(key, value)
			is String -> editor.putString(key, value)
			is Long -> editor.putLong(key, value)
			is Any -> editor.putString(key, Gson().toJson(value))
		}
		editor.apply()
	}

}