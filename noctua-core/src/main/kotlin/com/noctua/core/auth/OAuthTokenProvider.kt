package com.noctua.core.auth

import com.noctua.core.api.TokenApi
import com.noctua.core.model.OuraToken
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [TokenProvider] that holds an [OuraToken] and refreshes it automatically
 * shortly before expiry, using Oura's `refresh_token` grant.
 *
 * Note: Oura refresh tokens are single-use — the newly issued token pair
 * replaces the old one, and [onTokenRefreshed] is invoked so the app can
 * persist it (e.g. into EncryptedSharedPreferences / DataStore).
 */
class OAuthTokenProvider(
    private val tokenApi: TokenApi,
    private val clientId: String,
    private val clientSecret: String?,
    initialToken: OuraToken,
    private val nowEpochMs: () -> Long = System::currentTimeMillis,
    private val onTokenRefreshed: suspend (OuraToken) -> Unit = {},
) : TokenProvider {

    private val mutex = Mutex()
    private var current: OuraToken = initialToken

    override suspend fun token(): String {
        if (!current.isExpired(nowEpochMs())) return current.accessToken
        return mutex.withLock {
            if (!current.isExpired(nowEpochMs())) return current.accessToken
            val refreshed = tokenApi.refreshToken(
                refreshToken = current.refreshToken
                    ?: return current.accessToken,
                clientId = clientId,
                clientSecret = clientSecret,
            ).copy(obtainedAtEpochMs = nowEpochMs())
            current = refreshed
            onTokenRefreshed(refreshed)
            refreshed.accessToken
        }
    }
}
