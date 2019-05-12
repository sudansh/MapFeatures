package com.sudansh.maps.network

import com.sudansh.maps.managers.ProfileManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		var request = chain.request()
		val builder = request.newBuilder()
		builder.addHeader("Authorization", "Bearer ${ProfileManager.sessionToken}")

		request = builder.build()
		return chain.proceed(request)
	}
}

const val TOKEN =
	"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImlzcyI6IjhkYWE4NTc1LTRiZDAtNGE1Yi04Y2E2LTk1M2RkOTg1MWE0MCIsInR5cGUiOiJhcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uX2lkIjoiMTcyNzcwYjUtZTI3Ni00MjRmLThjMjQtNzBiNDBiODE5OTBiIn0.Kqf7EySMlStm92n6TT6PmLAmpKcd_nFyMHb4evl8MU8"