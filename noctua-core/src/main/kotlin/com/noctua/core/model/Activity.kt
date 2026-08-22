package com.noctua.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /v2/usercollection/daily_activity` — scope: `daily`. */
@Serializable
data class DailyActivity(
    val id: String? = null,
    val day: String? = null,
    val score: Int? = null,
    val timestamp: String? = null,
    @SerialName("active_calories") val activeCalories: Int? = null,
    @SerialName("average_met_minutes") val averageMetMinutes: Float? = null,
    val contributors: ActivityContributors? = null,
    @SerialName("equivalent_walking_distance") val equivalentWalkingDistance: Int? = null,
    @SerialName("high_activity_met_minutes") val highActivityMetMinutes: Int? = null,
    @SerialName("high_activity_time") val highActivityTime: Int? = null,
    @SerialName("inactivity_alerts") val inactivityAlerts: Int? = null,
    @SerialName("low_activity_met_minutes") val lowActivityMetMinutes: Int? = null,
    @SerialName("low_activity_time") val lowActivityTime: Int? = null,
    @SerialName("medium_activity_met_minutes") val mediumActivityMetMinutes: Int? = null,
    @SerialName("medium_activity_time") val mediumActivityTime: Int? = null,
    @SerialName("meters_to_target") val metersToTarget: Int? = null,
    @SerialName("non_wear_time") val nonWearTime: Int? = null,
    @SerialName("resting_time") val restingTime: Int? = null,
    @SerialName("sedentary_met_minutes") val sedentaryMetMinutes: Int? = null,
    @SerialName("sedentary_time") val sedentaryTime: Int? = null,
    val steps: Int? = null,
    @SerialName("target_calories") val targetCalories: Int? = null,
    @SerialName("target_meters") val targetMeters: Int? = null,
    @SerialName("total_calories") val totalCalories: Int? = null,
    @SerialName("class_5_min") val class5Min: String? = null,
    val met: SampleStream? = null,
)

@Serializable
data class ActivityContributors(
    @SerialName("meet_daily_targets") val meetDailyTargets: Int? = null,
    @SerialName("move_every_hour") val moveEveryHour: Int? = null,
    @SerialName("recovery_time") val recoveryTime: Int? = null,
    @SerialName("stay_active") val stayActive: Int? = null,
    @SerialName("training_frequency") val trainingFrequency: Int? = null,
    @SerialName("training_volume") val trainingVolume: Int? = null,
)

/** `GET /v2/usercollection/workout` — scope: `workout`. */
@Serializable
data class Workout(
    val id: String? = null,
    val activity: String? = null,
    val calories: Float? = null,
    val day: String? = null,
    val distance: Float? = null,
    @SerialName("end_datetime") val endDatetime: String? = null,
    val energy: Float? = null,
    val intensity: String? = null,
    val label: String? = null,
    val source: String? = null,
    @SerialName("start_datetime") val startDatetime: String? = null,
    @SerialName("heart_rate") val heartRate: SampleStream? = null,
)

/** Guided / unguided mindfulness session — `GET /v2/usercollection/session` — scope: `session`. */
@Serializable
data class Session(
    val id: String? = null,
    val day: String? = null,
    val type: String? = null,
    val mood: String? = null,
    @SerialName("start_datetime") val startDatetime: String? = null,
    @SerialName("end_datetime") val endDatetime: String? = null,
    @SerialName("heart_rate") val heartRate: SampleStream? = null,
    val hrv: SampleStream? = null,
    @SerialName("motion_count") val motionCount: SampleStream? = null,
)

/** User-entered tag — `GET /v2/usercollection/tag` or `enhanced_tag` — scope: `tag`. */
@Serializable
data class Tag(
    val id: String? = null,
    val day: String? = null,
    val text: String? = null,
    val timestamp: String? = null,
    @SerialName("tag_type_code") val tagTypeCode: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val comment: String? = null,
)

/** Ring hardware metadata — `GET /v2/usercollection/ring_configuration` — scope: `ring_configuration`. */
@Serializable
data class RingConfiguration(
    val id: String? = null,
    val color: String? = null,
    val design: String? = null,
    @SerialName("firmware_version") val firmwareVersion: String? = null,
    @SerialName("hardware_type") val hardwareType: String? = null,
    @SerialName("set_up_at") val setUpAt: String? = null,
    val size: Int? = null,
)
