package com.noctua.ai

import com.noctua.core.model.DailyActivity
import com.noctua.core.model.DailyReadiness
import com.noctua.core.model.DailySleep
import com.noctua.core.model.SleepPeriod

/**
 * The model's view of the world: a chronological window of Oura daily
 * summaries plus the detailed sleep periods used for HRV baselining.
 *
 * Build it from [com.noctua.core.OuraClient] results, or by hand in tests.
 * Lists are expected oldest → newest; [NoctuaAI] normalizes defensively.
 */
data class WellnessSnapshot(
    val readiness: List<DailyReadiness> = emptyList(),
    val sleep: List<DailySleep> = emptyList(),
    val activity: List<DailyActivity> = emptyList(),
    val sleepPeriods: List<SleepPeriod> = emptyList(),
) {
    val latestReadiness: DailyReadiness? get() = readiness.maxByOrNull { it.day.orEmpty() }
    val latestSleep: DailySleep? get() = sleep.maxByOrNull { it.day.orEmpty() }
    val latestActivity: DailyActivity? get() = activity.maxByOrNull { it.day.orEmpty() }
    val isEmpty: Boolean get() = readiness.isEmpty() && sleep.isEmpty() && activity.isEmpty()
}
