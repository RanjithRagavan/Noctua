package com.noctua.ai

/**
 * ExecuTorch-backed neural forecaster. Loads a `.pte` program (exported from
 * PyTorch — see `model/export_readiness_forecaster.py`) and runs it fully
 * on-device via the ExecuTorch Android runtime.
 *
 * The runtime is accessed **reflectively**: apps that ship
 * `org.pytorch:executorch-android` get neural inference; everyone else gets
 * a clean `null` and [NoctuaAI] falls back to [LinearHeuristicForecaster].
 * This keeps `noctua-ai` pure-JVM and unit-testable.
 *
 * Contract of the exported model:
 *   input  : float32 tensor, shape [1, 8]  (see [WellnessFeatures.toVector])
 *   output : float32 tensor, shape [1, 1]  — readiness in [0, 100]
 */
class ExecuTorchForecaster(
    private val modelPath: String,
) : ReadinessForecaster {

    private val module: Any? by lazy { loadModule(modelPath) }

    /** True when the ExecuTorch runtime and the .pte file are both present. */
    fun isAvailable(): Boolean = module != null

    override fun predictTomorrow(features: WellnessFeatures): Int? {
        val m = module ?: return null
        return runCatching {
            val tensorClass = Class.forName("org.pytorch.executorch.Tensor")
            val evalueClass = Class.forName("org.pytorch.executorch.EValue")

            val tensor = tensorClass
                .getMethod("fromBlob", FloatArray::class.java, LongArray::class.java)
                .invoke(null, features.toVector(), longArrayOf(1, WellnessFeatures.VECTOR_SIZE.toLong()))

            val input = evalueClass.getMethod("from", tensorClass).invoke(null, tensor)

            val forward = m.javaClass.getMethod("forward", Array<Any>::class.java)
            @Suppress("UNCHECKED_CAST")
            val outputs = forward.invoke(m, arrayOf(input) as Array<Any>) as Array<Any>

            val outTensor = evalueClass.getMethod("toTensor").invoke(outputs[0])
            val data = tensorClass.getMethod("getDataAsFloatArray").invoke(outTensor) as FloatArray

            data.firstOrNull()?.toInt()?.coerceIn(0, 100)
        }.getOrNull()
    }

    private fun loadModule(path: String): Any? = runCatching {
        val file = java.io.File(path)
        if (!file.exists()) return null
        val moduleClass = Class.forName("org.pytorch.executorch.Module")
        moduleClass.getMethod("load", String::class.java).invoke(null, file.absolutePath)
    }.getOrNull()
}
