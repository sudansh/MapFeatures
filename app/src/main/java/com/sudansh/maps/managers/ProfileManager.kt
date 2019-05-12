package com.sudansh.maps.managers

import com.sudansh.maps.util.PC_USER_SESSION_TOKEN

object ProfileManager {

	var sessionToken = ""
		get() = Prefs.getString(PC_USER_SESSION_TOKEN)
		set(value) {
			field = value
			Prefs.write(PC_USER_SESSION_TOKEN, value)
		}

	fun isSigned(): Boolean {
		return sessionToken.isNotEmpty()
	}
}