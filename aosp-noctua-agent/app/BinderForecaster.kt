package com.noctua.example

import android.os.ServiceManager
import com.noctua.ai.ReadinessForecaster
import com.noctua.ai.WellnessFeatures
import com.noctua.aosp.INoctuaAgent

/**
 * Drop-in ReadinessForecaster for an AOSP platform build of Noctua.
 *
 * This uses platform-only ServiceManager APIs and therefore belongs in the
 * AOSP/soong build, not the ordinary Gradle/SDK app variant. Call predictTomorrow
 * from Dispatchers.IO; Binder and model execution are synchronous.
 */
class BinderForecaster : ReadinessForecaster {
    @Volatile
    private var cachedAgent: INoctuaAgent? = null

    fun isAvailable(): Boolean = runCatching {
        ServiceManager.checkService(SERVICE_NAME) != null
    }.getOrDefault(false)

    fun runtimeInfo(): String? = runCatching { agent()?.getRuntimeInfo() }.getOrNull()

    override fun predictTomorrow(features: WellnessFeatures): Int? = runCatching {
        agent()?.forecast(features.toVector())?.readinessScore
    }.getOrElse {
        // A service restart invalidates the cached proxy; the next call resolves it again.
        cachedAgent = null
        null
    }

    private fun agent(): INoctuaAgent? {
        cachedAgent?.let { return it }
        val binder = ServiceManager.waitForService(SERVICE_NAME) ?: return null
        return INoctuaAgent.Stub.asInterface(binder)?.also { cachedAgent = it }
    }

    private companion object {
        const val SERVICE_NAME = "noctua_agent"
    }
}
