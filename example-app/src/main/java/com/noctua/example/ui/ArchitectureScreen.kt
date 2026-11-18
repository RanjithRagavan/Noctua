package com.noctua.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noctua.example.ui.theme.MintGreen

/** AOSP audience view: process boundaries, model contract, and proof commands. */
@Composable
fun ArchitectureScreen(state: NoctuaUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("AOSP execution architecture", style = MaterialTheme.typography.headlineSmall)
            Text(
                "The current build is identified below. The reference path shows where " +
                    "inference moves when Noctua is integrated into a custom Android image.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }

        ExecutionPathCard(state)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("Native service boundary", style = MaterialTheme.typography.titleMedium)
                PlatformStep(
                    "01",
                    "Noctua platform app",
                    "OuraClient + FeatureExtractor + HeuristicInsightEngine. " +
                        "Raw health history and Oura credentials stay here.",
                )
                PlatformStep(
                    "02",
                    "INoctuaAgent.aidl",
                    "Binder IPC carries only eight normalized floats and returns " +
                        "ForecastResult(score, rawScore, inferenceMicros, runtime).",
                )
                PlatformStep(
                    "03",
                    "noctua_agent native daemon",
                    "A separate process loads ExecuTorch's C++ Module, serializes " +
                        "forward() calls, and isolates native memory and crashes.",
                )
                PlatformStep(
                    "04",
                    "Verified system partition",
                    "Model: /system/etc/noctua/readiness_forecaster.pte. " +
                        "The demo daemon has a dedicated SELinux domain and no net_domain.",
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Unchanged model contract", style = MaterialTheme.typography.titleMedium)
                Text(
                    "WellnessFeatures.toVector()\n" +
                        "  → float32[1,8]\n" +
                        "  → readiness_forecaster.pte\n" +
                        "  → float32[1,1] readiness",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    "The same 833-parameter MLP moves across the boundary; " +
                        "the AIDL interface does not change when a backend changes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("From inference to an OS agent", style = MaterialTheme.typography.titleMedium)
                Text(
                    "TRIGGER → SENSE → INFER → DECIDE → ACT → AUDIT",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "JobScheduler or an explicit request triggers work. The native daemon " +
                        "infers; framework policy decides; a local notification suggests recovery. " +
                        "No silent DND, network, or power changes.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("AOSP integration checklist", style = MaterialTheme.typography.titleMedium)
                Text("AIDL  •  ServiceManager  •  Soong  •  init.rc  •  SELinux", fontWeight = FontWeight.SemiBold)
                Text(
                    "The regular APK stays app-local. The AOSP variant uses platform_apis, " +
                        "platform signing, and explicit service access—never blanket untrusted_app access.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Terminal proof on the custom image", style = MaterialTheme.typography.titleMedium)
                Text(
                    "adb shell service check noctua_agent\n" +
                        "adb shell noctua_client\n" +
                        "adb shell noctua_client 0.6 -0.5 -0.3 -0.4 -0.2 0.1 -0.4 0.3",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
private fun PlatformStep(number: String, title: String, detail: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            number,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            )
        }
    }
}
