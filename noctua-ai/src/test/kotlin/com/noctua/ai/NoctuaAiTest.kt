package com.noctua.ai

import com.noctua.core.model.DailyReadiness
import com.noctua.core.model.DailySleep
import com.noctua.core.model.SleepPeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureExtractorTest {

    private fun sleepPeriod(day: String, totalSeconds: Int, hrv: Int) = SleepPeriod(
        day = day,
        type = "long_sleep",
        totalSleepDuration = totalSeconds,
        averageHrv = hrv,
        efficiency = 90,
    )

    @Test
    fun `sleep debt accumulates against target`() {
        val periods = (1..7).map { day ->
            sleepPeriod("2026-08-%02d".format(day), totalSeconds = 6 * 3600, hrv = 60)
        }
        val debt = FeatureExtractor.sleepDebtHours(periods, 8 * 3600)
        assertEquals(14f, debt, 0.01f) // 2h short x 7 days
    }

    @Test
    fun `sleep debt is negative when oversleeping`() {
        val periods = (1..7).map { day ->
            sleepPeriod("2026-08-%02d".format(day), totalSeconds = 9 * 3600, hrv = 60)
        }
        val debt = FeatureExtractor.sleepDebtHours(periods, 8 * 3600)
        assertEquals(-7f, debt, 0.01f)
    }

    @Test
    fun `hrv z-score detects baseline deviation`() {
        val periods = (1..14).map { day ->
            sleepPeriod("2026-08-%02d".format(day), totalSeconds = 8 * 3600, hrv = 60 + day % 3)
        } + sleepPeriod("2026-08-15", totalSeconds = 8 * 3600, hrv = 35)
        val z = FeatureExtractor.hrvZScore(periods)
        assertNotNull(z)
        assertTrue("expected strongly negative z, got $z", z!! < -1.5f)
    }

    @Test
    fun `hrv z-score needs enough history`() {
        val periods = (1..3).map { day ->
            sleepPeriod("2026-08-%02d".format(day), totalSeconds = 8 * 3600, hrv = 60)
        }
        assertNull(FeatureExtractor.hrvZScore(periods))
    }

    @Test
    fun `trend detects falling readiness`() {
        val t = FeatureExtractor.trend(listOf(90f, 87f, 84f, 81f, 78f))
        assertNotNull(t)
        assertTrue(t!! < -2.5f)
    }

    @Test
    fun `feature vector has fixed size and is normalized`() {
        val snapshot = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 80) },
            sleepPeriods = (1..7).map { sleepPeriod("2026-08-%02d".format(it), 7 * 3600, 60) },
        )
        val features = FeatureExtractor.extract(snapshot)
        val v = features.toVector()
        assertEquals(WellnessFeatures.VECTOR_SIZE, v.size)
        assertTrue(v.all { it in -1f..1f })
    }
}

class HeuristicInsightEngineTest {

    private val engine = HeuristicInsightEngine()

    @Test
    fun `low readiness streak raises alert`() {
        val snapshot = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 55) },
        )
        val insights = engine.generate(snapshot, FeatureExtractor.extract(snapshot))
        assertTrue(insights.any { it.severity == Insight.Severity.ALERT })
    }

    @Test
    fun `high readiness streak is celebrated`() {
        val snapshot = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 90) },
        )
        val insights = engine.generate(snapshot, FeatureExtractor.extract(snapshot))
        assertTrue(insights.any { it.severity == Insight.Severity.POSITIVE })
    }

    @Test
    fun `heavy sleep debt raises warning`() {
        val snapshot = WellnessSnapshot(
            sleep = (1..7).map { DailySleep(day = "2026-08-%02d".format(it), score = 60) },
            sleepPeriods = (1..7).map {
                SleepPeriod(day = "2026-08-%02d".format(it), type = "long_sleep", totalSleepDuration = 5 * 3600)
            },
        )
        val insights = engine.generate(snapshot, FeatureExtractor.extract(snapshot))
        assertTrue(insights.any { it.title.contains("Sleep debt") })
    }

    @Test
    fun `linear forecaster predicts lower readiness with heavy debt`() {
        val forecaster = LinearHeuristicForecaster()
        val good = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 88) },
            sleepPeriods = (1..14).map {
                SleepPeriod(day = "2026-08-%02d".format(it), type = "long_sleep", totalSleepDuration = 8 * 3600, averageHrv = 62)
            },
        )
        val bad = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 62) },
            sleepPeriods = (1..14).map {
                SleepPeriod(day = "2026-08-%02d".format(it), type = "long_sleep", totalSleepDuration = 5 * 3600, averageHrv = 45)
            },
        )
        val fGood = forecaster.predictTomorrow(FeatureExtractor.extract(good))!!
        val fBad = forecaster.predictTomorrow(FeatureExtractor.extract(bad))!!
        assertTrue("good ($fGood) should beat bad ($fBad)", fGood > fBad)
    }

    @Test
    fun `NoctuaAI returns ranked report with forecast`() {
        val snapshot = WellnessSnapshot(
            readiness = (1..7).map { DailyReadiness(day = "2026-08-%02d".format(it), score = 58) },
            sleepPeriods = (1..14).map {
                SleepPeriod(day = "2026-08-%02d".format(it), type = "long_sleep", totalSleepDuration = 6 * 3600, averageHrv = 55)
            },
        )
        val report = NoctuaAI().analyze(snapshot)
        assertTrue(report.insights.isNotEmpty())
        assertNotNull(report.forecastedReadiness)
        // highest severity first
        val severities = report.insights.map { it.severity.ordinal }
        assertEquals(severities, severities.sortedDescending())
    }
}
