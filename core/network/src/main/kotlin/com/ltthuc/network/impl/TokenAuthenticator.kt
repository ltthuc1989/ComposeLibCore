package com.ltthuc.network.impl

import com.ltthuc.network.api.TokenRefresher
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class TokenAuthenticator @Inject constructor(
    private val tokenRefresher: TokenRefresher,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null
        val newToken = runBlocking { tokenRefresher.refresh() } ?: return null
        return response.request.newBuilder()
            .header(ApiService.HEADER_AUTHORIZATION, "Bearer $newToken")
            .build()
    }
}

internal class NoOpTokenRefresher @Inject constructor() : TokenRefresher {
    override suspend fun refresh(): String? = null
}
