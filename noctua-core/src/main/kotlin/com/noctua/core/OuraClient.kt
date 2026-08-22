package com.noctua.core

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.noctua.core.api.OuraApi
import com.noctua.core.api.TokenApi
import com.noctua.core.auth.TokenProvider
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Typed, coroutine-first client for the Oura API v2.
 *
 * ```kotlin
 * val oura = OuraClient.Builder()
 *     .token("YOUR_PERSONAL_ACCESS_TOKEN")
 *     .build()
 *
 * val readiness = oura.dailyReadiness(startDate = "2026-08-14", endDate = "2026-08-21")
 * ```
 *
 * The client automatically follows `next_token` pagination and maps HTTP
 * failures to [OuraException] subtypes.
 */
class OuraClient private constructor(
    private val api: OuraApi,
) {

    // ---- Profile & device ---------------------------------------------------

    suspend fun personalInfo(): PersonalInfo = guard { api.personalInfo() }

    suspend fun ringConfigurations(): List<RingConfiguration> =
        guard { fetchAll { token -> api.ringConfigurations(token) } }

    // ---- Daily summaries ------------------------------------------------------

    suspend fun dailySleep(startDate: String? = null, endDate: String? = null): List<DailySleep> =
        guard { fetchAll { token -> api.dailySleep(startDate, endDate, token) } }

    suspend fun dailyReadiness(startDate: String? = null, endDate: String? = null): List<DailyReadiness> =
        guard { fetchAll { token -> api.dailyReadiness(startDate, endDate, token) } }

    suspend fun dailyActivity(startDate: String? = null, endDate: String? = null): List<DailyActivity> =
        guard { fetchAll { token -> api.dailyActivity(startDate, endDate, token) } }

    suspend fun dailySpo2(startDate: String? = null, endDate: String? = null): List<DailySpo2> =
        guard { fetchAll { token -> api.dailySpo2(startDate, endDate, token) } }

    suspend fun dailyStress(startDate: String? = null, endDate: String? = null): List<DailyStress> =
        guard { fetchAll { token -> api.dailyStress(startDate, endDate, token) } }

    suspend fun dailyResilience(startDate: String? = null, endDate: String? = null): List<DailyResilience> =
        guard { fetchAll { token -> api.dailyResilience(startDate, endDate, token) } }

    suspend fun dailyCardiovascularAge(startDate: String? = null, endDate: String? = null): List<DailyCardiovascularAge> =
        guard { fetchAll { token -> api.dailyCardiovascularAge(startDate, endDate, token) } }

    suspend fun vo2Max(startDate: String? = null, endDate: String? = null): List<Vo2Max> =
        guard { fetchAll { token -> api.vo2Max(startDate, endDate, token) } }

    // ---- Detailed periods & events --------------------------------------------

    suspend fun sleep(startDate: String? = null, endDate: String? = null): List<SleepPeriod> =
        guard { fetchAll { token -> api.sleep(startDate, endDate, token) } }

    suspend fun sleepTime(startDate: String? = null, endDate: String? = null): List<SleepTime> =
        guard { fetchAll { token -> api.sleepTime(startDate, endDate, token) } }

    /** Heart-rate time series. Oura expects ISO-8601 datetimes here, e.g. `2026-08-21T00:00:00Z`. */
    suspend fun heartrate(startDatetime: String? = null, endDatetime: String? = null): List<HeartRateSample> =
        guard { fetchAll { token -> api.heartrate(startDatetime, endDatetime, token) } }

    suspend fun workouts(startDate: String? = null, endDate: String? = null): List<Workout> =
        guard { fetchAll { token -> api.workouts(startDate, endDate, token) } }

    suspend fun sessions(startDate: String? = null, endDate: String? = null): List<Session> =
        guard { fetchAll { token -> api.sessions(startDate, endDate, token) } }

    suspend fun tags(startDate: String? = null, endDate: String? = null): List<Tag> =
        guard { fetchAll { token -> api.tags(startDate, endDate, token) } }

    suspend fun enhancedTags(startDate: String? = null, endDate: String? = null): List<Tag> =
        guard { fetchAll { token -> api.enhancedTags(startDate, endDate, token) } }

    suspend fun restModePeriods(startDate: String? = null, endDate: String? = null): List<RestModePeriod> =
        guard { fetchAll { token -> api.restModePeriods(startDate, endDate, token) } }

    // ---- Internals -------------------------------------------------------------

    /** Follows `next_token` pagination up to [MAX_PAGES] pages. */
    private suspend fun <T> fetchAll(call: suspend (String?) -> MultiDocumentResponse<T>): List<T> {
        val out = ArrayList<T>()
        var token: String? = null
        var pages = 0
        do {
            val page = call(token)
            out += page.data
            token = page.nextToken
            pages++
        } while (token != null && pages < MAX_PAGES)
        return out
    }

    private suspend fun <T> guard(block: suspend () -> T): T = try {
        block()
    } catch (e: HttpException) {
        throw when (e.code()) {
            401, 403 -> OuraException.Unauthorized()
            429 -> OuraException.RateLimited()
            else -> OuraException.Http(e.code(), e.message())
        }
    } catch (e: kotlinx.serialization.SerializationException) {
        throw OuraException.Serialization(e)
    } catch (e: OuraException) {
        throw e
    } catch (e: IOException) {
        throw OuraException.Network(e)
    }

    class Builder {
        private var tokenProvider: TokenProvider? = null
        private var baseUrl: String = DEFAULT_BASE_URL
        private var sandbox: Boolean = false
        private var logging: Boolean = false
        private var connectTimeoutSec: Long = 15
        private var readTimeoutSec: Long = 30

        /** Static bearer token (Personal Access Token or issued access token). */
        fun token(accessToken: String) = apply { tokenProvider = TokenProvider.of(accessToken) }

        /** Dynamic provider (e.g. [com.noctua.core.auth.OAuthTokenProvider] with auto-refresh). */
        fun tokenProvider(provider: TokenProvider) = apply { tokenProvider = provider }

        /** Override the API host (defaults to `https://api.ouraring.com/`). */
        fun baseUrl(url: String) = apply { baseUrl = if (url.endsWith('/')) url else "$url/" }

        /**
         * Route requests at Oura's sandbox (the `/v2/sandbox/usercollection`
         * routes), which return deterministic sample data — handy for demos
         * and tests.
         */
        fun sandbox(enabled: Boolean = true) = apply { sandbox = enabled }

        /** Log request/response headers+body via OkHttp (debug builds only!). */
        fun httpLogging(enabled: Boolean = true) = apply { logging = enabled }

        fun timeouts(connectSec: Long = 15, readSec: Long = 30) = apply {
            connectTimeoutSec = connectSec
            readTimeoutSec = readSec
        }

        fun build(): OuraClient {
            val provider = tokenProvider
                ?: throw IllegalStateException("No token configured — call .token(...) or .tokenProvider(...)")

            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer ${runBlocking { provider.token() }}")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }

            val sandboxInterceptor = Interceptor { chain ->
                val request = chain.request()
                val url = request.url
                if (url.encodedPath.startsWith("/v2/usercollection")) {
                    val rewritten = url.newBuilder()
                        .encodedPath(url.encodedPath.replaceFirst("/v2/", "/v2/sandbox/"))
                        .build()
                    chain.proceed(request.newBuilder().url(rewritten).build())
                } else {
                    chain.proceed(request)
                }
            }

            val http = OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSec, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSec, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .apply {
                    if (sandbox) addInterceptor(sandboxInterceptor)
                    if (logging) {
                        addInterceptor(
                            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY),
                        )
                    }
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(http)
                .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE))
                .build()

            return OuraClient(retrofit.create(OuraApi::class.java))
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://api.ouraring.com/"
        private const val MAX_PAGES = 50
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()

        internal val json: Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
        }

        /** Standalone service for the OAuth2 token endpoint (code exchange / refresh). */
        fun tokenService(baseUrl: String = DEFAULT_BASE_URL): TokenApi =
            Retrofit.Builder()
                .baseUrl(if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/")
                .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE))
                .build()
                .create(TokenApi::class.java)
    }
}
