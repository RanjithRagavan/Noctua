package com.noctua.core.auth

/**
 * Supplies the bearer token attached to every API request.
 * Implementations may refresh transparently (see [OAuthTokenProvider]).
 */
fun interface TokenProvider {
    suspend fun token(): String

    companion object {
        /** Simple provider for a static token (PAT or an already-issued access token). */
        fun of(staticToken: String): TokenProvider = TokenProvider { staticToken }
    }
}
