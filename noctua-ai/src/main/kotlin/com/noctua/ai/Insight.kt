package com.noctua.ai

/**
 * A single piece of guidance produced on-device.
 */
data class Insight(
    val category: Category,
    val severity: Severity,
    val title: String,
    val body: String,
    /** 0..100 — how strongly the data supports this insight. */
    val confidence: Int,
) {
    enum class Category { SLEEP, RECOVERY, ACTIVITY, STRAIN, MILESTONE }
    enum class Severity { INFO, POSITIVE, WARNING, ALERT }
}

/** Everything Noctua computed for the current window. */
data class NoctuaReport(
    val insights: List<Insight>,
    /** Predicted readiness score for tomorrow (0..100), or null when not computable. */
    val forecastedReadiness: Int?,
    val features: WellnessFeatures,
)
