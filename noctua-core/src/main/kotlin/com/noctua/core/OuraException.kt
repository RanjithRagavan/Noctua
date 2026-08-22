package com.noctua.core

import java.io.IOException

/** Typed failure modes surfaced by [OuraClient]. */
sealed class OuraException(message: String, cause: Throwable? = null) : IOException(message, cause) {

    /** HTTP 401/403 — token missing, expired, or scope not granted. */
    class Unauthorized(message: String = "Unauthorized — check token and scopes") : OuraException(message)

    /** HTTP 429 — Oura rate limit (5000 requests / 5 minutes) exceeded. */
    class RateLimited(message: String = "Rate limit exceeded — back off and retry") : OuraException(message)

    /** Any other non-2xx HTTP response. */
    class Http(val code: Int, message: String) : OuraException("HTTP $code: $message")

    /** Connectivity / DNS / TLS failure before an HTTP response was received. */
    class Network(cause: Throwable) : OuraException("Network error: ${cause.message}", cause)

    /** Response body could not be decoded into the expected model. */
    class Serialization(cause: Throwable) : OuraException("Failed to decode Oura response", cause)
}
