package com.noctua.example.demo

import com.noctua.core.model.ActivityContributors
import com.noctua.core.model.DailyActivity
import com.noctua.core.model.DailyReadiness
import com.noctua.core.model.DailySleep
import com.noctua.core.model.ReadinessContributors
import com.noctua.core.model.SleepContributors
import com.noctua.core.model.SleepPeriod
import java.time.LocalDate
import kotlin.math.sin
import kotlin.random.Random

/**
 * Deterministic 21-day demo dataset so the app (and its screenshots) work
 * without an Oura account. Replace with real OuraClient results in Connect.
 */
object DemoData {

    fun snapshot(): com.noctua.ai.WellnessSnapshot {
        val random = Random(42)
        val today = LocalDate.now()

        val readiness = mutableListOf<DailyReadiness>()
        val sleep = mutableListOf<DailySleep>()
        val activity = mutableListOf<DailyActivity>()
        val periods = mutableListOf<SleepPeriod>()

        for (i in 21 downTo 1) {
            val day = today.minusDays(i.toLong())
            val wave = sin(i / 3.5)
            val readinessScore = (78 + wave * 10 + random.nextInt(-4, 5))
                .toInt().coerceIn(45, 98)
            val sleepScore = (readinessScore + random.nextInt(-8, 6)).coerceIn(45, 99)
            val hrv = (58 + wave * 8 + random.nextInt(-4, 5)).toInt()
            val totalSleep = ((7.4 + wave * 0.6 + random.nextDouble(-0.5, 0.5)) * 3600).toInt()

            readiness += DailyReadiness(
                id = "demo-readiness-$i",
                day = day.toString(),
                score = readinessScore,
                temperatureDeviation = (random.nextDouble(-0.3, 0.4)).toFloat(),
                contributors = ReadinessContributors(
                    hrvBalance = (readinessScore + random.nextInt(-10, 10)).coerceIn(1, 100),
                    restingHeartRate = (readinessScore + random.nextInt(-10, 10)).coerceIn(1, 100),
                    sleepBalance = (sleepScore + random.nextInt(-10, 10)).coerceIn(1, 100),
                ),
            )
            sleep += DailySleep(
                id = "demo-sleep-$i",
                day = day.toString(),
                score = sleepScore,
                contributors = SleepContributors(
                    deepSleep = (sleepScore + random.nextInt(-12, 12)).coerceIn(1, 100),
                    efficiency = (sleepScore + random.nextInt(-8, 8)).coerceIn(1, 100),
                    totalSleep = (sleepScore + random.nextInt(-10, 10)).coerceIn(1, 100),
                ),
            )
            activity += DailyActivity(
                id = "demo-activity-$i",
                day = day.toString(),
                score = (75 + wave * 12 + random.nextInt(-6, 7)).toInt().coerceIn(40, 99),
                steps = (8500 + wave * 3000 + random.nextInt(-1500, 1500)).toInt().coerceAtLeast(500),
                activeCalories = (350 + wave * 120 + random.nextInt(-60, 60)).toInt().coerceAtLeast(50),
                totalCalories = 2400 + random.nextInt(-150, 150),
                contributors = ActivityContributors(
                    meetDailyTargets = 70 + random.nextInt(-15, 20),
                    trainingVolume = 65 + random.nextInt(-15, 20),
                ),
            )
            periods += SleepPeriod(
                id = "demo-period-$i",
                day = day.toString(),
                type = "long_sleep",
                totalSleepDuration = totalSleep,
                timeInBed = (totalSleep * 1.12).toInt(),
                averageHrv = hrv,
                efficiency = 82 + random.nextInt(-6, 12),
                deepSleepDuration = (totalSleep * 0.18).toInt(),
                remSleepDuration = (totalSleep * 0.22).toInt(),
                lightSleepDuration = (totalSleep * 0.52).toInt(),
                latency = random.nextInt(180, 900),
                lowestHeartRate = 48 + random.nextInt(0, 6),
            )
        }

        return com.noctua.ai.WellnessSnapshot(
            readiness = readiness,
            sleep = sleep,
            activity = activity,
            sleepPeriods = periods,
        )
    }
}
