package com.noctua.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Envelope returned by every Oura v2 multi-document route:
 * `GET /v2/usercollection/{type}` returns `{ "data": [...], "next_token": "..." }`.
 */
@Serializable
data class MultiDocumentResponse<T>(
    val data: List<T> = emptyList(),
    @SerialName("next_token") val nextToken: String? = null,
)

/** `GET /v2/usercollection/personal_info` — scope: `personal`. */
@Serializable
data class PersonalInfo(
    val id: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val height: Float? = null,
    @SerialName("biological_sex") val biologicalSex: String? = null,
)

/** Time-bucketed metric stream embedded in several documents (interval in seconds). */
@Serializable
data class SampleStream(
    val interval: Float? = null,
    val items: List<Float?> = emptyList(),
    val timestamp: String? = null,
)

/** Single heart-rate sample — `GET /v2/usercollection/heartrate`. Scope: `heartrate`. */
@Serializable
data class HeartRateSample(
    val bpm: Int? = null,
    val source: String? = null,
    val timestamp: String? = null,
)

/** OAuth2 token response from `POST https://api.ouraring.com/oauth/token`. */
@Serializable
data class OuraToken(
    @SerialName("token_type") val tokenType: String = "bearer",
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    @SerialName("refresh_token") val refreshToken: String? = null,
    val scope: String? = null,
    /** Epoch millis when the token was obtained — set client-side for expiry math. */
    val obtainedAtEpochMs: Long = 0,
) {
    fun isExpired(nowEpochMs: Long, leewayMs: Long = 60_000): Boolean {
        if (obtainedAtEpochMs <= 0L || expiresIn <= 0L) return false
        return nowEpochMs >= obtainedAtEpochMs + expiresIn * 1000 - leewayMs
    }
}
