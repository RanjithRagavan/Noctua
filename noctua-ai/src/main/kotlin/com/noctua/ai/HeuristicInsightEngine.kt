package com.noctua.ai

import kotlin.math.abs

/**
 * Deterministic, always-available baseline engine. Encodes evidence-informed
 * heuristics (sleep debt accumulation, HRV baseline deviation, temperature
 * strain flags) as transparent rules — every insight is explainable from the
 * features that triggered it, which matters for user trust and for auditing.
 */
class HeuristicInsightEngine : InsightEngine {

    override fun generate(snapshot: WellnessSnapshot, features: WellnessFeatures): List<Insight> {
        val out = mutableListOf<Insight>()

        readinessAlerts(features, out)
        hrvDeviation(features, out)
        sleepDebt(features, out)
        temperatureStrain(features, out)
        sleepEfficiency(features, out)
        activityBalance(features, out)

        if (out.isEmpty() && !snapshot.isEmpty) {
            out += Insight(
                category = Insight.Category.MILESTONE,
                severity = Insight.Severity.POSITIVE,
                title = "All systems nominal",
                body = "Sleep, recovery and activity markers are all within your personal " +
                    "baselines. Great day to push a little harder.",
                confidence = 70,
            )
        }
        return out
    }

    private fun readinessAlerts(f: WellnessFeatures, out: MutableList<Insight>) {
        val mean = f.readinessMean7d ?: return
        val trend = f.readinessTrend
        when {
            mean < 65 -> out += Insight(
                Insight.Category.RECOVERY, Insight.Severity.ALERT,
                "Sustained low readiness",
                "Your 7-day readiness average is ${mean.toInt()}. Prioritize recovery: " +
                    "lighter training, earlier bedtime, and hydration.",
                confidence = 85,
            )
            trend != null && trend <= -3f -> out += Insight(
                Insight.Category.RECOVERY, Insight.Severity.WARNING,
                "Readiness trending down",
                "Readiness is falling ~${abs(trend).toInt()} pts/day. Consider a deload day " +
                    "before the dip compounds.",
                confidence = 78,
            )
            mean >= 85 -> out += Insight(
                Insight.Category.RECOVERY, Insight.Severity.POSITIVE,
                "Peak recovery window",
                "7-day readiness average is ${mean.toInt()} — you're well recovered. " +
                    "Ideal conditions for high-intensity training.",
                confidence = 82,
            )
        }
    }

    private fun hrvDeviation(f: WellnessFeatures, out: MutableList<Insight>) {
        val z = f.hrvZScore ?: return
        when {
            z <= -1.5f -> out += Insight(
                Insight.Category.STRAIN, Insight.Severity.WARNING,
                "HRV below your baseline",
                "Last night's HRV was ${"%.1f".format(-z)}σ below your 14-night baseline — " +
                    "a classic early sign of strain or incomplete recovery.",
                confidence = 80,
            )
            z >= 1.5f -> out += Insight(
                Insight.Category.RECOVERY, Insight.Severity.POSITIVE,
                "HRV rebound",
                "HRV is ${"%.1f".format(z)}σ above baseline. Your autonomic system is " +
                    "bouncing back nicely.",
                confidence = 75,
            )
        }
    }

    private fun sleepDebt(f: WellnessFeatures, out: MutableList<Insight>) {
        when {
            f.sleepDebtHours >= 2f -> out += Insight(
                Insight.Category.SLEEP, Insight.Severity.WARNING,
                "Sleep debt accumulating",
                "You've banked ${"%.1f".format(f.sleepDebtHours)}h less sleep than target " +
                    "over the past week. One 60–90 min earlier night repays the bulk of it.",
                confidence = 88,
            )
            f.sleepDebtHours <= -1f -> out += Insight(
                Insight.Category.SLEEP, Insight.Severity.POSITIVE,
                "Sleep surplus",
                "You're ahead of target by ${"%.1f".format(-f.sleepDebtHours)}h this week. " +
                    "Banked sleep is protective against the odd short night.",
                confidence = 72,
            )
        }
    }

    private fun temperatureStrain(f: WellnessFeatures, out: MutableList<Insight>) {
        val dev = f.temperatureDeviation ?: return
        if (dev >= 0.5f) {
            out += Insight(
                Insight.Category.STRAIN, Insight.Severity.ALERT,
                "Elevated body temperature",
                "Nightly temperature deviated +${"%.1f".format(dev)}°C from baseline. " +
                    "Often precedes illness — consider rest and fluids today.",
                confidence = 84,
            )
        }
    }

    private fun sleepEfficiency(f: WellnessFeatures, out: MutableList<Insight>) {
        val eff = f.sleepEfficiency ?: return
        if (eff < 80) {
            out += Insight(
                Insight.Category.SLEEP, Insight.Severity.INFO,
                "Restless night",
                "Sleep efficiency was $eff%. Caffeine after 2pm, late meals, and bright " +
                    "screens are the usual suspects.",
                confidence = 65,
            )
        }
    }

    private fun activityBalance(f: WellnessFeatures, out: MutableList<Insight>) {
        val steps = f.steps ?: return
        if (steps < 4_000) {
            out += Insight(
                Insight.Category.ACTIVITY, Insight.Severity.INFO,
                "Low movement day",
                "Only $steps steps yesterday. A 20-minute walk meaningfully improves " +
                    "tonight's deep sleep share.",
                confidence = 60,
            )
        }
    }
}
