package com.noctua.core.api

import com.noctua.core.model.DailyActivity
import com.noctua.core.model.DailyCardiovascularAge
import com.noctua.core.model.DailyReadiness
import com.noctua.core.model.DailyResilience
import com.noctua.core.model.DailySleep
import com.noctua.core.model.DailySpo2
import com.noctua.core.model.DailyStress
import com.noctua.core.model.HeartRateSample
import com.noctua.core.model.MultiDocumentResponse
import com.noctua.core.model.PersonalInfo
import com.noctua.core.model.RestModePeriod
import com.noctua.core.model.RingConfiguration
import com.noctua.core.model.Session
import com.noctua.core.model.SleepPeriod
import com.noctua.core.model.SleepTime
import com.noctua.core.model.Tag
import com.noctua.core.model.Vo2Max
import com.noctua.core.model.Workout
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit mapping of the Oura API v2 user collection routes.
 * Base URL: `https://api.ouraring.com` (routes are absolute from `/v2/...`).
 *
 * Multi-document routes accept `start_date` / `end_date` (yyyy-MM-dd) and return
 * `next_token` for pagination; `heartrate` uses `start_datetime` / `end_datetime`.
 */
interface OuraApi {

    @GET("/v2/usercollection/personal_info")
    suspend fun personalInfo(): PersonalInfo

    @GET("/v2/usercollection/daily_sleep")
    suspend fun dailySleep(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailySleep>

    @GET("/v2/usercollection/daily_sleep/{id}")
    suspend fun dailySleepDocument(@Path("id") id: String): DailySleep

    @GET("/v2/usercollection/daily_readiness")
    suspend fun dailyReadiness(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailyReadiness>

    @GET("/v2/usercollection/daily_readiness/{id}")
    suspend fun dailyReadinessDocument(@Path("id") id: String): DailyReadiness

    @GET("/v2/usercollection/daily_activity")
    suspend fun dailyActivity(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailyActivity>

    @GET("/v2/usercollection/daily_activity/{id}")
    suspend fun dailyActivityDocument(@Path("id") id: String): DailyActivity

    @GET("/v2/usercollection/daily_spo2")
    suspend fun dailySpo2(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailySpo2>

    @GET("/v2/usercollection/daily_stress")
    suspend fun dailyStress(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailyStress>

    @GET("/v2/usercollection/daily_resilience")
    suspend fun dailyResilience(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailyResilience>

    @GET("/v2/usercollection/daily_cardiovascular_age")
    suspend fun dailyCardiovascularAge(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<DailyCardiovascularAge>

    @GET("/v2/usercollection/vO2_max")
    suspend fun vo2Max(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<Vo2Max>

    @GET("/v2/usercollection/sleep")
    suspend fun sleep(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<SleepPeriod>

    @GET("/v2/usercollection/sleep/{id}")
    suspend fun sleepDocument(@Path("id") id: String): SleepPeriod

    @GET("/v2/usercollection/sleep_time")
    suspend fun sleepTime(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<SleepTime>

    @GET("/v2/usercollection/heartrate")
    suspend fun heartrate(
        @Query("start_datetime") startDatetime: String? = null,
        @Query("end_datetime") endDatetime: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<HeartRateSample>

    @GET("/v2/usercollection/workout")
    suspend fun workouts(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<Workout>

    @GET("/v2/usercollection/session")
    suspend fun sessions(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<Session>

    @GET("/v2/usercollection/tag")
    suspend fun tags(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<Tag>

    @GET("/v2/usercollection/enhanced_tag")
    suspend fun enhancedTags(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<Tag>

    @GET("/v2/usercollection/rest_mode_period")
    suspend fun restModePeriods(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<RestModePeriod>

    @GET("/v2/usercollection/ring_configuration")
    suspend fun ringConfigurations(
        @Query("next_token") nextToken: String? = null,
    ): MultiDocumentResponse<RingConfiguration>
}
