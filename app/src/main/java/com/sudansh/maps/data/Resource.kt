package com.sudansh.maps.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
	companion object {
		fun <T> success(data: T?): Resource<T> {
			return Resource(Status.SUCCESS, data, null)
		}

		fun <T> error(msg: String, data: T?): Resource<T> {
			return Resource(Status.ERROR, data, msg)
		}

	}
}

/**
 *
 * Status of a resource that is provided to the UI.
 *  These are usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
 */
enum class Status {
	SUCCESS,
	ERROR,
	LOADING
}
