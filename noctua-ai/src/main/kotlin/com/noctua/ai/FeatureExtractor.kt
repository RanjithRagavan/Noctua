package com.noctua.ai

import com.noctua.core.model.SleepPeriod
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Derived, normalized signals computed from a [WellnessSnapshot].
 * Everything runs on-device; units are documented per field.
 */
data class WellnessFeatures(
    /** Cumulative sleep shortfall vs. target over the trailing 7 days, in hours. */
    val sleepDebtHours: Float,
    /** Z-score of last night's average HRV against the personal baseline. */
    val hrvZScore: Float?,
    /** Slope of readiness scores over the trailing window (points/day). */
    val readinessTrend: Float?,
    /** Mean readiness over the trailing 7 days. */
    val readinessMean7d: Float?,
    /** Mean sleep score over the trailing 7 days. */
    val sleepScoreMean7d: Float?,
    /** Latest nightly temperature deviation in °C, when available. */
    val temperatureDeviation: Float?,
    /** Latest sleep efficiency (0..100). */
    val sleepEfficiency: Int?,
    /** Latest step count. */
    val steps: Int?,
    /** Days of history that fed this computation. */
    val daysOfHistory: Int,
) {
    /** Fixed-length, roughly [-1, 1]-normalized vector for the neural forecaster. */
    fun toVector(): FloatArray = floatArrayOf(
        (sleepDebtHours / 10f).coerceIn(-1f, 1f),
        ((hrvZScore ?: 0f) / 3f).coerceIn(-1f, 1f),
        ((readinessTrend ?: 0f) / 5f).coerceIn(-1f, 1f),
        (((readinessMean7d ?: 75f) - 75f) / 25f).coerceIn(-1f, 1f),
        (((sleepScoreMean7d ?: 75f) - 75f) / 25f).coerceIn(-1f, 1f),
        ((temperatureDeviation ?: 0f) / 2f).coerceIn(-1f, 1f),
        (((sleepEfficiency ?: 85) - 85f) / 15f).coerceIn(-1f, 1f),
        (((steps ?: 8000) - 8000f) / 8000f).coerceIn(-1f, 1f),
    )

    companion object {
        const val VECTOR_SIZE = 8
    }
}

/** Pure-math feature extraction — deterministic and unit-testable. */
object FeatureExtractor {

    const val DEFAULT_TARGET_SLEEP_SECONDS = 8 * 3600

    fun extract(
        snapshot: WellnessSnapshot,
        targetSleepSeconds: Int = DEFAULT_TARGET_SLEEP_SECONDS,
    ): WellnessFeatures {
        val readiness = snapshot.readiness.sortedBy { it.day }
        val sleep = snapshot.sleep.sortedBy { it.day }
        val activity = snapshot.activity.sortedBy { it.day }
        val periods = snapshot.sleepPeriods.sortedBy { it.day }

        return WellnessFeatures(
            sleepDebtHours = sleepDebtHours(periods, targetSleepSeconds),
            hrvZScore = hrvZScore(periods),
            readinessTrend = trend(readiness.mapNotNull { it.score?.toFloat() }),
            readinessMean7d = mean(readiness.takeLast(7).mapNotNull { it.score?.toFloat() }),
            sleepScoreMean7d = mean(sleep.takeLast(7).mapNotNull { it.score?.toFloat() }),
            temperatureDeviation = readiness.lastOrNull()?.temperatureDeviation,
            sleepEfficiency = periods.lastOrNull()?.efficiency,
            steps = activity.lastOrNull()?.steps,
            daysOfHistory = maxOf(readiness.size, sleep.size, activity.size),
        )
    }

    /** Shortfall vs. target across the trailing 7 long-sleep periods, in hours. */
    fun sleepDebtHours(periods: List<SleepPeriod>, targetSleepSeconds: Int): Float {
        val mainSleeps = periods
            .filter { it.type == null || it.type == "long_sleep" }
            .takeLast(7)
        var debtSeconds = 0L
        for (p in mainSleeps) {
            val actual = p.totalSleepDuration ?: continue
            debtSeconds += (targetSleepSeconds - actual)
        }
        return (debtSeconds / 3600f).coerceIn(-24f, 24f)
    }

    /**
     * Z-score of the most recent long-sleep average HRV vs. the trailing
     * 14-night personal baseline (excluding the latest night itself).
     */
    fun hrvZScore(periods: List<SleepPeriod>): Float? {
        val hrvs = periods
            .filter { it.type == null || it.type == "long_sleep" }
            .mapNotNull { it.averageHrv?.toFloat() }
            .takeLast(15)
        if (hrvs.size < 5) return null
        val baseline = hrvs.dropLast(1)
        val mean = baseline.average().toFloat()
        val std = sqrt(baseline.map { (it - mean) * (it - mean) }.average()).toFloat()
        if (std < 1e-3f) return 0f
        return ((hrvs.last() - mean) / std).coerceIn(-5f, 5f)
    }

    /** Least-squares slope (points/day) of a score series. Null if < 3 points. */
    fun trend(values: List<Float>): Float? {
        val v = values.takeLast(7)
        if (v.size < 3) return null
        val n = v.size
        val xs = (0 until n).map { it.toFloat() }
        val xMean = xs.average()
        val yMean = v.average()
        var num = 0.0
        var den = 0.0
        for (i in 0 until n) {
            num += (xs[i] - xMean) * (v[i] - yMean)
            den += (xs[i] - xMean) * (xs[i] - xMean)
        }
        if (abs(den) < 1e-9) return 0f
        return (num / den).toFloat()
    }

    private fun mean(values: List<Float>): Float? =
        if (values.isEmpty()) null else values.average().toFloat()
}
