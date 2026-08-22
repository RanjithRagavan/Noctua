package com.noctua.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Detailed sleep period — `GET /v2/usercollection/sleep` — scope: `daily`.
 * One document per sleep period (long sleep, late nap, ...), with embedded
 * heart-rate / HRV / movement streams.
 */
@Serializable
data class SleepPeriod(
    val id: String? = null,
    val day: String? = null,
    val type: String? = null,
    @SerialName("bedtime_start") val bedtimeStart: String? = null,
    @SerialName("bedtime_end") val bedtimeEnd: String? = null,
    @SerialName("average_breath") val averageBreath: Float? = null,
    @SerialName("average_heart_rate") val averageHeartRate: Float? = null,
    @SerialName("average_hrv") val averageHrv: Int? = null,
    @SerialName("awake_time") val awakeTime: Int? = null,
    @SerialName("deep_sleep_duration") val deepSleepDuration: Int? = null,
    val efficiency: Int? = null,
    val latency: Int? = null,
    @SerialName("light_sleep_duration") val lightSleepDuration: Int? = null,
    @SerialName("low_battery_alert") val lowBatteryAlert: Boolean? = null,
    @SerialName("lowest_heart_rate") val lowestHeartRate: Int? = null,
    @SerialName("movement_30_sec") val movement30Sec: String? = null,
    val period: Int? = null,
    @SerialName("readiness_score_delta") val readinessScoreDelta: Int? = null,
    @SerialName("rem_sleep_duration") val remSleepDuration: Int? = null,
    @SerialName("restless_periods") val restlessPeriods: Int? = null,
    @SerialName("sleep_phase_5_min") val sleepPhase5Min: String? = null,
    @SerialName("time_in_bed") val timeInBed: Int? = null,
    @SerialName("total_sleep_duration") val totalSleepDuration: Int? = null,
    @SerialName("heart_rate") val heartRate: SampleStream? = null,
    val hrv: SampleStream? = null,
    val readiness: SleepReadinessDetail? = null,
)

@Serializable
data class SleepReadinessDetail(
    @SerialName("activity_balance") val activityBalance: Int? = null,
    @SerialName("body_temperature") val bodyTemperature: Int? = null,
    @SerialName("hrv_balance") val hrvBalance: Int? = null,
    @SerialName("previous_day_activity") val previousDayActivity: Int? = null,
    @SerialName("previous_night") val previousNight: Int? = null,
    @SerialName("recovery_index") val recoveryIndex: Int? = null,
    @SerialName("resting_heart_rate") val restingHeartRate: Int? = null,
    @SerialName("sleep_balance") val sleepBalance: Int? = null,
)

/** Recommended sleep window — `GET /v2/usercollection/sleep_time` — scope: `daily`. */
@Serializable
data class SleepTime(
    val id: String? = null,
    val day: String? = null,
    val status: String? = null,
    val recommendation: String? = null,
    @SerialName("optimal_bedtime") val optimalBedtime: BedtimeWindow? = null,
)

@Serializable
data class BedtimeWindow(
    @SerialName("day_tz") val dayTz: String? = null,
    @SerialName("end_offset") val endOffset: Int? = null,
    @SerialName("start_offset") val startOffset: Int? = null,
)

/** Rest mode block — `GET /v2/usercollection/rest_mode_period` — scope: `daily`. */
@Serializable
data class RestModePeriod(
    val id: String? = null,
    @SerialName("start_day") val startDay: String? = null,
    @SerialName("end_day") val endDay: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val episodes: List<RestModeEpisode> = emptyList(),
)

@Serializable
data class RestModeEpisode(
    val tags: List<String> = emptyList(),
    val timestamp: String? = null,
)
