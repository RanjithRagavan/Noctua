package com.noctua.ai

/**
 * Hand-tuned linear model over the normalized feature vector. Always
 * available, zero dependencies, and transparent — the fallback when no
 * ExecuTorch `.pte` model is bundled, and a sanity baseline for one.
 *
 * Weights were chosen so each feature moves the prediction in the
 * physiologically sensible direction (more debt ↓, higher HRV z ↑, ...).
 */
class LinearHeuristicForecaster : ReadinessForecaster {

    override fun predictTomorrow(features: WellnessFeatures): Int? {
        if (features.daysOfHistory < 3) return null
        val v = features.toVector()
        var score = BASE
        for (i in v.indices) score += WEIGHTS[i] * v[i]
        return score.toInt().coerceIn(0, 100)
    }

    companion object {
        private const val BASE = 78f

        /** Order matches [WellnessFeatures.toVector]. */
        private val WEIGHTS = floatArrayOf(
            -14f, // sleep debt
            10f, // HRV z-score
            8f, // readiness trend
            12f, // readiness mean vs 75
            8f, // sleep score mean vs 75
            -12f, // temperature deviation
            6f, // sleep efficiency
            3f, // steps
        )
    }
}
