package com.ltthuc.network.api

/**
 * Pluggable token refresh contract used by [com.ltthuc.network.impl.TokenAuthenticator].
 *
 * Default binding ([com.ltthuc.network.impl.NoOpTokenRefresher]) returns null so 401 responses fail
 * normally. Consumers needing real refresh logic provide their own binding.
 */
interface TokenRefresher {
    suspend fun refresh(): String?
}
