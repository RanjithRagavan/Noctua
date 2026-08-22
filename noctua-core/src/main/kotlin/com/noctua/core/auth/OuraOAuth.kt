package com.noctua.core.auth

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * OAuth2 helpers for the Oura authorization endpoints.
 *
 * Authorize URL:  `https://cloud.ouraring.com/oauth/authorize`
 * Token endpoint: `https://api.ouraring.com/oauth/token`
 *
 * Two flows are supported by Oura:
 *  - Authorization-code flow (`response_type=code`) — for apps with a backend
 *    that can hold the client secret.
 *  - Client-side flow (`response_type=token`) — token returns in the redirect
 *    fragment; no refresh tokens. Simplest option for a mobile-only app.
 */
object OuraOAuth {

    const val AUTHORIZE_URL = "https://cloud.ouraring.com/oauth/authorize"

    /** All scopes documented by Oura v2. */
    val ALL_SCOPES = listOf(
        "email", "personal", "daily", "heartrate",
        "workout", "tag", "session", "spo2",
    )

    /** Build the URL the user should be sent to (Custom Tab / browser). */
    fun authorizationUrl(
        clientId: String,
        redirectUri: String,
        scopes: List<String> = ALL_SCOPES,
        state: String? = null,
        useClientSideFlow: Boolean = true,
    ): String {
        val responseType = if (useClientSideFlow) "token" else "code"
        val params = buildList {
            add("response_type=$responseType")
            add("client_id=${urlEncode(clientId)}")
            add("redirect_uri=${urlEncode(redirectUri)}")
            add("scope=${urlEncode(scopes.joinToString(" "))}")
            if (state != null) add("state=${urlEncode(state)}")
        }
        return "$AUTHORIZE_URL?${params.joinToString("&")}"
    }

    /** Parse the fragment/query of the client-side redirect. Returns null on error/deny. */
    fun parseClientSideRedirect(url: String): ParsedRedirect {
        val fragment = url.substringAfter('#', "")
        val query = if (fragment.isNotEmpty()) fragment else url.substringAfter('?', "")
        val params = query.split('&')
            .mapNotNull {
                val idx = it.indexOf('=')
                if (idx <= 0) null else it.substring(0, idx) to it.substring(idx + 1)
            }
            .toMap()
        if (params.containsKey("error")) return ParsedRedirect(error = params["error"])
        val token = params["access_token"] ?: params["code"]
        return ParsedRedirect(
            accessToken = if (params.containsKey("access_token")) token else null,
            code = params["code"],
            state = params["state"],
            expiresIn = params["expires_in"]?.toLongOrNull(),
        )
    }

    data class ParsedRedirect(
        val accessToken: String? = null,
        val code: String? = null,
        val state: String? = null,
        val expiresIn: Long? = null,
        val error: String? = null,
    )

    // ---- PKCE helpers (optional hardening for the code flow) ----

    fun generateCodeVerifier(): String {
        val bytes = ByteArray(32).also(SecureRandom()::nextBytes)
        return base64Url(bytes)
    }

    fun codeChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
        return base64Url(digest)
    }

    private fun base64Url(bytes: ByteArray): String =
        java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

    private fun urlEncode(value: String): String =
        java.net.URLEncoder.encode(value, "UTF-8")
}
