package com.noctua.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noctua.ai.NoctuaAI
import com.noctua.ai.NoctuaReport
import com.noctua.ai.WellnessSnapshot
import com.noctua.core.OuraClient
import com.noctua.core.OuraException
import com.noctua.example.demo.DemoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class NoctuaUiState(
    val loading: Boolean = true,
    val demoMode: Boolean = true,
    val error: String? = null,
    val snapshot: WellnessSnapshot = WellnessSnapshot(),
    val report: NoctuaReport? = null,
)

class NoctuaViewModel : ViewModel() {

    private val ai = NoctuaAI()

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
}
