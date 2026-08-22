package com.noctua.ai

/**
 * Facade tying the AI layer together:
 *
 * ```kotlin
 * val ai = NoctuaAI(forecaster = ExecuTorchForecaster("/files/readiness_forecaster.pte"))
 * val report = ai.analyze(snapshot)
 * report.forecastedReadiness // e.g. 74
 * report.insights            // ranked, explainable, generated on-device
 * ```
 *
 * Privacy by design: no network access, no analytics, no persistence —
 * biometric data never leaves the device.
 */
class NoctuaAI(
    private val engines: List<InsightEngine> = listOf(HeuristicInsightEngine()),
    private val forecaster: ReadinessForecaster = LinearHeuristicForecaster(),
    private val targetSleepSeconds: Int = FeatureExtractor.DEFAULT_TARGET_SLEEP_SECONDS,
) {

    fun analyze(snapshot: WellnessSnapshot): NoctuaReport {
        val features = FeatureExtractor.extract(snapshot, targetSleepSeconds)

        val insights = engines
            .flatMap { engine -> runCatching { engine.generate(snapshot, features) }.getOrDefault(emptyList()) }
            .distinctBy { it.title }
            .sortedWith(
                compareByDescending<Insight> { it.severity.ordinal }
                    .thenByDescending { it.confidence },
            )

        val forecast = runCatching { forecaster.predictTomorrow(features) }.getOrNull()
            ?: LinearHeuristicForecaster().predictTomorrow(features)

        return NoctuaReport(
            insights = insights,
            forecastedReadiness = forecast,
            features = features,
        )
    }
}
