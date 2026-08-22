package com.noctua.ai

/**
 * Predicts tomorrow's readiness score (0..100) from the [WellnessFeatures]
 * vector. Implementations must be synchronous and side-effect free.
 */
fun interface ReadinessForecaster {
    /** Returns null when a prediction cannot be produced (e.g. model missing). */
    fun predictTomorrow(features: WellnessFeatures): Int?
}
