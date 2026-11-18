package com.noctua.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noctua.example.ui.theme.MintGreen
import com.noctua.example.ui.theme.WarmAmber

/** Makes the actual inference process visible instead of hiding behind "on-device". */
@Composable
fun ExecutionPathCard(
    state: NoctuaUiState,
    onClick: (() -> Unit)? = null,
) {
    val host = state.inferenceHost
    val accent = when (host) {
        InferenceHost.APP_PROCESS -> MaterialTheme.colorScheme.primary
        InferenceHost.AOSP_SERVICE -> MaterialTheme.colorScheme.secondary
        InferenceHost.LINEAR_FALLBACK -> WarmAmber
    }
    val badge = when (host) {
        InferenceHost.APP_PROCESS -> "APP PROCESS"
        InferenceHost.AOSP_SERVICE -> "AOSP NATIVE SERVICE"
        InferenceHost.LINEAR_FALLBACK -> "LINEAR FALLBACK"
    }
    val path = when (host) {
        InferenceHost.APP_PROCESS -> "Noctua app  →  JNI / AAR  →  .pte model"
        InferenceHost.AOSP_SERVICE -> "Noctua app  →  Binder IPC  →  noctua_agent"
        InferenceHost.LINEAR_FALLBACK -> "Noctua app  →  deterministic linear model"
    }
    val description = when (host) {
        InferenceHost.APP_PROCESS ->
            "ExecuTorch runs inside the app process. No AOSP service is being used."
        InferenceHost.AOSP_SERVICE ->
            "ExecuTorch runs in the isolated native daemon on the system image."
        InferenceHost.LINEAR_FALLBACK ->
            "The neural runtime is unavailable; Noctua is using its transparent fallback."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.09f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "INFERENCE EXECUTION PATH",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
                ExecutionBadge(badge, accent)
            }
            Text(path, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            )
            if (host != InferenceHost.LINEAR_FALLBACK) {
                Text(
                    "float32 [1,8]  →  float32 [1,1]   •   no inference network",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = accent,
                )
            }
            if (onClick != null) {
                Text(
                    "View AOSP architecture →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ExecutionBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.16f), shape = MaterialTheme.shapes.small) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}
