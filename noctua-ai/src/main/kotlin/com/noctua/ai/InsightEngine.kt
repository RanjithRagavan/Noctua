package com.noctua.ai

/** Pluggable insight source. Engines are pure functions — no I/O, no clock. */
fun interface InsightEngine {
    fun generate(snapshot: WellnessSnapshot, features: WellnessFeatures): List<Insight>
}
