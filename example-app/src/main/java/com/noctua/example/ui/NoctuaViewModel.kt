package com.noctua.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noctua.ai.ExecuTorchForecaster
import com.noctua.ai.LinearHeuristicForecaster
import com.noctua.ai.NoctuaAI
import com.noctua.ai.NoctuaReport
import com.noctua.ai.ReadinessForecaster
import com.noctua.ai.WellnessSnapshot
import com.noctua.core.OuraClient
import com.noctua.core.OuraException
import com.noctua.example.demo.DemoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

data class NoctuaUiState(
    val loading: Boolean = true,
    val demoMode: Boolean = true,
    val error: String? = null,
    /** Which forecaster produced the prediction, e.g. "neural · ExecuTorch" or "linear fallback". */
    val forecasterLabel: String = "",
    val snapshot: WellnessSnapshot = WellnessSnapshot(),
    val report: NoctuaReport? = null,
)

class NoctuaViewModel(application: Application) : AndroidViewModel(application) {

    /** Loads the bundled .pte and prefers the ExecuTorch neural forecaster. */
    private val forecasterInfo: Pair<ReadinessForecaster, String> by lazy {
        loadForecaster(application)
    }

    private val ai: NoctuaAI by lazy { NoctuaAI(forecaster = forecasterInfo.first) }

    private val _state = MutableStateFlow(NoctuaUiState())
    val state: StateFlow<NoctuaUiState> = _state.asStateFlow()

    init {
        loadDemo()
    }

    /** Demo mode: deterministic local data, zero network calls. */
    fun loadDemo() {
        _state.update { it.copy(loading = true, error = null) }
        val snapshot = DemoData.snapshot()
        _state.update {
            it.copy(
                loading = false,
                demoMode = true,
                forecasterLabel = forecasterInfo.second,
                snapshot = snapshot,
                report = ai.analyze(snapshot),
            )
        }
    }

    /** Live mode: pull the trailing 21 days from the Oura API and analyze on-device. */
    fun connectWithToken(token: String) {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val client = OuraClient.Builder().token(token).build()
                val start = LocalDate.now().minusDays(21).toString()
                val end = LocalDate.now().toString()
                val snapshot = WellnessSnapshot(
                    readiness = client.dailyReadiness(start, end),
                    sleep = client.dailySleep(start, end),
                    activity = client.dailyActivity(start, end),
                    sleepPeriods = client.sleep(start, end),
                )
                _state.update {
                    it.copy(
                        loading = false,
                        demoMode = false,
                        forecasterLabel = forecasterInfo.second,
                        snapshot = snapshot,
                        report = ai.analyze(snapshot),
                    )
                }
            } catch (e: OuraException.Unauthorized) {
                _state.update { it.copy(loading = false, error = "Token rejected by Oura. Check it and retry.") }
            } catch (e: OuraException) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    /**
     * Copies the bundled ExecuTorch program out of assets (the runtime needs
     * a real file path) and returns the neural forecaster when usable,
     * otherwise the zero-dependency linear fallback.
     */
    private fun loadForecaster(app: Application): Pair<ReadinessForecaster, String> = runCatching {
        val file = File(app.filesDir, MODEL_FILE)
        if (!file.exists()) {
            app.assets.open(MODEL_FILE).use { input ->
                file.outputStream().use { input.copyTo(it) }
            }
        }
        val neural = ExecuTorchForecaster(file.absolutePath)
        if (neural.isAvailable()) {
            neural to "neural · ExecuTorch"
        } else {
            LinearHeuristicForecaster() to "linear fallback"
        }
    }.getOrElse {
        LinearHeuristicForecaster() to "linear fallback"
    }

    companion object {
        private const val MODEL_FILE = "readiness_forecaster.pte"
    }
}
