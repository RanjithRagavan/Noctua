package com.noctua.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /v2/usercollection/daily_sleep` — scope: `daily`. */
@Serializable
data class DailySleep(
    val id: String? = null,
    val day: String? = null,
    val score: Int? = null,
    val timestamp: String? = null,
    val contributors: SleepContributors? = null,
)

@Serializable
data class SleepContributors(
    @SerialName("deep_sleep") val deepSleep: Int? = null,
    val efficiency: Int? = null,
    val latency: Int? = null,
    @SerialName("rem_sleep") val remSleep: Int? = null,
    val restfulness: Int? = null,
    val timing: Int? = null,
    @SerialName("total_sleep") val totalSleep: Int? = null,
)

/** `GET /v2/usercollection/daily_readiness` — scope: `daily`. */
@Serializable
data class DailyReadiness(
    val id: String? = null,
    val day: String? = null,
    val score: Int? = null,
    val timestamp: String? = null,
    @SerialName("temperature_deviation") val temperatureDeviation: Float? = null,
    @SerialName("temperature_trend_deviation") val temperatureTrendDeviation: Float? = null,
    val contributors: ReadinessContributors? = null,
)

@Serializable
data class ReadinessContributors(
    @SerialName("activity_balance") val activityBalance: Int? = null,
    @SerialName("body_temperature") val bodyTemperature: Int? = null,
    @SerialName("hrv_balance") val hrvBalance: Int? = null,
    @SerialName("previous_day_activity") val previousDayActivity: Int? = null,
    @SerialName("previous_night") val previousNight: Int? = null,
    @SerialName("recovery_index") val recoveryIndex: Int? = null,
    @SerialName("resting_heart_rate") val restingHeartRate: Int? = null,
    @SerialName("sleep_balance") val sleepBalance: Int? = null,
)

/** `GET /v2/usercollection/daily_spo2` — scope: `spo2`. */
@Serializable
data class DailySpo2(
    val id: String? = null,
    val day: String? = null,
    val timestamp: String? = null,
    @SerialName("breathing_disturbance_index") val breathingDisturbanceIndex: Float? = null,
    @SerialName("spo2_percentage") val spo2Percentage: Spo2Aggregate? = null,
)

@Serializable
data class Spo2Aggregate(
    val average: Float? = null,
)

/** `GET /v2/usercollection/daily_stress` — scope: `daily`. */
@Serializable
data class DailyStress(
    val id: String? = null,
    val day: String? = null,
    val timestamp: String? = null,
    @SerialName("stress_high") val stressHigh: Int? = null,
    @SerialName("recovery_high") val recoveryHigh: Int? = null,
    @SerialName("day_summary") val daySummary: String? = null,
)

/** `GET /v2/usercollection/daily_resilience` — scope: `daily`. */
@Serializable
data class DailyResilience(
    val id: String? = null,
    val day: String? = null,
    val timestamp: String? = null,
    val level: String? = null,
    val contributors: ResilienceContributors? = null,
)

@Serializable
data class ResilienceContributors(
    @SerialName("sleep_recovery") val sleepRecovery: Float? = null,
    @SerialName("daytime_recovery") val daytimeRecovery: Float? = null,
    val stress: Float? = null,
)

/** `GET /v2/usercollection/vO2_max` — scope: `heart_health`. */
@Serializable
data class Vo2Max(
    val id: String? = null,
    val day: String? = null,
    val timestamp: String? = null,
    @SerialName("vo2_max") val vo2Max: Float? = null,
)

/** `GET /v2/usercollection/daily_cardiovascular_age` — scope: `heart_health`. */
@Serializable
data class DailyCardiovascularAge(
    val id: String? = null,
    val day: String? = null,
    val timestamp: String? = null,
    @SerialName("vascular_age") val vascularAge: Float? = null,
)
